(ns display-object.views.editor
  (:require [net.cgrand.enlive-html :refer [deftemplate defsnippet content html-content]]))

(def ^:dynamic *top-navbar-sel* [:div.navbar.navbar-inverse.navbar-fixed-top :div.navbar-inner])
(defsnippet top-navbar "templates/top_nav_bar.html" *top-navbar-sel*
  [{:keys[] :as settings}])

(def ^:dynamic *workspace-sel* [:#drop-zone])
(defsnippet workspace "templates/workspace.html" *workspace-sel*
  [{:keys[] :as settings}])

(def ^:dynamic *download-modal-sel* [:div#downloadModal :div])
(defsnippet download-modal "templates/download_modal.html" *download-modal-sel*
  [{:keys[] :as settings}])

(def ^:dynamic *share-modal-sel* [:div#shareModal :div])
(defsnippet share-modal "templates/share_modal.html" *share-modal-sel*
  [{:keys[] :as settings}])

(def ^:dynamic *feedback-modal-sel* [:div#feedbackModal :div])
(defsnippet feedback-modal "templates/feedback_modal.html" *feedback-modal-sel*
  [{:keys[] :as settings}])

(def ^:dynamic *grid-system*  [:ul.nav.nav-list.accordion-group])
(defsnippet grid-system "templates/grid_system.html" *grid-system*
  [{:keys[] :as settings}])

(def ^:dynamic *css-base*  [:ul.nav.nav-list.accordion-group])
(defsnippet css-base "templates/css_base.html" *css-base*
  [{:keys[] :as settings}])

(def ^:dynamic *components*  [:ul.nav.nav-list.accordion-group])
(defsnippet components "templates/components.html" *components*
  [{:keys[] :as settings}])

(def ^:dynamic *javascript-sel* [:ul.nav.nav-list.accordion-group])
(defsnippet javascript-components "templates/javascript_components.html" *javascript-sel*
  [{:keys[] :as settings}])

(def ^:dynamic *sidebar-nav-sel* [:div.sidebar-nav])
(defsnippet sidebar-nav "templates/sidebar_nav.html" *sidebar-nav-sel*
  [{:keys[] :as settings}]
  [:div#grid-system] (content (grid-system settings))
  [:div#css-base] (content (css-base settings))
  [:div#components] (content (components settings))
  [:div#javascript-components] (content (javascript-components settings)))

(deftemplate editor "templates/editor.html"
  [{:keys [] :as settings}]
  [:div.navbar.navbar-inverse.navbar-fixed-top] (content (top-navbar settings))
  [:div.container-fluid :div.row-fluid :div] (content (sidebar-nav settings))
  [:div.demo] (content (workspace settings))
  [:div#downloadModal] (content (download-modal settings))
  [:div#shareModal] (content (share-modal settings))
  [:div#feedbackModal] (content (feedback-modal settings)))

(comment
  (deftemplate editor "templates/editor.html"
    [{:keys [] :as settings}]
    ;;  [:body] (html-content (editor-snippet settings))
    [:body] (html-content "<div>hello</div>")))

(deftemplate site-builder  "templates/builder.html" [])
