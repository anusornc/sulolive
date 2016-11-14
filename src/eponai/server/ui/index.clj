(ns eponai.server.ui.index
  (:require [om.next :as om :refer [defui]]
            [om.dom :as dom]
            [clojure.string :as str]
            [eponai.server.ui.common :as common :refer [text-javascript]]))

(defn iubenda-script-code []
  ["var _iub = _iub || [];"
      "_iub.csConfiguration = {"
      "  cookiePolicyId: 7944779,"
      "  siteId: 640904,"
      "  lang: \"en\","
      "  priorConsent: false"
      "};"])

(defn document-ready-code []
  ["$(document).foundation();"
   "function onSubscribe(elementId) {"
   "  document.getElementById('subscribe-intro').innerHTML = \"<small><i class='fa fa-spinner fa-spin'></small>\";"
   "}"
   ""
   "$(document).ready(function() {"
   "  $('#subscribe-form').submit(function(e) {"
   "    e.preventDefault();"
   "    var dataString = $(this).serialize();"
   "    $.ajax({"
   "      type: 'POST',"
   "      url: '/newsletter/subscribe',"
   "      data: dataString,"
   "      error: function(response) {"
   "        document.getElementById('subscribe-intro').innerHTML = \"<small>\" + JSON.parse(response.responseText).message + \"</small>\";"
   "      },"
   "      success: function(response) {"
   "        document.getElementById('subscribe-intro').innerHTML = \"<small>\" + JSON.parse(response).message + \"</small>\";"
   "      }"
   "    });"
   "  });"
   "});"])

(def intro-message-text
  "When your lifestyle is not following traditional patterns, there's no reason you shouldn't be able to track your money comfortably. Live your nomadic life and travel in peace while tracking your money anytime, anywhere, in any currency.")

