(ns display-object.routes
  (:require [compojure.core :as c-core :refer [defroutes GET POST PUT DELETE
                                               HEAD OPTIONS PATCH ANY]]
            [compojure.route :as c-route]
            [net.cgrand.enlive-html :refer [deftemplate] :as html]
            [shoreleave.middleware.rpc :refer [remote-ns]]
;;            [display-object.site :as cont-site ]
            [display-object.api]
            [display-object.editor :refer [editor site-builder]]))

;; Remote APIs exposed
(remote-ns 'display-object.api :as "api")

(defroutes site-pages
  (GET "/site-builder" [] (site-builder))
  (GET "/editor" [] (editor {})))

(defroutes app-routes
  (c-route/resources "/")
  (c-route/resources "/templates/" {:root "/templates"})
  (c-route/resources "/design/" {:root "templates"})
  (c-route/not-found "404 Page not found."))

(def all-routes (c-core/routes site-pages app-routes))
