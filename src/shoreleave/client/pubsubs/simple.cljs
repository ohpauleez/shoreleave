(ns shoreleave.client.pubsubs.simple
  (:require [goog.pubsub.PubSub :as pubsub]
            [shoreleave.client.pubsubs.protocols :as ps-protocols]
            [shoreleave.client.efunction :as efn]))

(extend-protocol ps-protocols/IPublishable
  
  function
  (topicify [t]
    (or (ps-protocols/publishized? t)
        (str `t)))
  (publishized? [t]
    (:sl-published (meta t)))
  (publishize [fn-as-topic bus]
    (if (ps-protocols/publishized? fn-as-topic)
      fn-as-topic
      (let [published-topic (ps-protocols/topicify fn-as-topic)
            new-meta (assoc (meta fn-as-topic) :sl-published published-topic)]
        (efn/Function. (fn [& args]
                         (let [ret (apply fn-as-topic args)]
                           (ps-protocols/publish bus published-topic ret)
                           ret))
                       new-meta))))

  efn/Function
  (topicify [t]
    (or (ps-protocols/publishized? t)
        (str `t)))
  (publishized? [t]
    (:sl-published (meta t)))
  (publishize [fn-as-topic bus]
    (if (ps-protocols/publishized? fn-as-topic)
      fn-as-topic
      (let [published-topic (ps-protocols/topicify fn-as-topic)
            new-meta (assoc (meta fn-as-topic) :sl-published published-topic)]
        (efn/Function. (fn [& args]
                         (let [ret (apply (.-f fn-as-topic) args)]
                           (ps-protocols/publish bus published-topic ret)
                           ret))
                       new-meta))))

  default
  (topicify [t]
    (str t)))

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

