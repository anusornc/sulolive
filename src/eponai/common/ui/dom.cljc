(ns eponai.common.ui.dom
  (:require
    [eponai.common.ui.elements.css :as css]
    [om.dom :as dom]
    [taoensso.timbre :refer [debug]]
    #?(:cljs [goog.object :as gobj])))

(defn add-keys-to-content [content]
  (letfn [(get-key [x] #?(:cljs (gobj/get x "key")
                          :clj (:react-key x)))
          (set-key [x v] #?(:cljs (.cloneElement js/React x #js {:key v})
                            :clj (assoc x :react-key v)))]
    (->> content
         (map-indexed (fn [i x]
                        (when x
                          (let [v (get-key x)]
                            (cond-> x (nil? v) (set-key (str i))))))))))

(defn element [el-fn {:keys [classes] :as opts} content]
  (let [react-opts (-> opts
                       (dissoc :classes)
                       (update :className (fn [s] (clojure.string/join " " [s (css/keys->class-str classes)])))
                       #?(:cljs clj->js))
        content (flatten content)]
    (condp = (count content)
      0 (el-fn react-opts)
      1 (el-fn react-opts (first content))
      (apply el-fn react-opts (add-keys-to-content content)))))

(defn article [opts & content]
  (element dom/article opts content))

(defn section [opts & content]
  (element dom/section opts content))

(defn div [opts & content]
  (element dom/div opts content))

(defn a [opts & content]
  (element dom/a opts content))

(defn header [opts & content]
  (element dom/header opts content))

;; Lists
(defn li [opts & content]
  (element dom/li opts content))

(defn ul [opts & content]
  (element dom/ul opts content))

(defn ol [opts & content]
  (element dom/ol opts content))

(defn dl [opts & content]
  (element dom/dl opts content))

(defn dt [opts & content]
  (element dom/dt opts content))

(defn dd [opts & content]
  (element dom/dd opts content))

(defn strong [opts & content]
  (element dom/strong opts content))

(defn em [opts & content]
  (element dom/em opts content))

(defn i [opts & content]
  (element dom/i opts content))

(defn p [opts & content]
  (element dom/p opts content))

(defn s [opts & content]
  (element dom/s opts content))

(defn span [opts & content]
  (element dom/span opts content))

(defn br [opts & content]
  (element dom/br opts content))

(defn small [opts & content]
  (element dom/small opts content))

(defn label [opts & content]
  (element dom/label opts content))

(defn hr [opts & content]
  (element dom/hr opts content))

(defn h1 [opts & content]
  (element dom/h1 opts content))

(defn h2 [opts & content]
  (element dom/h2 opts content))

(defn h3 [opts & content]
  (element dom/h3 opts content))

(defn h4 [opts & content]
  (element dom/h4 opts content))

(defn h5 [opts & content]
  (element dom/h5 opts content))

(defn h6 [opts & content]
  (element dom/h6 opts content))

(defn input [opts & content]
  (element dom/input opts content))

(defn textarea [opts & content]
  (element dom/textarea opts content))

(defn form [opts & content]
  (element dom/form opts content))

(defn select [opts & content]
  (element dom/select opts content))

(defn option [opts & content]
  (element dom/option opts content))

(defn optgroup [opts & content]
  (element dom/optgroup opts content))

(defn img [opts & content]
  (element dom/img opts content))

(defn picture [opts & content]
  (element dom/picture opts content))

(defn fieldset [opts & content]
  (element dom/fieldset opts content))

(defn nav [opts & content]
  (element dom/nav opts content))

(defn footer [opts & content]
  (element dom/footer opts content))

(defn button [opts & content]
  (element dom/button opts content))

(defn video [opts & content]
  (element dom/video opts content))

(defn source [opts & content]
  (element dom/source opts content))

;; TABLE

(defn table [opts & content]
  (element dom/table opts content))

(defn thead [opts & content]
  (element dom/thead opts content))

(defn tbody [opts & content]
  (element dom/tbody opts content))

(defn tfoot [opts & content]
  (element dom/tfoot opts content))

(defn tr [opts & content]
  (element dom/tr opts content))

(defn th [opts & content]
  (element dom/th opts content))

(defn td [opts & content]
  (element dom/td opts content))

(defn iframe [opts & content]
  (element dom/iframe opts content) )

(defn meta-tag [opts & content]
  (element dom/meta opts content))