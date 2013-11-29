(ns display-object.routes
  (:require [compojure.core :as c-core :refer [defroutes  routes GET]]
            [compojure.route :refer [resources not-found]]
            [net.cgrand.enlive-html :as html :refer [deftemplate] ]
            [shoreleave.middleware.rpc :refer [remote-ns]]
            [display-object.controllers.api]
            [display-object.views.editor :refer [editor]]
            [display-object.views.site :refer [about-display-object]]))

(remote-ns 'display-object.controllers.api :as "api")

(defroutes site-pages
  (GET "/about" [] (about-display-object))
  (GET "/editor" [] (editor {})))

(defroutes app-routes
  (resources "/")
  (resources "/templates/" {:root "/templates"})
  (resources "/design/" {:root "templates"})
  (not-found "404 Page not found."))

(def all-routes (routes site-pages app-routes))
