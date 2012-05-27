(ns shoreleave.client.pubsubs.protocols)

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

