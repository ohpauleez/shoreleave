(ns shoreleave.client.pubsubs.protocols
  (:require [shoreleave.client.efunction :as efn]
            [shoreleave.client.worker :as swk]))

(defprotocol IMessageBrokerBus
  (subscribe [broker-bus topic handler-fn])
  (subscribe-once [broker-bus topic handler-fn])
  #_(subscribe-> [broker-bus hf1 hf2]
               [broker-bus hf1 hf2 hf3]
               [broker-bus hf1 hf2 hf3 hf4]
               [broker-bus hf1 hf2 hf3 hf4 hf5]
               [broker-bus hf1 hf2 hf3 hf4 hf5 hf6]
               [broker-bus hf1 hf2 hf3 hf4 hf5 hf6 hf7]
               [broker-bus hf1 hf2 hf3 hf4 hf5 hf6 hf7 hf8])
  (unsubscribe [broker-bus topic handler-fn])
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
        (-> t hash str)))
  (publishized? [t]
    (:sl-published (meta t)))
  (publishize [fn-as-topic bus]
    (if (-> (meta fn-as-topic) :sl-buses (get (-> bus hash keyword)))
      fn-as-topic
      (let [published-topic (topicify fn-as-topic)
            new-meta (assoc (meta fn-as-topic) :sl-published published-topic
                                               :sl-buses (-> (get (meta fn-as-topic) :sl-buses #{}) (conj (-> bus hash keyword))))]
        (efn/Function. (fn [& args]
                         (let [ret (apply fn-as-topic args)]
                           (publish bus published-topic ret)
                           ret))
                       new-meta))))

  efn/Function
  (topicify [t]
    (or (publishized? t)
        (topicify (.-f t))))
  (publishized? [t]
    (:sl-published (meta t)))
  (publishize [fn-as-topic bus]
    (if (-> (meta fn-as-topic) :sl-buses (get (-> bus hash keyword)))
      fn-as-topic
      (let [published-topic (topicify fn-as-topic)
            new-meta (assoc (meta fn-as-topic) :sl-published published-topic
                                               :sl-buses (-> (get (meta fn-as-topic) :sl-buses #{}) (conj (-> bus hash keyword))))]
        (efn/Function. (fn [& args]
                         (let [ret (apply (.-f fn-as-topic) args)]
                           (publish bus published-topic ret)
                           ret))
                       new-meta))))

  swk/WorkerFn
  (topicify [t]
    (or (publishized? t)
        (-> t hash str)))
  (publishized? [t]
    (-> t hash str))
  (publishize [worker-as-topic bus]
    (let [published-topic (topicify worker-as-topic)
          bus-key (-> bus hash keyword)]
      (do
        (add-watch worker-as-topic bus-key #(publish bus published-topic {:old %3 :new %4}))
        worker-as-topic)))

  Atom
  (topicify [t]
    (or (publishized? t)
        (-> t hash str)))
  (publishized? [t]
    (-> t hash str))
  (publishize [atom-as-topic bus]
    (let [published-topic (topicify atom-as-topic)
          bus-key (-> bus hash keyword)]
      (do
        (add-watch atom-as-topic bus-key #(publish bus published-topic {:old %3 :new %4}))
        atom-as-topic)))

  default
  (topicify [t]
    (str t)))

