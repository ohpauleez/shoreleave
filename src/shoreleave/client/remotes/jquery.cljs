(ns shoreleave.client.remotes.jquery
  (:use [shoreleave.client.common :only [clj->js]]))

(defn ajax
  "This uses jQuery's ajax request to perform a remote
  (potentially cross-origin or jsonp) request"
  [url & kw-opts]
  (let [kw-map (apply hash-map kw-opts)]
    (.ajax js/jQuery (clj->js (assoc kw-map :url url)))))

