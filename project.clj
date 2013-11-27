(defproject display-object "0.1.0"
  :description "Display Object and Editor"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :ring {:handler dispaly-object.handler/war-handler
         :init display-object.handler/init
         :destroy display-object.handler/destroy}
  :main display-object.server
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring "1.2.1"]
                 [compojure "1.1.6"] ; Web routing https://github.com/weavejester/compojure
                 [domina "1.0.2"]
                 [enlive "1.1.4"] ; serverside DOM manipulating
                 [enfocus "2.0.2"] ; client DOM manipulating
                 [vrepl "1.1.1"]
                 [com.taoensso/timbre "2.7.1"] ; Logging
                 [ring-clj-params "0.1.0"]
                 [org.clojure/clojurescript "0.0-2080"]
                 [tryclojure "0.1.0-SNAPSHOT"]
                 [himera "0.1.0-SNAPSHOT"]
                 [ring "1.2.1"]
                 [ring-anti-forgery "0.3.0"]
                 [ring-server "0.3.1" :exclusions [[org.clojure/clojure] [ring]]]
                 [ring-refresh "0.1.2" :exclusions [[org.clojure/clojure] [compojure]]]
                 [shoreleave/shoreleave-remote "0.3.0"]
                 [shoreleave/shoreleave-remote-ring "0.3.0"]
                 [shoreleave "0.3.0"]
                 [junit/junit "4.11"]
                 [lein-junit "1.1.4"]]
  :plugins [[lein-cljsbuild "0.3.3"]
            [lein-marginalia "0.7.1"]
            [lein-test-out "0.3.0"]
            [lein-git-deps "0.0.1-SNAPSHOT"]
            [com.cemerick/clojurescript.test "0.2.1"]
            [lein-ring "0.8.5"]
            [lein-localrepo "0.4.1"]
            [s3-wagon-private "1.1.2"]
            [lein-expectations "0.0.8"]
            [lein-autoexpect "0.2.5"]          ]
  :git-dependencies [["https://github.com/Raynes/tryclojure.git"]]
  :source-paths [".lein-git-deps/Raynes/src/"]
  :dev-dependencies [[jline "0.9.94"]
                     [ring-mock "0.1.5"]
                     [ring/ring-devel "1.2.1"]
                     [clj-webdriver "0.6.0"]
                     [expectations "1.4.56"]
                     [org.clojure/tools.trace "0.7.6"]
                     [vmfest "0.3.0-rc.1"]]
  :cljsbuild {:builds
              [{:source-paths ["src/cljs"],
                :compiler
                {:pretty-print true,
                 :output-dir "resources/public/js/",
                 :output-to "resources/public/js/repl.js",
                 :optimizations :simple},
                :jar true}]}
  :jvm-opts ["-Djava.security.policy=heroku.policy" "-Xmx80M"]
  :source-paths ["src/clj"]
  :main display-object.server.app
  :min-lein-version "2.0.0")
