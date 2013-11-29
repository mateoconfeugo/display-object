(ns display-object.controllers.api
  ^{:author "Matthew Burns"
    :doc "API controller for display object"}
  (:require [taoensso.timbre :refer [info]]
            [clojure.java.jdbc :as jdbc :refer [with-query-results with-connection]]
            [clojure.java.jdbc.sql :as sql :refer [select where]]))

(defn publish
  "Publish landing site to the destination "
  [{:keys [landing-site-id destination-id doc-db] :as request}]
)

(defn save
  "Persist the user authoring edits"
  [{:keys [page-html landing-site-id user-id prefix user-db-host user-db-port
           user-db-name user-db-username user-db-password user-db-protocol]
    :or {prefix "site-builder" user-db-name "mgmt" user-db-port "3306" user-db-host "127.0.0.1"
         user-db-username "root" user-db-password "test123" user-db-protocol "mysql"}
    :as request}])

(defn update
  "Persist the user authoring edits"
  [{:keys [xpath page-html snippet-html landing-site-id user-id uuid
           prefix user-db-host user-db-port  user-db-name user-db-username
           user-db-password user-db-protocol]
    :or {prefix "site-builder" user-db-name "mgmt" user-db-port "3306" user-db-host "127.0.0.1"
         user-db-username "root" user-db-password "test123" user-db-protocol "mysql"}
    :as request}])
