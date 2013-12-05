(ns display-object.protocols)

(defprotocol DisplayObject
  (render [this] "Populate occupied DOM")
  (get-channels [this] "Get the i/o channels that govern operation of the display object"))
