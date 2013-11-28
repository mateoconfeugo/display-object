(ns display-object.handler
  (:refer-clojure :exclude [read-string]) ; suppress the shadowing warning
  (:require [clojure.core :as core]
            [clojure.edn :refer [read-string]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :as route :refer [resources not-found]]
            [compojure.handler :as handler :refer [site]]
            [ring.middleware.gzip :refer [wrap-gzip]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
            [ring.middleware.session.cookie :refer [cookie-store]]
            [shoreleave.middleware.rpc :refer [wrap-rpc]]
            [display-object.routes :refer [all-routes]]))

(defn init []  (println "The baseline app is starting"))
(defn destroy []  (println "The baseline app has been shut down"))

(defn wrap-add-anti-forgery-cookie [handler & [opts]]
  "Mimics code in Shoreleave-baseline's customized ring-anti-forgery middleware."
  (fn [request]
    (let [response (handler request)]
      (if-let [token (-> request :session (get "__anti-forgery-token"))]
        (assoc-in response [:cookies "__anti-forgery-token"] token)
        response))))

(defn get-handler
  [app cfg]
  (-> app
          wrap-rpc
          ;;      wrap-add-anti-forgery-cookie
          ;;      wrap-anti-forgery
          wrap-gzip
          (wrap-resource "templates")
          wrap-file-info
          (handler/site {:session {:cookie-name (:cookie-name cfg )
                                   :store (cookie-store {:key (:session-secret cfg)})
                                   :cookie-attrs {:max-age (:session-max-age-seconds cfg)
                                                :http-only true}}})))

(def app all-routes)

;;(def war-handler  (get-handler app (clojure.edn/read-string (format "%s/%s" (System/getProperty "user.dir") "resources/config.edn"))))
(def war-handler  (get-handler app {}))
