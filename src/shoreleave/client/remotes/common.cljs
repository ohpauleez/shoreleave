(ns shoreleave.client.remotes.common
  (:require [clojure.string :as cstr]
            [goog.Uri.QueryData :as query-data]
            [goog.structs :as structs]
            [goog.string :as gstr]
            [shoreleave.client.cookies :as cookies])
  (:use [shoreleave.client.common :only [clj->js]]))

(defn rand-id-str []
  (gstr/getRandomString))

(defn ->url-method [m]
  (cstr/upper-case (name m)))

(defn parse-route [route]
  (cond
    (string? route) ["GET" route]
    (vector? route) (let [[m u] route]
                      [(->url-method m) u])
    :else ["GET" route]))

(defn ->simple-callback [callback]
  (when callback
    (fn [req]
      (let [data (.getResponseText req)]
        (callback data)))))

(defn csrf-protected [content-map method]
  (if-let [anti-forgery-token (and (= method "POST")
                                   (:__anti-forgery-token cookies/cookies))]
    (merge content-map {:__anti-forgery-token anti-forgery-token})
    content-map))

(defn ->data-str [d]
(let [cur (clj->js d)
      query (query-data/createFromMap (structs/Map. cur))]
  (str query)))
 
