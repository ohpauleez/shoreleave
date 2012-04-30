(ns shoreleave.client.remotes.jsonp
  (:require [goog.net.Jsonp :as jsonp]
            [shoreleave.client.common :as common]))

(defn jsonp [uri & [opts]]
  (let [{:keys [on-success on-timeout payload param-value]} opts
        req (goog.net.Jsonp. uri)
        data (when payload (common/clj->js payload))
        on-success (when on-success #(on-success (js->clj %)))
        on-timeout (when on-timeout #(on-timeout (js->clj %)))
        ]
    (.send req data on-success on-timeout param-value)))