(defui Index
  Object
  (render [this]
    (let [{:keys [release?]} (om/props this)]
      (dom/html
        {:lang "en"}
        (apply dom/head nil
               (-> (common/head release?)
                   (conj (common/inline-javascript (iubenda-script-code)))
                   (conj (dom/script {:type    text-javascript
                                      :src     "//cdn.iubenda.com/cookie_solution/safemode/iubenda_cs.js"
                                      :charset "utf-8"
                                      :async   true}))))
        (dom/body
          nil
          (dom/div {:id "landing-page"}
            (dom/div {:id "header"}
              (dom/nav {:className "top-bar"}
                       (dom/div {:className "top-bar-title"}
                         (dom/a {:className "navbar-brand"
                                 :href      ""}
                                (dom/strong nil "jourmoney")))
                       (dom/span {:data-responsive-toggle "responsive-menu"
                                  :data-hide-for "medium"
                                  :className "top-bar-title float-right show-for-small-only"}
                                 (dom/button {:className "menu-icon" :type "button" :data-toggle true}))
                       (dom/div {:className "top-bar-right"
                                 :id "responsive-menu"}
                         (dom/ul
                           {:className "menu"}
                           (dom/li nil
                                   (dom/a {:href "#at-a-glance"} "Features"))
                           (dom/li nil (dom/a {:href "#pay-what-you-want"} "Pricing"))
                           (dom/li nil (dom/a {:href "/signup"} (dom/strong nil "Sign In / Sign Up"))))))
              (dom/div {:className "intro"}
                (dom/div {:className "row align-center column small-12 medium-8 large-5"}
                  (dom/div {:className "intro-message"}
                    (dom/h1 nil "Tracking money as a nomad")
                    (dom/hr {:className "intro-divider"})

                    (dom/span nil intro-message-text)))
                (dom/div {:className "row align-center actions"}
                  (map (fn [[classes text href]]
                         (dom/div {:className "column small-5 medium-3 large-2 text-center"}
                           (dom/a {:className classes :href href} text)))
                       [["button hollow" "Get Started" "/signup"]
                        ["button" "Demo" "/play"]]))))

            (dom/div {:id "nomadic-living" :className "content-section"}
              (dom/h3 {:className "section-heading text-center"}
                      "Nomadic living")
              (dom/div {:className "row"}
                (map (fn [[title text]]
                       (if (= ::filler-item title)
                         (dom/div {:className "column medium-6 small-12"})
                         (dom/div {:className "column medium-6 small-12 nomad-section"}
                           (dom/h4 nil title)
                           (dom/span nil text))))
                     [["Housing & transport"
                       "Instead of a monthly fixed rent, housing expenses tend to have no fixed amounts or time intervals and transportation becomes more regular. So add your costs over certain time periods and have monthly amounts calculated for you for easier overview."]
                      [::filler-item]
                      [::filler-item]
                      ["International bank fees"
                       "Specify your bank fees on international purchases with your expenses. If you're using cash, add your ATM withdrawals to include all those ATM fees."]
                      ["Excuse me, do you have WiFi?"
                       "Even if the answer is \"No\", grab your coffee and add your expense offline in the mobile app. We will sync up when you get your connection back."]])))

            (dom/div {:id "at-a-glance" :className "content-section"}
              (dom/div {:className "text-center"}
                (dom/h3 {:className "section-heading"} "At a glance")
                (dom/div {:className "row small-up-2 medium-up-3"}
                  (map (fn [[icon title text]]
                         (dom/div {:className "glance-item column"}
                           (dom/img {:src (str "/assets/icons/" icon)})
                           (dom/h4 nil title)
                           (dom/span nil text)))
                       [["offline.svg"
                         "Use offline"
                         "No internet access? No problem, just use the mobile app to do what you need to do offline."]
                        ["tags.svg"
                         "Tags"
                         "Add context to your transactions with tags and use them filter over the things you are interested in."]
                        ["currencies.svg"
                         "Currencies"
                         "Add your expenses in the local currency and have them automatically converted."]
                        ["no-password.svg"
                         "No passwords"
                         "No need to remember a new password. Just sign in using your email or social media."]
                        ["share.svg"
                         "Collaborate"
                         "Share your dashboards with friends and family and collaborate on your expense tracking."]
                        ["smart-categories.svg"
                         "Smart categories"
                         "Create categories based on rules that automatically apply to your expenses."]]))))

            (dom/div {:id "pay-what-you-want"
                      :className "content-section text-center"}
              (dom/h3 {:className "section-heading"}
                      "Pay What You Want")
              (dom/h4 nil
                      "set your own price for a monthly subscription starting at "
                      (dom/strong nil "$0"))
              (dom/div {:className "row column"}
                (dom/div {:className "column pricing-section"}
                  (dom/h5 nil "Why?")
                  (dom/ul {:className "no-bullet"}
                    (map (fn [[text]]
                           (dom/li nil
                             (dom/i {:className "fa fa-long-arrow-right fa-fw"})
                             text))
                         [["We want Jourmoney to be accessible to everyone, regardless of financial situation."]
                          ["We want to give you the freedom to decide what you think a subscription is worth to you."]
                          ["We are interested in how you value Jourmoney, so we can keep improving and make it better!"]])))))

            (dom/div {:id "footer"}
              (dom/div {:className "row align-center"}
                (dom/span nil "Start tracking your money today"))
              (dom/div {:className "row align-center"}
                (map (fn [[classes text href]]
                       (dom/div {:className "column small-5 medium-3 large-2 text-center"}
                         (dom/a {:className classes :href href} text)))
                     [["button hollow" "Get Started" "/signup"]
                      ["button" "Demo" "/play"]]))

              (dom/footer
                {:className "footer"}
                (dom/ul {:className "menu"}
                        (dom/li nil
                                (dom/p {:className "small copyright"}
                                           "Say hi to us anytime at "
                                           (dom/a {:className "mail-link"
                                                   :href      "mailto:info@jourmoney.com"}
                                                  "info@jourmoney.com"))))
                (dom/ul {:className "menu"}
                        (dom/li nil
                                (dom/p {:className "copyright small"}
                                       "Copyright © eponai 2016. All Rights Reserved"))))))

          (dom/script {:src "https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"
                       :type text-javascript})
          (dom/script {:type text-javascript
                       :src "https://cdn.jsdelivr.net/foundation/6.2.4/foundation.min.js"})
          (common/inline-javascript (document-ready-code)))))))