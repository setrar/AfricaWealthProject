;; Short Version
;; In the default package use the below libraries
(ns default
  (:require [clojure.set :as set])
  (:use (incanter core zoo stats charts io latex))
  (:require [clj-time.core :as time])
  (:use (clj-time [format :only (formatter formatters parse)]
          [coerce :only (to-long)])))

(defn dates-long
  "returns the dates as Unix long"
  [data]
  (let [ymd-formatter (formatters :year-month-day)
        dates-str	($ :Date data)]
    (map #(to-long (parse ymd-formatter %)) dates-str)))

;; Define the Quandl MtGox BTC/USD
;; Fetch the data always in Ascending Order
(def url "http://www.quandl.com/api/v1/datasets/BITCOIN/MTGOXUSD.csv?&trim_start=2010-07-01&trim_end=2013-07-01&sort_order=asc")
(def btc (read-dataset url :header true))

;; Convert to Zoo object
(def btc-z (zoo btc :Date))
;; ln(Pt)-ln(Pt-1) returns Zoo object
;; This is mostly 2 operations to reach ln(1/Pt-1 / 1/Pt)   pt-1 = 97.51 and Pt = 88.05
;; 1) Roll Apply (/ (div 97.51) (div 88.05) where is the inverse
;; 2) Apply (log from 1)
;;(def btc-div (zoo-apply #(apply - (div %)) 2 btc-zoo :Close))
;;(def btc-r (zoo-apply #(apply log %) 1 btc-div :Close))
(def btc-r (zoo-apply #(apply log %) 1 (zoo-apply #(apply / (div %)) 2 btc-z :Close) :Close))
;; Issue with zoo when row is null
(def btc-r ($ [:not 0] :all btc-r))

(view btc-r)

;; Descriptive Statistics
(def btc-lr ($ :Close btc-r))
(mean btc-lr) ;; 0.006929153189399101
(variance btc-lr) ;; 0.005812258211367765
(sd btc-lr) ;; 0.07623816768107537
(skewness btc-lr) ;; -0.04491679240743672
(kurtosis btc-lr) ;; 392704.64840013976 Looks very wrong

;; Displaying the data
(view (histogram btc-lr))
(view (time-series-plot (dates-long btc) ($ :Close btc-z) :x-label "Date" :y-label "BTC Simple Returns"))
(view (time-series-plot (dates-long btc) ($ :Close btc-r) :x-label "Date" :y-label "BTC Log Returns"))
