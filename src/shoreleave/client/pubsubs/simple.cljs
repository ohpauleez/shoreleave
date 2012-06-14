(ns shoreleave.client.pubsubs.simple
  (:require [goog.pubsub.PubSub :as pubsub]
            [shoreleave.client.pubsubs.protocols :as ps-protocols]))


;; This is defined in ns barker.client.main, but ` resolves ns to user
;(js/console.log (keyword `process-search)
;(js/console.log (keyword (symbol "barker.client.main" (name 'process-search))))
;all *ns* things are missing

(extend-type goog.pubsub.PubSub
  ps-protocols/IMessageBrokerBus
  (subscribe [bus topic handler-fn]
    (.subscribe bus (ps-protocols/topicify topic) handler-fn))

  (subscribe-once [bus topic handler-fn]
    (.subscribeOnce bus (ps-protocols/topicify topic) handler-fn))

  #_(subscribe-> [bus & chain-handler-fns]
    (let [subscripts (partition 2 1 chain-handler-fns)]
      (when-not (empty? subscripts)
        (doseq [[t h] subscripts]
          (ps-protocols/subscribe bus t h)))))

  (unsubscribe [bus topic handler-fn]
    (.unsubscribe bus (ps-protocols/topicify topic) handler-fn))

  (publish
    ([bus topic data]
     (.publish bus (ps-protocols/topicify topic) data))
    ([bus topic data & more-data]
     (.publish bus (ps-protocols/topicify topic) (into [data] more-data))))

  IHash
  (-hash [bus] (goog.getUid bus)))

(defn subscribers-count [bus topic]
  (.getCount bus (ps-protocols/topicify topic)))

(defn bus []
  (goog.pubsub.PubSub.))

