(ns display-object.server
  "Server handles for repl development and `run` hooks"
  (:refer-clojure :exclude [read-string]) ; suppress the shadowing warning
  (:require [clojure.core :as core]
            [clojure.core.async :refer [chan <! >! sliding-buffer go]]
            [clojure.edn :as edn :refer [read-string]]
            [display-object.async :refer [forward!]]
            [display-object.config :refer [get-default-config]]
            [display-object.handler :as handler]
            [ring.server.standalone :as ring-server])
  (:gen-class))


(defn spawn-client-display-object-process!
  [ws-in ws-out command-chan id clients name]
  (let [in (chan (sliding-buffer 1))]
    (swap! clients assoc id in)
    (forward! in ws-in)
    (go
     (>! command-chan [:player/join id name])
     (loop [msg (<! ws-out)]
       (if msg
         (let [command (edn/read-string msg)]
           (>! command-chan (conj command id))
           (recur (<! ws-out)))
         (do (>! command-chan [:player/leave id])
             (swap! clients dissoc id))))
     (println "Client process terminating"))))


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
