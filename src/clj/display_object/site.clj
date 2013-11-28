(ns display-object.site
  (:require [garden.def :refer [defstylesheet defstyles]]
            [garden.units :refer [px]]))

(defstylesheet screen
  {:output-to "resources/public/screen.css"}
  [:body
   {:font-family "sans-serif"
    :font-size (px 16)
    :line-height 1.5}]
  [:div {:padding (px 5)}])
