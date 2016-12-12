(ns eponai.common.ui.navbar
  (:require
    [eponai.common.ui.common :as common]
    [om.dom :as dom]
    [om.next :as om :refer [defui]]
    [taoensso.timbre :refer [debug error]]))

(defn cart-dropdown [{:keys [cart/items cart/price]}]
  (dom/div #js {:className "cart-container dropdown-pane"}
    (dom/div nil
      (apply dom/ul #js {:className "cart menu vertical"}
             (map (fn [i]
                    (dom/li nil
                            (dom/div #js {:className "row collapse align-middle content-item"}
                              (dom/div #js {:className "columns small-2"}
                                (dom/div #js {:className "photo-container"}
                                  (dom/div #js {:className "photo square thumbnail" :style #js {:backgroundImage (str "url(" (:item/img-src i) ")")}})))
                              (dom/div #js {:className "columns small-10"}
                                (dom/div #js {:className "content-item-title-section"}
                                  (dom/small #js {:className "name"} (:item/name i)))
                                (dom/div #js {:className "content-item-subtitle-section"}
                                  (dom/span #js {:className "price"}
                                            (common/two-decimal-price (:item/price i))))))))
                  (take 3 items)))

      (dom/div #js {:className "callout nude"}
        (when (< 3 (count items))
          (dom/small nil (str "You have " (- (count items) 3) " more item(s) in your bag")))
        (dom/h5 nil "Total: " (dom/strong nil (common/two-decimal-price price))))
      (dom/a #js {:className "button expanded"
                  :href      "/checkout"} "View My Bag"))))

(defn user-dropdown [component user]
  (dom/div #js {:className "dropdown-pane"}
    (dom/ul #js {:className "menu vertical"}
            (dom/li nil
                    (dom/a #js {:onClick #(om/transact! component `[(session/signout) :query/auth])}
                           "Sign Out")))))

(defui Navbar
  static om/IQuery
  (query [_]
    [{:query/cart [:cart/price
                   {:cart/items [:item/price
                                 :item/img-src
                                 :item/name]}]}
     :query/auth])
  Object
  #?(:cljs
     (open-signin [this]
                  (debug "Open signin")
             (let [{:keys [lock]} (om/get-state this)
                   current-url js/window.location.href
                   options (clj->js {:connections        ["facebook" "google-oauth2"]
                                     :callbackURL        "http://localhost:3000/auth"
                                     :authParams         {:scope "openid email user_friends"
                                                          :connectionScopes   {"facebook" ["email" "public_profile" "user_friends"]}
                                                          :state current-url}
                                     :primaryColor       "#9A4B4F"
                                     :dict               {:title "SULO"}
                                     :icon ""
                                     ;:container "modal"
                                     })]
               (.socialOrMagiclink lock options))))

  (componentDidUpdate [this prev-props prev-state]
    (debug "Did update: " prev-state)
    #?(:cljs
       (let [{:keys [signin-open?]} (om/get-state this)]
         (when signin-open?
           (.open-signin this)))))
  (initLocalState [_]
    {:cart-open? false})
  (componentWillUnmount [this]
    (let [{:keys [lock]} (om/get-state this)]
      (.close lock)))
  (componentDidMount [this]
    #?(:cljs
       (when js/Auth0LockPasswordless
         (let [lock (new js/Auth0LockPasswordless "JMqCBngHgOcSYBwlVCG2htrKxQFldzDh" "sulo.auth0.com")]
           (om/update-state! this assoc :lock lock)))))

  (render [this]
    (let [{:keys [cart-open? signin-open?]} (om/get-state this)
          {:keys [query/cart query/auth]} (om/props this)]
      (debug "Got auth: " auth)
      (dom/div #js {:id "sulo-navbar"}
        (dom/nav #js {:className "navbar top-bar"}
                 (dom/div #js {:className "top-bar-left"}
                   (dom/ul #js {:className "menu"}
                           (dom/li nil
                                   (dom/a #js {:className "navbar-brand"
                                               :href      "/"}
                                          "Sulo"))
                           (dom/li nil
                                   (dom/input #js {:type "text"
                                                   :placeholder "Search items or stores"}))))

                 (dom/div #js {:className "top-bar-right"}
                   (dom/ul #js {:className "menu"}
                           (if (some? auth)
                             (dom/li #js {:className "user-profile menu-dropdown"}
                                     (dom/a nil "You")
                                     (user-dropdown this cart))
                             (dom/li nil
                                     (dom/a #js {:onClick #(do
                                                            #?(:cljs
                                                               (.open-signin this)))} "Sign in")))
                           (dom/li #js {:className "menu-dropdown"}
                                   (dom/a #js {:href "/checkout"
                                               ;:onClick #(om/update-state! this update :cart-open? not)
                                               }
                                          (dom/span nil (common/two-decimal-price (:cart/price cart)))
                                          (dom/i #js {:className "fa fa-shopping-cart fa-fw"}))
                                   (cart-dropdown cart)))

                   ;(when cart-open?)
                   ))
        (when signin-open?
          (common/modal {:size "tiny"
                         :on-close #(om/update-state! this assoc :signin-open? false)} (dom/div #js {:id "modal"})))
        ))))

(def ->Navbar (om/factory Navbar))