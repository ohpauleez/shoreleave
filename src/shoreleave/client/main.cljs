(ns shoreleave.client.main
  (:require [crate.core :as crate]
            [fetch.remotes :as remotes]
            [enfocus.core :as ef]
            [shoreleave.client.brepl :as brepl]
            [shoreleave.client.remote :as remote-resource])
  (:use [jayq.core :only [$ append delegate data]])
  (:require-macros [enfocus.macros :as em]
                   [fetch.macros :as fm])
  (:use-macros [crate.macros :only [defpartial]]))

; Store off the body using jQuery
(def $body ($ :body))

; Here's how you create JS objects and call JS methods
(def query-args (goog.Uri.QueryData. (subs window.location.search 1)))
(def query-arg-map (zipmap (map keyword (.getKeys query-args)) (.getValues query-args)))

; Modify/Manip the DOM with Enfocus - uses CSS3 selectors
(defn body-replace [new-content]
  (em/at js/document
    ["body"] (em/content)))

; Get an HTML snippet, a CLJ snippet, any resource (enfocus templates even).
; Here, we're expecting a Clojure Map back
(def tutor-snip
  (remote/request
    100
    "profiles/tutor/100"
    :on-success #(->> % :body body-replace)
    :on-error #(->> % :status (str "*ERROR* ") js/alert)))

; Perform an RPC call ON the server.  The server must define a `(defremote ...)`
; The return of the RPC call is sent to the client here.
;
; `say-hello` is defined on the server, and takes no arguments
(fm/remote (say-hello) [result]
  (js/alert result))

; We can also define HTML snippets here to save us from having to fetch an html resource
; This partial can be used to make custom buttons...
; (button :label my-label, :action js-explode-effect :data-param "")
(defpartial button [{:keys [label action param]}]
  [:a.button {:href "#" :data-action action :data-param param} label])

; You can always connect to the browser repl if `brepl` is a query arg
(when (.containsKey query-args "brepl")
  (brepl/start-repl))
;; The above can also use standard clojure data, if it's available
;; (when-not (:brepl query-arg-map)

