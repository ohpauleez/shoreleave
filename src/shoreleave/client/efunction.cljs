(ns shoreleave.client.efunction)

(deftype Function [f meta]
  
  IWithMeta
  (-with-meta  [F meta]  (Function. f meta))
  
  IMeta
  (-meta [F] meta)

  IFn
  (-invoke [F & args]
    (apply (.-f F) args))
  
  IHash
  (-hash [F] (goog.getUid (.-f F))))

