(ns shoreleave.client.blob)


(defn- blobber
  ([]
   (blobber js/window))
  ([w]
   (or (.-BlobBuilder w) (.-WebKitBlobBuilder w) (.-MozBlobBuilder w))))

;; Make sure we have a top-level BlobBuiler - this is for protocol sake
(set! (.-BlobBuilder js/window) (blobber))

(defn blob-builder [] 
   (js/window.BlobBuilder.))

(extend-type js/window.BlobBuilder
  
  ITransientCollection
  ;(-persistent! [blobber] (as-vector blobber))
  (-conj! [blobber str-piece]
    (.append blobber str-piece)))

(defn blob [blobber]
  (.getBlob blobber))

(defn object-url! [file-or-blob]
  (let [url (or (.-URL js/window) (.-webkitURL js/window))]
    (.createObjectURL url file-or-blob)))

