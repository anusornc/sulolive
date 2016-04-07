(ns eponai.web.parser.mutate
  (:require [eponai.common.database.transact :as t]
            [eponai.common.parser :refer [mutate]]
            [eponai.client.parser.mutate]
            [datascript.core :as d]
            [taoensso.timbre :refer-macros [info debug error trace]]))

;; ################ Local mutations ####################
;; Local mutations goes here. These are specific to the
;; app running on this platform.
;; Remote mutations should be defined in:
;;     eponai.client.parser.mutate

;;---------------- Modal show/hide
(defmethod mutate 'ui.modal/show
  [{:keys [state]} _ _]
  {:action #(t/transact state [{:ui/singleton               :ui.singleton/modal
                                 :ui.singleton.modal/visible true}])})

(defmethod mutate 'ui.modal/hide
  [{:keys [state]} _ _]
  {:action #(t/transact state [{:ui/singleton               :ui.singleton/modal
                                 :ui.singleton.modal/visible false}])})

;;------------ Profile menu show/hide
(defmethod mutate 'ui.menu/show
  [{:keys [state]} _ _]
  {:action #(t/transact state [{:ui/singleton               :ui.singleton/menu
                                 :ui.singleton.menu/visible true}])})

(defmethod mutate 'ui.menu/hide
  [{:keys [state]} _ _]
  {:action #(t/transact state [{:ui/singleton               :ui.singleton/menu
                                 :ui.singleton.menu/visible false}])})

(defmethod mutate 'ui.loader/show
  [{:keys [state]} _ _]
  {:action #(t/transact state [{:ui/singleton                :ui.singleton/loader
                                 :ui.singleton.loader/visible true}])})

(defmethod mutate 'ui.loader/hide
  [{:keys [state]} _ _]
  {:action #(t/transact state [{:ui/singleton                :ui.singleton/loader
                                 :ui.singleton.loader/visible false}])})

(defmethod mutate 'project/set-active-uuid
  [{:keys [state]} _ {:keys [project-dbid]}]
  {:action #(let [project-uuid (:project/uuid (d/entity (d/db state) project-dbid))]
             (t/transact state [{:ui/component              :ui.component/project
                                 :ui.component.project/uuid project-uuid}]))})

(defmethod mutate 'project/select-tab
  [{:keys [state]} _ {:keys [selected-tab]}]
  {:action #(t/transact state [{:ui/component             :ui.component/project
                                :ui.component.project/selected-tab selected-tab}])})

(defmethod mutate 'ui.component.project/clear
  [{:keys [state]} _ _]
  {:action #(t/transact state
                         [[:db.fn/retractAttribute [:ui/component :ui.component/project] :ui.component.project/uuid]])})

(defmethod mutate 'transactions.filter/update
  [{:keys [state]} _ {:keys [filter]}]
  {:action #(t/transact state
                        [{:ui/component                     :ui.component/transactions
                          :ui.component.transactions/filter filter}])})

(defmethod mutate 'transactions.filter/clear
  [{:keys [state]} _ _]
  {:action #(t/transact state
                         [[:db.fn/retractAttribute [:ui/component :ui.component/transactions] :ui.component.transactions/filter]])})

(defmethod mutate 'transactions/deselect
  [{:keys [state]} _ _]
  {:action #(t/transact-one state [:db.fn/retractAttribute
                                   [:ui/component :ui.component/transactions]
                                   :ui.component.transactions/selected-transaction])})

(defmethod mutate 'transactions/select-transaction
  [{:keys [state]} _ {:keys [transaction-dbid]}]
  {:action #(t/transact-one state
                            {:ui/component                                   :ui.component/transactions
                             :ui.component.transactions/selected-transaction transaction-dbid})})
