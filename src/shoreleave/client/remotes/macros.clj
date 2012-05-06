(ns shoreleave.client.remotes.macros)

(defmacro rpc
  [[sym & params] & [destruct & body]]
  (let [func (if destruct
               `(fn ~destruct ~@body)
               nil)]
    `(shoreleave.client.remotes.http-rpc/remote-callback ~(str sym)
                                                        ~(vec params)
                                                        ~func)))

(defmacro letrpc
  [bindings & body]
  (let [bindings (partition 2 bindings)]
    (reduce
      (fn [prev [destruct func]]
        `(rpc ~func [~destruct] ~prev))
      `(do ~@body)
      (reverse bindings))))

