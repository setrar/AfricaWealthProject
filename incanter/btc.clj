;; In the default package use the below libraries
(ns default (:use (incanter core stats charts io)))
;; Define the MSCI EZA Yahoo URL
(def url "http://www.quandl.com/api/v1/datasets/BITCOIN/MTGOXUSD.csv?&trim_start=2010-07-17&trim_end=2013-07-08&sort_order=desc")
;; Fetch the data
(def btc (read-dataset url :header true))
;; Plot a graph
(view (scatter-plot :High :Low :data btc))

(view btc)

(def btc-pdf (roll-apply #(apply - (log %)) 2 ($ :Close btc)))

(def btc-times (dates-long btc))

(view btc-pdf)
(mean btc-pdf)
(variance btc-pdf)
(sd btc-pdf)
(skewness btc-pdf)
(kurtosis btc-pdf)


(view (time-series-plot btc-times btc-pdf :x-label "Date" :y-label "BTC Log Returns"))
