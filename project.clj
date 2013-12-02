(defproject display-object "0.1.0"
  :description "Display Object and Editor"

  :url "http://mateoconfeugo.github.io/display-object/"

  :license {:name "Eclipse Public License"  :url "http://www.eclipse.org/legal/epl-v10.html"}

  :ring {:handler display-object.handler/war-handler
         :init display-object.handler/init
         :destroy display-object.handler/destroy}

  :main display-object.server

  :uberjar-name "display-object-standalone.jar"

  :heroku {
           :app-name "display-object"
           :app-url "http://display-object.herokuapp.com"
           }

  :jvm-opts ["-Djava.security.policy=heroku.policy" "-Xmx80M"]

  :min-lein-version "2.0.0"

  :source-paths [ "src/clj" "src/cljs" ".generated/clj" ".generated/cljs"]

  :hooks [cljx.hooks leiningen.cljsbuild]

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/core.async "0.1.242.0-44b1e3-alpha"]
                 [org.clojure/core.match "0.2.0"]
                 [ring "1.2.1"] ; Webserver framework
                 [compojure "1.1.6"] ; Web routing https://github.com/weavejester/compojure
                 [domina "1.0.2"]
                 [prismatic/dommy "0.1.1"]
                 [enlive "1.1.4"] ; serverside DOM manipulating
                 [enfocus "2.0.2"] ; client DOM manipulating
                 [jayq "2.5.0"] ; clojurescript jquery wrapper
                 [garden "1.1.4"] ; server side css rendering library
                 [org.clojure/java.jdbc "0.3.0-alpha5"] ;
                 [vrepl "1.1.1"]
                 [com.taoensso/timbre "2.7.1"] ; Logging
                 [ring-clj-params "0.1.0"]
                 [amalloy/ring-gzip-middleware "0.1.3" :exclusions [org.clojure/clojure]]
                 [org.clojure/clojurescript "0.0-2080"]
  ;;               [tryclojure "0.1.0-SNAPSHOT"]
;;                 [himera "0.1.0-SNAPSHOT"]
                 [ring "1.2.1"]
                 [ring-anti-forgery "0.3.0"]
                 [ring-server "0.3.1" :exclusions [[org.clojure/clojure] [ring]]]
                 [ring-refresh "0.1.2" :exclusions [[org.clojure/clojure] [compojure]]]
                 [shoreleave/shoreleave-remote "0.3.0"]
                 [shoreleave/shoreleave-remote-ring "0.3.0"]
                 [shoreleave "0.3.0"]
                 [junit/junit "4.11"]
                 [lein-junit "1.1.4"]]

  :plugins [[lein-cljsbuild "0.3.3"] ; handle the clojurescript to javascript compilation process
            [lein-garden "0.1.0"] ; Build the css resources as the src code affecting them changes
            [lein-marginalia "0.7.1"] ; Literate programing
            [lein-heroku-deploy "0.1.0"] ; Deploy to Heroku
            [lein-test-out "0.3.0"] ; Output the test results in junit format
            [lein-git-deps "0.0.1-SNAPSHOT"] ; Use git repositories as clojure component repo
            [com.cemerick/clojurescript.test "0.2.1"] ; clojurescript test framework
            [lein-ring "0.8.5"] ; perform webserver operations with lein
            [lein-localrepo "0.4.1"] ; use local jar repo
            [s3-wagon-private "1.1.2"] ; use Amazon s3 bitbucket as private jar repo
            [lein-expectations "0.0.8"] ; Run the expectations framework
            [lein-autoexpect "0.2.5"] ; Run expectations framework when the src it tests chages
            [com.keminglabs/cljx "0.3.1"]] ; Preprocess clojure code that runs on both jvm and browser
;;  :git-dependencies [["https://github.com/Raynes/tryclojure.git"]]
;;  :source-paths [".lein-git-deps/Raynes/src/"]

  :dev-dependencies [[jline "0.9.94"]
                     [ring-mock "0.1.5"]
                     [ring/ring-devel "1.2.1"]
                     [clj-webdriver "0.6.0"]
                     [expectations "1.4.56"]
                     [org.clojure/tools.trace "0.7.6"]
                     [vmfest "0.3.0-rc.1"]]

  :cljx {:builds [{:source-paths ["src/cljx"]
                   :output-path "target/generated/clj"
                   :rules :clj}
                  {:source-paths ["src/cljx"]
                   :output-path "target/generated/cljs"
                   :rules :cljs}]}

  :cljsbuild {
              :repl-listen-port 9000
              :repl-launch-commands
              ;; $ lein trampoline cljsbuild repl-launch firefox <URL>
              {"firefox" ["/Applications/Firefox.app/Contents/MacOS/firefox-bin" :stdout ".repl-firefox-out" :stderr ".repl-firefox-err"]
              ;; $ lein trampoline cljsbuild repl-launch firefox-naked
               "firefox-naked" ["firefox" "resources/private/html/naked.html"
                                :stdout ".repl-firefox-naked-out" :stderr ".repl-firefox-naked-err"]
              ;;$ lein trampoline cljsbuild repl-launch phantom <URL>
               "phantom" ["phantomjs" "phantom/repl.js" :stdout ".repl-phantom-out" :stderr ".repl-phantom-err"]
              ;;$ lein trampoline cljsbuild repl-launch phantom-naked
               "phantom-naked" ["phantomjs" "phantom/repl.js" "resources/private/html/naked.html"
                                :stdout ".repl-phantom-naked-out"  :stderr ".repl-phantom-naked-err"]}
              :test-commands  ;$ lein cljsbuild test
              {"unit" ["phantomjs" "phantom/unit-test.js" "resources/private/html/unit-test.html"]}
              :builds {
                       :dev
                       {:source-paths ["src/cljs"]
                        ;;                        :externs ["public/js/layout_manager.js"]
                        :compiler {:output-to "resources/public/js/main-debug.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}
                       :prod
                       {:source-paths ["src/cljs"]
                        ;;                        :externs ["public/js/layout_manager.js"]
                        :compiler {:output-to "resources/public/js/main.js"
                                   :optimizations :advanced
                                   :pretty-print false
;;                                   :source-map "resources/public/js/main.js.map"
                                   }}
                       :test
                       {:source-paths ["test-cljs"]
                        :compiler {:output-to "resources/private/js/unit-test.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}
                       }}

  :garden {:builds [{:id "screen"
                     :stylesheet display-object.site/screen
                      :compiler {:output-to "resources/screen.css"
                                 :pretty-print? false}}]})
