(ns ^{:doc "Make network requests.

  Adapted from ClojureScript:One which is...

  Adapted from Bobby Calderwood's Trail framework:
  <https://github.com/bobby/trail>
  
  Enahnced to support uniform calling format and CSRF protection"}
  shoreleave.client.remotes.request
  (:require [cljs.reader :as reader]
            [clojure.browser.event :as event]
            [goog.net.XhrManager :as manager]
            [shoreleave.client.remotes.common :as common])
  (:use [shoreleave.client.common :only [clj->js]]))

(def ^:private responders (atom {}))

(defn- add-responders [id success error]
  (when (or success error)
    (swap! responders assoc id {:success success :error error})))

(extend-type goog.net.XhrManager

  event/EventType
  (event-types [this]
    (into {}
          (map
           (fn [[k v]]
             [(keyword (. k (toLowerCase)))
              v])
           (js->clj goog.net.EventType)))))

(def ^:private
  *xhr-manager*
  (goog.net.XhrManager. nil
                        nil
                        nil
                        0
                        5000))

(defn request
  "Asynchronously make a network request for the resource at url. If
  provided via the `:on-success` and `:on-error` keyword arguments, the
  appropriate one of `on-success` or `on-error` will be called on
  completion. They will be passed a map containing the keys `:id`,
  `:body`, `:status`, and `:event`. The entry for `:event` contains an
  instance of the `goog.net.XhrManager.Event`.

  URLs/Routes can be expressed as `\"http://www.google.com\"`
  or as `[:post \"http://www.google.com\"]`
  The default method is GET.

  Other allowable keyword arguments are `:id`, `:content`, `:headers`,
  `:priority`, and `:retries`. `:id` defaults to a random string,
  `:retries` defaults to `0`."
  [route & {:keys [id content headers priority retries on-success
                    on-error]
             :or   {id (common/rand-id-str), retries 0}}]
  (let [[method uri] (common/parse-route route)]
    (try
      (add-responders id on-success on-error)
      (.send *xhr-manager*
             id
             uri
             method
             (when content (common/->data-str
                             (common/csrf-protected content method)))
             (clj->js headers)
             priority
             ;; This next one is a callback, and we could use it to get
             ;; rid of the atom and figure out success/failure ourselves
             nil
             retries)
      (catch js/Error e
        nil))))

(defn- response-success [e]
  (when-let [{success :success} (get @responders (:id e))]
    (success e)
    (swap! responders dissoc (:id e))))

(defn- response-error [e]
  (when-let [{error :error} (get @responders (:id e))]
    (error e)
    (swap! responders dissoc (:id e))))

(defn- response-received
  [f e]
  (f {:id     (.-id e)
      :body   (.getResponse e/xhrIo)
      :status (.getStatus e/xhrIo)
      :event  e}))

(event/listen *xhr-manager* "success" (partial response-received response-success))
(event/listen *xhr-manager* "error"   (partial response-received response-error))

