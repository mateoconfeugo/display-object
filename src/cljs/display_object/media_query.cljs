(ns display-object.media-query
  (:require-macros  [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async  :refer [<! >! chan put! alts!]]
            [jayq.core  :as jq :refer [$ text val on prevent remove-class add-class remove]]))

(defn media-query-transform
  [channel transform]
  (let [e (<! channel)
        form-el (jq/$ :#top-form)
        _ (jq/remove-class form-el "form-inline")]
    (.log js/console form-el)))

(defn init-media-query []
  (let [channel (chan)
        media-query-list (.matchMedia  js/window "(max-width: 500px)")
        mq-cb-handler (fn [obj] (if (.-matches media-query-list) (go (>! channel {:rule "foo"}))))
        _ (.addListener media-query-list mq-cb-handler)]
    channel))
