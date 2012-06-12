(ns shoreleave.client.pubsubs.protocols
  (:require [shoreleave.client.efunction :as efn]))

(defprotocol IMessageBrokerBus
  (subscribe [broker-bus handler-fn topic])
  (subscribe-once [broker-bus handler-fn topic])
  (unsubscribe [broker-bus handler-fn topic])
  (publish
    [broker-bus topic data]
    [broker-bus topic data more-data]))

(defprotocol IPublishable
  (topicify [t])
  (publishized? [t])
  (publishize [t broker-bus]))


(extend-protocol IPublishable

  function
  (topicify [t]
    (or (publishized? t)
        (str `t)))
  (publishized? [t]
    (:sl-published (meta t)))
  (publishize [fn-as-topic bus]
    (if (publishized? fn-as-topic)
      fn-as-topic
      (let [published-topic (topicify fn-as-topic)
            new-meta (assoc (meta fn-as-topic) :sl-published published-topic)]
        (efn/Function. (fn [& args]
                         (let [ret (apply fn-as-topic args)]
                           (publish bus published-topic ret)
                           ret))
                       new-meta))))

  efn/Function
  (topicify [t]
    (or (publishized? t)
        (str `t)))
  (publishized? [t]
    (:sl-published (meta t)))
  (publishize [fn-as-topic bus]
    (if (publishized? fn-as-topic)
      fn-as-topic
      (let [published-topic (topicify fn-as-topic)
            new-meta (assoc (meta fn-as-topic) :sl-published published-topic)]
        (efn/Function. (fn [& args]
                         (let [ret (apply (.-f fn-as-topic) args)]
                           (publish bus published-topic ret)
                           ret))
                       new-meta))))

  default
  (topicify [t]
    (str t)))

#_(subscribe my-listener source-fn)
#_(subscribe my-listner "cats")
#_(subscribe my-listener (atom {}))
#_(defn some-fn [] 5)
#_(def pub-some-fn (publishize some-fn))
#_(def pub-some-watchable (publishize (atom {})))

