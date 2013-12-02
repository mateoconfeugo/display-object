(ns display-object.async
  (:require [clojure.core.async :refer [go <! >!]]))

(defn forward!
  [from to]
  (go
   (loop [msg (<! from)]
     (>! to msg)
     (when msg
       (recur (<! from))))))
