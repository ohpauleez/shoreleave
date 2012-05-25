(ns shoreleave.client.history
  (:require [goog.events :as gevents]
            [goog.History :as ghistory]
            [goog.history.EventType :as history-event]
            [goog.history.Html5History :as history5])
  (:use [shoreleave.client.common :only [clj->js]]))

(declare history)
(defn navigate-callback
  ([callback-fn]
   (navigate-callback history callback-fn))
  ([hist callback-fn]
   (gevents/listen hist history-event/NAVIGATE
                  (fn [e]
                    (callback-fn {:token (keyword (.-token e))
                                  :type (.-type e)
                                  :navigation? (.-isNavigation e)})))))
 
(defn init-history []
  (let [history (if (history5/isSupported)
                  (goog.history.Html5History.)
                  (goog.History.))]
                   (.setEnabled history true)
                   (gevents/unlisten (.-window_ history) (.-POPSTATE gevents/EventType) ; This is a patch-hack to ignore double events
                                     (.-onHistoryEvent_ history), false, history)
                   history))

(def history (init-history))
(defn get-token  [hist]  (.getToken hist))
(defn set-token!  [hist tok]  (.setToken hist tok))
(defn replace-token! [hist tok] (.replaceToken hist tok))

;; Raw access to the HTML5 History API
;; ===================================
;; This is advantageous when you want to use the stateobj
;; for partial view or data cacheing

(defn push-state [state-map]
  (let [{:keys [state title url]
         :or {state nil
              title js/document.title}} state-map]
    (apply js/window.history.pushState (map clj->js [state title url]))
    (.dispatchEvent history (goog.history.Event. url false))))

