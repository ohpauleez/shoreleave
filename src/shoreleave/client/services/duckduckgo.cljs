(ns shoreleave.client.services.duckduckgo
  (:require [shoreleave.client.remote :as remote]))

(defn zero-click [query callback-fn]
  (remote/jsonp "http://api.duckduckgo.com"
                :content {:format "json" :pretty "0"
                          :q query}
                :on-success #(callback-fn %)))

