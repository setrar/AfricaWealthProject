"""
(def mtgoxUSD (get-symbol-from-quandl 'BITCOIN/MTGOXUSD' '2012-01-19' '2012-01-20'))
(def url 'http://www.quandl.com/api/v1/datasets/BITCOIN/MTGOXUSD.csv?&trim_start=2010-07-17&trim_end=2013-07-08&sort_order=desc')
"""
(ns default
  (:require [clojure.set :as set])
  (:use (incanter core zoo stats charts io, excel))
  (:require [clj-time.core :as time])
  (:use (clj-time [format :only (formatter formatters parse)]
          [coerce :only (to-long)])))

;; Function Definition
(defn same-dates?
  "are two datasets covering the same time frame?"
  [x y]
  (let [x-dates	(into #{} ($ :Date x))
        y-dates	(into #{} ($ :Date y))
        x-y	(set/difference x-dates y-dates)
        y-x	(set/difference y-dates x-dates)]
    (and (empty? x-y) (empty? y-x))))

(defn dates-long
  "returns the dates as long"
  [data]
  (let [ymd-formatter (formatters :year-month-day)
        dates-str	($ :Date data)]
    (map #(to-long (parse ymd-formatter %)) dates-str)))

(defn get-symbol-from-quandl
  "returns a dataset read from a remote URL given a QUANDL symbol name"
  ;;  ([symbol] (get-symbol-from-yahoo symbol (earlier (joda-date) (period 5 :year)) (joda-date)) (token))
  [yf-symbol from to yf-token]
  (let [symbol	(.toUpperCase yf-symbol)
        [y0 m0 d0] (re-seq #"\d+" from)
        [y1 m1 d1] (re-seq #"\d+" to)
        token yf-token
        uri (str "http://www.quandl.com/api/v1/datasets/" symbol ".csv?"
              "trim_start=" y0 "-" m0 "-" d0
              "&trim_end=" y1 "-" m1 "-" d1 "&sort_order=desc"
              "&auth_token=" token)]
    (print uri)
    (-> (read-dataset
          uri
          :header true)
    ;;Date	Open	High	Low	Close	Volume (BTC)	Volume (Currency)	Weighted Price
      (col-names
        [:index :open :high :low :close :volume1 :volume2 :adj-close]))
    )
  )

;; Running code starts here

;; Replace with your own token
(def quandl_token "YOUR TOKEN HERE")
(def startDate "2013-06-11")
(def endDate "2013-07-10")


(def btc (get-symbol-from-quandl "BITCOIN/MTGOXUSD" startDate endDate quandl_token))
(def aapl (get-symbol-from-quandl "GOOG/NASDAQ_AAPL" startDate endDate quandl_token))


(same-dates? aapl btc)	; true

(def aapl-ac ($ :close aapl))
(def btc-ac ($ :adj-close btc))

(def aapl-times (dates-long aapl))
(def btc-times (dates-long btc))

(apply max ($ :adj-close btc))
(apply min ($ :adj-close btc))

;; ln(Pt)-ln(Pt-1) returns Zoo object
(def btc-z (zoo (zoo-apply #(apply - (log %)) 2 btc :adj-close)))

;; This works as Zoo Objects but can't be used with view
(view (time-series-plot aapl-times btc-z :x-label "Date" :y-label "AAPL"))
(view (time-series-plot snp-times snp-ac :x-label "Date" :y-label "SnP"))
