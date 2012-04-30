(ns shoreleave.client.remotes.xhr
  (:require [goog.net.XhrIo :as xhr]
            ;[cljs.reader :as reader]
            [goog.events :as events]
            [shoreleave.client.remotes.common :as common])
  (:use [shoreleave.client.common :only [clj->js]]))

(defn xhr [route content callback & [opt-headers]]
  (let [req (goog.net.XhrIo.)
        [method uri] (common/parse-route route)
        data (common/->data-str content)
        callback (common/->simple-callback callback)]
    (when callback
      (events/listen req goog.net.EventType/COMPLETE #(callback req)))
    (.send req uri method data (when opt-headers (clj->js opt-headers)))))

