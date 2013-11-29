(ns display-object.editor
  ^{:author "Matthew Burns"
    :doc "Display Object Editor containing the functionality build a web site from a set of nested display objects"}
  (:use-macros [dommy.macros :only [node sel sel1 deftemplate]])
  (:require-macros [cljs.core.match.macros :refer [match]]
                   [cljs.core.async.macros  :refer [go]]
                   [jayq.macros :refer [ready]]
                   [shoreleave.remotes.macros :as srm :refer [rpc]]
                   [enfocus.macros :as em])
  (:require [cljs.core.async :refer [alts! chan >! >! put!]]
            [cljs.core.async.impl.ioc-helpers ]
            [jayq.core :as jq :refer [$ on prevent]]
            [dommy.utils :as utils]
            [dommy.core :as dommy]
            [enfocus.core :as ef :refer [at html-content]]
            [enfocus.effects :as effects]
            [enfocus.events :as ev]
            [goog.dom :as dom]
            [jayq.core :refer [$ text val on prevent remove-class add-class remove empty html children append]]
            [shoreleave.browser.storage.sessionstorage :refer [storage]]
            [shoreleave.remote]))

(defn data-from-event
  "Transforms a js event into a proper clojure map"
  [event]
  (-> event .-currentTarget $ .data (js->clj :keywordize-keys true)))

(defn click-chan
  "Creates a chan the when event is raised puts the event data with the correct routing message into
   the channel that is returned upon creation"
  [selector msg-name]
  (let [rc (chan)]
    (jq/on (jq/$ "body") :click selector {}
           (fn [e]
             (jq/prevent e)
             (put! rc [msg-name (data-from-event e)])))
    rc))

(defn extract-tuple
  [event ui]
  "Gather the necessary dom and admin data from a droppable event and ui element node"
  (let [demo (html ($ ".demo"))
        dom (nth (js->clj (.-item ui)) 0)
        xpath (js/getXPath dom)
        session {}]
    {:page-html demo :snippet-html (.-innerHTML dom) :xpath xpath :dom dom
     :site-id (:site-id session) :user-id (:user-id session)}))

(defn drop-channel
  [selector msg-name formatter]
  "Create an element channel that response to drop events on the element by extracting the
   relevent data from the event and ui object and sending it as a message to be dispatched
   via the created channel"
  (let [elm ($ selector)
        element-channel (chan)
        _ (.sortable elm (clj->js {:receive (fn [event ui]
                                              (do (.log js/console (clj->js [event ui elm]))
                                                  (go (>! element-channel [msg-name (assoc (formatter event ui) :target elm)]))
                                                  ))}))]
    element-channel))

(defn update-site
  "Call the server side persiting logic to save the landing site edits send the results
   back to the dispatcher to be handled"
  [{:keys [page-html xpath snippet-html  user-id site-id drop-channel dom target] :as tuple}]
  (srm/rpc (api/update {:page-html page-html :xpath xpath :snippet-html snippet-html :user-id user-id :site-id site-id}) [resp]
           (put!  drop-channel [:site-updated (assoc resp :dom dom :target target)])))

(defn save-site
  "Call the server side persiting logic to save the landing site edits"
  [{:keys [page-html user-id site-id drop-channel] :as tuple}]
  (srm/rpc (api/save {:page-html page-html :user-id user-id :site-id site-id}) [resp]
           (put! drop-channel [:site-updated {:page-html resp}])))

(defn render-workspace
  [ch model elm]
  "Insert the new html with the recent edits"
  (do (if (:uuid model)
        ;;        (add-class ($ ((.-getElementsByXPath js/xpath) js/document (:xpath model))) (str "uuid_" (:uuid model))))
        (.log js/console (clj->js model)))
      (.log js/console (js/getXPath (:target model)))
      ;;      (add-class ($ (:dom model)) "demo")
      (add-class ($ elm) "demo")
      (html ($ elm) (or (:tmp-page-html model) (:page-html model)))))

(em/deftemplate html-editor :compiled  "templates/html-editor.html" [])

(defn new-editor
  "Create the event channels and start the loop that feeds the event data dispatch table
   that routes channel event data to the correct handler val is a vector witha keyword
   to be dispatched on and the value from the channel"
  [selector session]
  (let [save-channel (click-chan "#save-site" :save-site)
        drop-channel (drop-channel ".demo" :drop-snippet extract-tuple)
        input-channel (chan)
        dispatch-fn (fn [ch val session]
                      (match [(nth val 0)]
                             [:save-site] (save-site {:page-html (html ($ ".demo"))
                                                      :user-id (:user-id session)
                                                      :site-id (:site-id session)
                                                      :drop-channel ch})
                             [:html-in] (at js/document (nth val 1) (html-content (html-editor )))
                             [:drop-snippet] (update-site (assoc (nth val 1) :drop-channel ch))
                             [:site-updated] (render-workspace ch (nth val 1) ($ ".demo"))))
        channels [save-channel drop-channel input-channel]]
    (go (while true
          (let [[val ch] (alts! channels)]
            (dispatch-fn ch val session))))
    input-channel))


(defn start-editor []
  (let [_ (.log js/console "creating a new display object editor")
        html-in (new-editor "#display-object-editor" {})
        _ (put! html-in [:html-in "#display-object-editor"])]))

(ready (start-editor))
