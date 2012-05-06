(ns shoreleave.client.remotes.http-rpc
  (:require [shoreleave.client.remotes.xhr :as xhr]
            [goog.structs.PriorityPool :as priority]
            [cljs.reader :as reader]))

(def ^:dynamic *remote-uri* "/_fetch")

; TODO I believe there is an error with the xhrManager getting back Clojure data, but I can't confirm it
#_(defn remote-callback [remote params callback & extra-content]
  (xhr/request [:post *remote-uri*]
               :content (merge
                          {:remote remote
                           :params (pr-str params)}
                          (apply hash-map extra-content))
               :priority (dec priority/DEFAULT_PRIORITY_)
               :on-success (when callback
                             #(->> % :body
                                (fn [data]
                                  (let [data (if (= data "") "nil" data)]
                                    (callback (reader/read-string data))))))
               :on-error #(->> % :event js/console.log)))

(defn remote-callback [remote params callback & extra-content]
  (xhr/xhr [:post *remote-uri*]
           :content (merge
                      {:remote remote
                       :params (pr-str params)}
                      (apply hash-map extra-content))
           :on-success (when callback
                         (fn [data]
                           (let [data (if (= data "") "nil" data)]
                             (callback (reader/read-string data)))))))

