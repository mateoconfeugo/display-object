; DO NOT EDIT THIS FILE! IT WAS AUTOMATICALLY GENERATED BY
; lein-cljsbuild FROM THE FOLLOWING SOURCE FILE:
; file:/Users/matthewburns/github/display-object/src/clj/display_object/crossover/macros.clj


(ns display-object.crossover.macros)

(defmacro go-loop [& body]
  `(cljs.core.async.macros/go
     (while true
       ~@body)))
