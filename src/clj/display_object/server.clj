(ns display-object.server
  "Server handles for repl development and `run` hooks"
  (:refer-clojure :exclude [read-string]) ; suppress the shadowing warning
  (:require [clojure.core :as core]
            [clojure.edn :as edn :refer [read-string]]
            [display-object.config :refer [get-default-config]]
            [display-object.handler :as handler]
            [ring.server.standalone :as ring-server])
  (:gen-class))

(defn start-server
  "used for starting the server in development mode from REPL"
  [& [port]]
   (let [cfg (get-default-config)
         port (or (and port (Integer/parseInt port))
                 (Integer. (get (System/getenv) "PORT" (cfg :site-builder-port)))
                 8080)
        server (ring-server/serve (handler/get-handler #'handler/app cfg)
                                  {:port port
                                   :init handler/init
                                   :auto-reload? true
                                   :destroy handler/destroy
                                   :join true})]
    server))

(defn stop-server [server]
  "Stop the jetty server"
  (when server
    (.stop server)
    server))

(defn restart-server [server]
  "Restart jetty server"
  (when server
    (doto server
      (.stop)
      (.start))))

(def server-starters {:dev start-server})

(defn -main
  "entry point"
  [& m]
  (let [mode-kw (keyword (or (first m) :dev))
        server-fn (server-starters mode-kw)
        server (server-fn)]
    server))
