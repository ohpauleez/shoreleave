(ns shoreleave.client.common
  (:require [shoreleave.client.brepl :as brepl])
  (:use [jayq.util :only [clj->js]]))

;; Ignore the warning this generates
(def clj->js clj->js)

(defn args-map [location-str]
  (let [query-args-obj (goog.Uri.QueryData. (if (contains? #{\# \?} (get location-str 0))
                                              (subs location-str 1)
                                              location-str))]
    (zipmap (map keyword (.getKeys query-args-obj)) (.getValues query-args-obj))))

(defn query-args-map []
  (args-map js/window.location.search))
(defn hash-args-map []
  (args-map js/window.location.hash))

(defn set-window-hash-args [args-map]
  (let [hash-str (reduce (fn [old-str [k v]] (str old-str (name k) "=" v "&")) "#" args-map)
        clean-hash-str (subs hash-str 0 (dec (count hash-str)))]
    (set! js/window.location.hash clean-hash-str)))

(defn toggle-brepl
  ([]
    (toggle-brepl (query-args-map)))
  ([query-map]
    (toggle-brepl query-map :brepl))
  ([query-map query-key]
    (when (query-map query-key)
      (brepl/connect))))

