(ns display-object.core
  (:require-macros [shoreleave.remotes.macros :as srm :refer [rpc]]
                   [cljs.core.match.macros :refer [match]]
                   [cljs.core.async.macros  :refer [go]]
                   [enfocus.macros :as em])
  (:require [display-object.helpers :refer [event-chan by-id click-chan listen]]
            [display-object.media-query :refer [media-query-transform init-media-query]]
            [display-object.websocket :refer [connect!]]
            [cljs.core.async :refer [<! >! chan put! alts!]]
            [enfocus.core :as ef :refer [from read-form html-content at]]
            [jayq.core  :as jq :refer [$ text val on prevent remove-class add-class remove]]
            [shoreleave.remotes.http-rpc :refer [remote-callback]]
            [shoreleave.common :as common]
            [shoreleave.browser.history :as history]))

(defprotocol DisplayObject
  (render [this] "Populate occupied DOM")
  (get-channels [this] "Get the i/o channels that govern operation of the display object"))

(defn get-dom-event-channels
  [el])

(defn ^:export new-error-channel
  [{:keys [] :as args}]
  (chan))

(defn ^:export get-channels [display-object]
  [(:ec display-object) (:mqc display-object) (:css-in display-object)])

(defn ^:export  render-display-object [display-object]
  (let [input (:model display-object)
        transform (:handler-fn display-object)]
    (transform input)))

(defn ^:export render-display-object-to-channel [display-object & channel]
  (put! (or channel (:html-out display-object)) (render-display-object display-object)))

(defn ^:export route-html-to-el [display-object])

(defn  create-event-channels [dply-obj-cfg]
  (map #(listen (:el %) (:type %) (:handler-fn %)) (-> dply-obj-cfg :event-channel-handler)))

(defn new-display-object
  "Creates a generic display object that can then be feed intos into the channels and outputs corresponding read"
  [{:keys [el channels handler-fns websocket-url html css cms-model snippet-fn menu-html] :as args}]
  (let [state (atom {:html html
                     :css css
                     :cms cms-model
                     :snippet snippet-fn
                     :editor-menu-html menu-html
                     :sample-html html
                     :sample-css css})
        ws-channels (connect! websocket-url)
        channels {:css-input (chan)
                  :css-output (chan)
                  :html-input (chan)
                  :html-output (chan)
                  :cms-input (chan)
                  :cms-output (chan)
                  :websocket-in (:in ws-channels)
                  :websocket-out (:out ws-channels)
                  :dom-event-channel (get-dom-event-channels el)
                  :cljs-channel (chan)
                  :ec (new-error-channel {})}
        bus (into [] (vals channels))
        dispatch-table (fn [ch val]
                         (match [( nth val 0)]
;;                                [:web-in] ((:websocket-input-fn handler-fns) (nth val 1))
;;                                [:web-out] ((:websocket-input-fn handler-fns) (nth val 1))
                                [:html-in] (swap! state assoc :html (nth val 1))
                                [:html-out] (snippet-fn (nth val 1))
                                [:data-in] (snippet-fn (nth val 1))
                                [:css-in] (swap! state assoc :css  (nth val 1))
                                [:css-out] ((:update-css-fn handler-fns) (nth val 1))))]
    (reify DisplayObject
           (render [this] ())
           (get-channels [this]))
    (go (while true
          (let [[val ch] (alts! bus)]
            (dispatch-table ch val))))))

(defn run
  [dis-obj]
  (do
    (.log js/console (str "setting up the " (:name dis-obj)))
    (go (while true
          (let [input-data (<! (:data-in dis-obj))]
            (do
              (.log js/console "setting up data input data stream channel")
              (render-display-object-to-channel (assoc dis-obj :model input-data))))))
    (go (while true
          (let [html-data (<! (:html-in dis-obj))]
            (.log js/console "setting up html-in channel"))))
    (go (while true
          (let [html-data (<! (:html-out dis-obj))]
            (at ($ (:el dis-obj) (html-content html-data))))))
    (go (while true
          (let [css-data (<! (:css-in dis-obj))]
            (.log js/console css-data))))
    (go (while true
          (let [e (<! (:media-query-channel dis-obj))]
            (.log js/console e))))
    (go (while true
          (let [[event choosen] (alts! [(:dom-event-channels dis-obj)])
                dispatch (:channel-dispatcher dis-obj)
                ec (new-error-channel {})]
            (dispatch {:channel choosen :event event :error-channel ec}))))))
