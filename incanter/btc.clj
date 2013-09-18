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

;; Define the MSCI EZA Yahoo URL
;; Fetch the data
(def url "http://www.quandl.com/api/v1/datasets/BITCOIN/MTGOXUSD.csv?&trim_start=2010-07-17&trim_end=2013-07-08&sort_order=desc")
(def btc (read-dataset url :header true))

;; Plot graph and view data
(view (scatter-plot :High :Low :data btc))
(view btc)

(def btc-prices ($ :Close btc))
(def btc-times (dates-long btc))
;; Log Return Calculation
(def btc-pdf (roll-apply #(apply - (log %)) 2 btc-prices))

(view btc-prices)
(view btc-pdf)

;; Descriptive Statistics
(head btc-pdf) ;; (-0.006557005809394667 0.093736255674302 0.01672059390461733 -0.15563826915897838 0.01441768330555071
(mean btc-pdf) ;; 0.006749139231492085
(variance btc-pdf) ;; 0.005825567863660677
(sd btc-pdf) ;; 0.07632540772023873
(skewness btc-pdf) ;; -0.051000870058728595
(kurtosis btc-pdf) ;; 387546.04030830524 Looks very wrong

(view (histogram btc-pdf))
(view (time-series-plot btc-times btc-prices :x-label "Date" :y-label "BTC Simple Returns"))
(view (time-series-plot btc-times btc-pdf :x-label "Date" :y-label "BTC Log Returns"))


