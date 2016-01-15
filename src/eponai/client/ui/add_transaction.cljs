(ns eponai.client.ui.add_transaction
  (:require [om.next :as om :refer-macros [defui]]
            [eponai.client.ui :refer-macros [style opts]]
            [eponai.client.ui.datepicker :refer [->Datepicker]]
            [eponai.client.ui.tag :as tag]
            [eponai.common.format :as format]
            [sablono.core :refer-macros [html]]
            [cljsjs.pikaday]
            [cljsjs.moment]
            [garden.core :refer [css]]
            [datascript.core :as d]))

(defn node [name on-change opts & children]
  (apply vector
         name
         (merge {:on-change on-change} opts)
         children))

(defn input [on-change opts]
  (node :input on-change opts))

(defn select [on-change opts & children]
  (apply node :select on-change opts children))

(defn on-change [this k]
  #(om/update-state! this assoc k (.-value (.-target %))))

(defn new-input-tag! [this name]
  (let [id (random-uuid)]
    (om/update-state!
      this
      (fn [state]
        (cond-> state
                true (assoc :input-tag "")

                (not (some #(= (:tag/name %) name)
                           (:input-tags state)))
                (update :input-tags conj
                        (assoc
                          (tag/tag-props
                            name
                            #(om/update-state!
                              this update :input-tags
                              (fn [tags]
                                (into []
                                      (remove (fn [{:keys [::tag-id]}] (= id tag-id)))
                                      tags))))
                          ::tag-id id)))))))

(defn on-add-tag-key-down [this input-tag]
  (fn [key]
    (when (and (= 13 (.-keyCode key))
               (seq (.. key -target -value)))
      (.preventDefault key)
      (new-input-tag! this input-tag))))

(defui AddTransaction
  static om/IQuery
  (query [_]
    [{:query/all-currencies [:currency/code]}
     {:query/all-budgets [:budget/uuid
                          :budget/name]}])
  Object
  (initLocalState [this]
    (let [{:keys [query/all-currencies
                  query/all-budgets]} (om/props this)]
      {:input-date (js/Date.)
       :input-tags []
       :input-currency (-> all-currencies
                           first
                           :currency/code)
       :input-budget (-> all-budgets
                         first
                         :budget/uuid)}))
  (render
    [this]
    (let [{:keys [query/all-currencies
                  query/all-budgets]} (om/props this)
          {:keys [input-amount input-currency input-title input-date
                  input-tags input-tag input-budget]}
          ;; merging state with props, so that we can test the states
          ;; with devcards
          (merge (om/props this)
                 (om/get-state this))]
      (println "budgets: " all-budgets)
      (html
        [:div#add-transaction-modal
         {:class "panel panel-default"}
         [:div.panel-heading
          "Add transaction"]
         [:div
          {:class "form-group panel-body"}
          [:label.form-control-static
           {:for "budget-input"}
           "Sheet:"]

          [:select.form-control#budget-input
           {:on-change (on-change this :input-budget)
            :type      "text"
            :default-value     input-budget}
           (for [budget all-budgets]
             [:option
              (opts {:value (:budget/uuid budget)
                     :key   [(:budget/uuid budget)]})
              (or (:budget/name budget) "Untitled")])]

          [:label.form-control-static
           {:for "amount-input"}
           "Amount:"]
          ;; Input amount with currency
          [:div
           (opts {:style {:display        "flex"
                          :flex-direction "row"
                          :justify-content "stretch"
                          :max-width "100%"}})
           [:input#amount-input
            (opts {:type        "number"
                   :placeholder "0.00"
                   :min         "0"
                   :value       input-amount
                   :class       "form-control"
                   :style       {:width        "80%"
                                 :margin-right "0.5em"}
                   :on-change   (on-change this :input-amount)})]

           [:select
            (opts {:class         "form-control"
                   :on-change     (on-change this :input-currency)
                   :default-value input-currency
                   :style         {:width "20%"}})
            (for [{:keys [currency/code]} all-currencies]
              [:option
               (opts {:value (name code)
                      :key   [code]})
               (name code)])]]

          [:label.form-control-static
           {:for "title-input"}
           "Title:"]

          [:input.form-control#title-input
           {:on-change (on-change this :input-title)
            :type      "text"
            :value     input-title}]

          [:label.form-control-static
           {:for "date-input"}
           "Date:"]

          ; Input date with datepicker

          [:div#date-input
           (->Datepicker
             (opts {:value     input-date
                    :on-change #(om/update-state!
                                 this
                                 assoc
                                 :input-date
                                 %)}))]

          [:label.form-control-static
           {:for "tags-input"}
           "Tags:"]

          [:div
           (opts {:style {:display "flex"
                          :flex-direction "column"
                          :justify-content "flex-start"}})
           [:input.form-control#tags-input
            (opts {:on-change   (on-change this :input-tag)
                   :type        "text"
                   :value       input-tag
                   :on-key-down (on-add-tag-key-down this input-tag)})]

           [:div.form-control-static
            (for [tag input-tags]
              (tag/->Tag
                (assoc tag :key (::tag-id tag))))]]

          [:button
           (opts {:style    {:align-self "center"}
                  :class    "btn btn-default btn-lg"
                  :type     "submit"
                  :on-click #(om/transact!
                              this
                              `[(transaction/create
                                  ~(let [state (om/get-state this)]
                                     (-> state
                                         (assoc :input-date (format/date->ymd-string (:input-date state)))
                                         (assoc :input-uuid (d/squuid))
                                         (assoc :input-created-at (.getTime (js/Date.)))
                                         (assoc :input-currency input-currency)
                                         (assoc :input-budget input-budget)
                                         (dissoc :input-tag)
                                         (update :input-tags
                                                 (fn [tags]
                                                   (map :tag/name tags))))))
                                :query/all-budgets])})
           "Save"]]]))))

(def ->AddTransaction (om/factory AddTransaction))
