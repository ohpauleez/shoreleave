(ns shoreleave.client.services.geo
  (:require [shoreleave.client.common :as common]))

;; TODO replace this with: http://maps.google.com/maps/geo?q=nyc&sensor=false
;; and http://closure-library.googlecode.com/svn/docs/class_goog_net_Jsonp.html
;; goog.net.Jsonp(uri, opt_callbackParamName) 

;; TODO We should do an include-js function that pulls in google maps:
;; <script src="http://maps.google.com/maps/api/js?sensor=false"></script> 
;;
;;<script>window.jQuery || document.write('<script src="../public/js/jquery-1.7.1.min.js"><\/script>')</script> 

;; TODO: I really need to extern these in here
(def geocoder (js/google.maps.Geocoder.))

;; TODO: if we want the zip, it's the last element in (.-address_components geo-obj)
(defn mapify-location [geocode-results]
  (let [[geo-obj] geocode-results
        lat (.geometry.location.lat geo-obj)
        lng (.geometry.location.lng geo-obj)]
    {:lat         lat
     :lng         lng
     :latlng-str  (str lat "," lng)
     :formatted   geo-obj.formatted_address}))

(defn normalize-location
  ([loc-str callback]
   (normalize-location loc-str callback mapify-location))
  ([loc-str callback process-location-fn]
   (.geocode geocoder
     (common/clj->js {:address loc-str})
     #(callback (process-location-fn %1)))))

;; This version should probably be used instead of `#(callback ...)
;; It makes sure that the call was successful
#_(fn [data status]
       (when (= status (js/google.maps.GeocoderStatus.OK))
         (callback (mapify-location data))))
