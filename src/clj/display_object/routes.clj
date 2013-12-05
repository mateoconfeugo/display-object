(ns display-object.routes
  (:require [compojure.core :refer [defroutes  routes context GET]]
            [compojure.route :refer [resources not-found ]]
            [net.cgrand.enlive-html :as html :refer [deftemplate] ]
            [ring.util.response :refer [file-response content-type]]
            [shoreleave.middleware.rpc :refer [remote-ns]]
            [display-object.controllers.api]
            [display-object.views.editor :refer [editor]]
            [himera.server.service :refer [app]]
            [display-object.views.site :refer [about-display-object]]))

(remote-ns 'display-object.controllers.api :as "api")

(defroutes site-pages
  (GET "/about" [] (about-display-object))
  (GET "/editor" [] (editor {})))

(defroutes app-routes
  (context "/himera" [] app)
  (resources "/")
;;  (GET "/clientconfig" [] (content-type (file-response "clientconfig.edn")   "application/edn"))
  (resources "/templates/" {:root "/templates"})
  (resources "/design/" {:root "templates"})
  (not-found "404 Page not found."))

(def all-routes (routes site-pages app-routes))
