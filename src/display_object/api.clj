(ns display-object.api
  ^{:author "Matthew Burns"
    :doc "API controller for display object"}
  (:require [com.ashafa.clutch :refer [couch create! document-exists? get-document put-document with-db]]
            [taoensso.timbre :refer [info]]
            [clojure.java.jdbc :as jdbc :refer [with-query-results with-connection]]
            [clojure.java.jdbc.sql :as sql :refer [select where]]))


(defn publish
  "Publish landing site to the destination "
  [{:keys [landing-site-id destination-id doc-db] :as request}]
  (let [landing-site (with-db doc-db (get-document landing-site-id))
;;        destination (with-db doc-db (get-document destination-id))
;;        mover (get-mover destination)
        ]
;;    (publish-to landing-site)
;;    (publish-to destination landing-site)
    ))

(defn save
  "Persist the user authoring edits"
  [{:keys [page-html landing-site-id user-id prefix user-db-host user-db-port
           user-db-name user-db-username user-db-password user-db-protocol]
    :or {prefix "site-builder" user-db-name "mgmt" user-db-port "3306" user-db-host "127.0.0.1"
         user-db-username "root" user-db-password "test123" user-db-protocol "mysql"}
    :as request}]
  (let [mysql-db {:subprotocol (or user-db-protocol "mysql")
                  :subname (format "//%s:%s/%s" user-db-host user-db-port user-db-name)
                  :user user-db-username :password user-db-password}
        user (:username  (first (jdbc/with-connection mysql-db
                       (jdbc/with-query-results res ["select * from user where id =?" user-id]
                         (doall res)))))
        doc-db (site-builder-db prefix user)
        ls-id (or landing-site-id (sb/get-new-landing-site-id doc-db))
        landing-site (if (and landing-site-id (document-exists? doc-db ls-id))
                       (with-db doc-db (get-document landing-site-id))
                       (with-db doc-db (put-document {:_id (str (get-new-landing-site-id doc-db))})))
        saved-site (sb/save-landing-site {:page-html page-html :landing-site-id ls-id :db doc-db})]
    (:page-html saved-site)  ))

;;[{:keys [xpath page-html snippet-html landing-site-id user-id uuid prefix user-db-host user-db-port
;;        user-db-name user-db-username user-db-password user-db-protocol :as request}]

(defn update
  "Persist the user authoring edits"
  [{:keys [xpath page-html snippet-html landing-site-id user-id uuid
           prefix user-db-host user-db-port  user-db-name user-db-username
           user-db-password user-db-protocol]
    :or {prefix "site-builder" user-db-name "mgmt" user-db-port "3306" user-db-host "127.0.0.1"
         user-db-username "root" user-db-password "test123" user-db-protocol "mysql"}
    :as request}]
  (let [mysql-db {:subprotocol (or user-db-protocol "mysql")
                  :subname (format "//%s:%s/%s" user-db-host user-db-port user-db-name)
                  :user user-db-username :password user-db-password}
        user (first (jdbc/with-connection mysql-db
                      (jdbc/with-query-results res ["select * from user where id =?" user-id]
                        (doall res))))
        uuid (or uuid (new-uuid))
        doc-db (site-builder-db prefix (:username user))
        ls-id (or landing-site-id (get-new-landing-site-id doc-db))
        landing-site (if (and landing-site-id (document-exists? doc-db ls-id))
                       (with-db doc-db  (get-document landing-site-id))
                       (with-db doc-db (put-document  {:_id (str (sb/get-new-landing-site-id doc-db))})))
        updated-site (sb/update-landing-site {:page-html page-html :landing-site-id ls-id :db doc-db
                                              :snippet-html snippet-html :uuid uuid :xpath xpath})]
    {:page-html (:tmp-page-html updated-site)
     :uuid uuid
     :xpath xpath
     :landing-site-id landing-site-id
     :user-id user-id}))
