(ns shoreleave.client.pubsubs.simple
  (:require [goog.pubsub.PubSub :as pubsub]
            [shoreleave.client.pubsubs.protocols :as ps-protocols]))


;; This is defined in ns barker.client.main, but ` resolves ns to user
;(js/console.log (keyword `process-search)
;(js/console.log (keyword (symbol "barker.client.main" (name 'process-search))))

(extend-type goog.pubsub.PubSub
  ps-protocols/IMessageBrokerBus
  (subscribe [bus handler-fn topic]
    (.subscribe bus (ps-protocols/topicify topic) handler-fn))

  (subscribe-once [bus handler-fn topic]
    (.subscribeOnce bus (ps-protocols/topicify topic) handler-fn))

  (unsubscribe [bus handler-fn topic]
    (.unsubscribe bus (ps-protocols/topicify topic) handler-fn))

  (publish
    ([bus topic data]
     (.publish bus (ps-protocols/topicify topic) data))
    ([bus topic data & more-data]
     (.publish bus (ps-protocols/topicify topic) (into [data] more-data)))))


(defn subscribers-count [bus topic]
  (.getCount bus (ps-protocols/topicify topic)))

(defn bus []
  (goog.pubsub.PubSub.))

