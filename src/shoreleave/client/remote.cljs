(ns shoreleave.client.remote
  (:require [shoreleave.client.remotes.request :as request]
            [shoreleave.client.remotes.jsonp :as jsonp]
            [shoreleave.client.remotes.http-rpc :as rpc])
)

;; This is an XHR request that uses a pool of XHR handlers
;; You should always prefer to use this method over others
(def request request/request)
(def jsonp jsonp/jsonp)
(def remote-callback rpc/remote-callback)

