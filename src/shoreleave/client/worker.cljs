(ns shoreleave.client.worker
  (:require [shoreleave.client.efunction :as efn]
            [shoreleave.client.blob :as blob]
            [cljs.reader :as reader]))

(deftype WorkerFn [F eworker res-atom-vector]
  
  IWithMeta
  (-with-meta [WFn meta]
    (WorkerFn. (with-meta (.-F WFn) meta) (.-eworker WFn) (.-res-atom-vector WFn)))
  
  IMeta
  (-meta [WFn] (meta (.-F WFn)))

  IFn
  (-invoke [WFn arg]
    (let [w (.-eworker WFn)]
      (.postMessage w arg)))

  IDeref
  (-deref [WFn]
    (-deref (.-res-atom-vector WFn)))

  IWatchable
  (-add-watch [WFn key f]
    (-add-watch (.-res-atom-vector WFn) key f))
  (-remove-watch [WFn key]
    (-remove-watch (.-res-atom-vector WFn) key))
  ;#_(-notify-watches [WFn oldval newval]
  ;  (-notify-watches (.-res-atom-vector WFn) oldval newval))
  
  IHash
  (-hash [WFn] (goog.getUid (.-f (.-F WFn))))
  )

(defn worker [f]
  (let [f-str (str f)
        fn-str (str "var swfn = " f-str ";"
                    "self.onmessage = function(c){self.postMessage(swfn(c.data));return c};")
        bb (blob/blob-builder)
        *b (conj! bb fn-str)
        blobb (blob/blob bb)
        agg (atom [])
        w (js/Worker. (blob/object-url! blobb))
        *w (set! (.-onmessage w) #(swap! agg conj (.-data %)))
        wfn (WorkerFn. (efn/Function. f {}) w agg)]
    wfn))

