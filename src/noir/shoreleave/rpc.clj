(ns noir.shoreleave.rpc
  (:use [noir.core :only [defpage]]
        [noir.server :only [load-views-ns]]))

(def remote-uri "/_fetch")
(def remotes (atom {}))

(defn get-remote [remote]
  (get @remotes remote))

(defn add-remote [remote func]
  (swap! remotes assoc (keyword (name remote)) func))

(defn safe-read [s]
  (binding [*read-eval* false]
    (read-string s)))

(defmacro defremote [remote params & body]
  `(do
    (defn ~remote ~params ~@body)
    (add-remote ~(name remote) ~remote)))

(defn remote-ns [namesp-sym & opts]
  (let [{:keys [as]} (apply hash-map opts)
        namesp (try
                 (load-views-ns namesp-sym)
                 (find-ns namesp-sym)
                 (catch Exception e
                   (throw (Exception. (str "Could not locate a namespace when aliasing remotes: " namesp-sym))
                          e)))
        public-fns (ns-publics namesp)]
    (doseq [[fn-name fn-var] public-fns]
      (add-remote (str as "/" fn-name) fn-var))))

(defn call-remote [remote params]
  (if-let [func (get-remote remote)]
    (let [result (apply func params)]
      {:status 202
       :headers {"Content-Type" "application/clojure; charset=utf-8"}
       :body (pr-str result)})
    {:status 404}))

#_(defpage [:post remote-uri] {:keys [remote params]}
  (let [params (safe-read params)
        remote (keyword remote)]
    (call-remote remote params)))

(defmacro defremote-uri [endpoint-uri]
  `(defpage [:post ~endpoint-uri] args#
     (let [params# (safe-read (:params args#))
           remote# (keyword (:remote args#))]
       (call-remote remote# params#))))

(defn activate-remotes!
  ([]
   (activate-remotes! remote-uri))
  ([endpoint-uri]
   (defremote-uri endpoint-uri)))

