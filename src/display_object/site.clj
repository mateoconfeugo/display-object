(ns display-site.site
  "Web specific controllers")

;; These are controller actions for the main site.
;; They're wired up via Compojure routes in `baseline/routes.clj`


(defn test-shoreleave []
  (slurp "resources/public/html/test.html"))
