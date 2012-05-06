(ns shoreleave.client.remotes.jsonp
  (:require [goog.net.Jsonp :as jsonp]
            [shoreleave.client.common :as common]))

(defn jsonp [uri & opts]
  (let [{:keys [on-success on-timeout content callback-name callback-value timeout]} opts
        req (goog.net.Jsonp. uri callback-name)
        data (when content (common/clj->js content))
        on-success (when on-success #(on-success (js->clj % :keywordize-keys true)))
        on-timeout (when on-timeout #(on-timeout (js->clj % :keywordize-keys true)))
        ]
    (when timeout (.setRequestTimeout req timeout))
    (.send req data on-success on-timeout callback-value)))

