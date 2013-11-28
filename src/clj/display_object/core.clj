(ns display-object.com
  (:require-macros [shoreleave.remotes.macros :as srm :refer [rpc]]
                   [cljs.core.match.macros :refer [match]]
                   [cljs.core.async.macros  :refer [go]]
                   [enfocus.macros :as em])
  (:require [flourish-common.utils.helpers :refer [event-chan by-id click-chan listen]]
            [flourish-common.utils.media-query :refer [media-query-transform init-media-query]]
            [cljs.core.async :refer [<! >! chan put! alts!]]
            [enfocus.core :as ef :refer [from read-form html-content at]]
            [jayq.core  :as jq :refer [$ text val on prevent remove-class add-class remove]]
            [shoreleave.remotes.http-rpc :refer [remote-callback]]
            [shoreleave.common :as common]
            [shoreleave.browser.history :as history]))

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


(defprotocol DisplayObject
  (render this)
  (get-channels this))

(defn new-display-object
  [{:keys [el channels handler-fns] :as args}]
  (let [state (atom {:html nil
                     :css nil
                     :cms nil
                     :snippets nil
                     :template nil})
        channels {:css-input (chan)
                  :css-output (chan)
                  :html-input (chan)
                  :html-output (chan)
                  :cms-input (chan)
                  :cms-output (chan)
                  :websocket-in (chan)
                  :websocket-out (chan)
                  :dom-event-channel (get-dom-event-channels el)
                  :cljs-channel} (chan)
        settings (build-display-object)
        _ (set-intial-state state)
        bus (into [] (vals channls))
        dispatch-table (fn [ch val]
                         (match [( nth val 0)]
                                [:web-in] ((:websocket-input fn handler-fns) (nth val 1))
                                [:web-out] ((:websocket-input fn handler-fns) (nth val 1))
                                [:html-in] ((:edit-html-fn handler-fns) (nth val 1))
                                [:html-out] ((:update-html-fn handler-fns) (nth val 1))
                                [:data-in] ((:update-model-fn handler-fns) (nth val 1))
                                [:css-in] ((:edit-css-fn handler-fns) (nth val 1))
                                [:css-out] ((:update-css-fn handler-fns) (nth val 1))))]
    (refiy DisplayObject
           (render [this]
                   ())
           (get-channels this)
           (initialize
            (go (while true
                  (let [[val ch] (alts! bus)]
                    (dispatch-table ch val))))



(defn ^:export build-display-object
  [{:keys [name type html handler-fn template css menu-html menu-item-html el initial-model-data] :as cfgs}]
  {:name name
   :type type
   :el el
   :editor-menu-html menu-html
   :sample-html html
   :current-html html
   :sample-css css
   :model (or initial-model-data  nil)
   :current-css css
   :handler-fn handler-fn
   :data-in (chan)
   :data-out (chan)
   :dom-event-channels (create-event-channels cfgs)
   :ec (new-error-channel {})
   :mqc (init-media-query)
   :css-in (chan)
   :html-in (chan)
   :html-out (chan)})

(defn ^:export run [dis-obj]
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
