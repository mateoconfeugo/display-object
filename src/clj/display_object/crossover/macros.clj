;*CLJSBUILD-REMOVE*;
(ns display-object.crossover.macros)

(defmacro go-loop [& body]
  `(cljs.core.async.macros/go
     (while true
       ~@body)))
