(ns eponai.server.ui.root
  (:require
    [om.dom :as dom]
    [om.next :as om :refer [defui]]
    [eponai.common.ui.router :as router]
    [eponai.server.ui.common :as common]
    [taoensso.timbre :refer [debug]]))

(defui Root
  Object
  (render [this]
    (let [{:keys [release? ::app-html route cljs-build-id]} (om/props this)]
      (debug "app-html: " app-html)
      (debug "ROUTE: " route)
      (dom/html
        {:lang "en"}
        (apply dom/head nil (common/head {:release? release?
                                          :cljs-build-id cljs-build-id}))
        (dom/body
          nil
          (dom/div #js {:height "100%" :id router/dom-app-id}
            app-html)

          (common/red5pro-script-tags release?)
          ;; (common/auth0-lock-passwordless release?)
          (common/auth0-lock release?)


          (when release?
            (dom/script {:src       "https://cdn.greta.io/greta.min.js"
                         :type      common/text-javascript
                         :id        "gretaScript"
                         :data-ac   "b554c0b026bb448362dfe657846bf982"
                         ;; Using data-lazy right now because we have etsy images on our site
                         ;; which doesn't have CORS set up (for good reason).
                         :data-lazy "true"}))
          ;(dom/script {:src "https://cdn.auth0.com/js/lock/10.6/lock.min.js"})

          (when  (= route :coming-soon)
            [(dom/script {:src "https://code.jquery.com/jquery-1.11.0.min.js"})
             ;(common/inline-javascript ["window.jQuery || document.write('<scr' + 'ipt src=\"https://code.jquery.com/jquery-1.11.0.min.js\"><\\/sc' + 'ript>')"])
             (common/inline-javascript ["window.$kol_jquery = window.jQuery"])
             (when release?
               (dom/script {:src "https://kickoffpages-kickofflabs.netdna-ssl.com/widgets/1.9.4/kol_any_form.js"}))
             (when release?
               (dom/script {:src "https://kickoffpages-kickofflabs.netdna-ssl.com/w/89256/144137.js"}))])

          (dom/script {:src "https://js.stripe.com/v2/"
                       :type common/text-javascript})
          (dom/script {:src "https://js.stripe.com/v3/"
                       :type common/text-javascript})


          (dom/script {:src "https://maps.googleapis.com/maps/api/js?key=AIzaSyB8bKA0NO74KlYr5dpoJgM_k6CvtjV8rFQ&libraries=places"})

          (dom/script {:src  (common/budget-js-path cljs-build-id)
                       :type common/text-javascript})

          (when-not (= cljs-build-id "devcards")
            (common/inline-javascript ["env.web.main.runsulo()"])))))))
