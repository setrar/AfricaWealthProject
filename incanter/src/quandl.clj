"""
(def mtgoxUSD (get-symbol-from-quandl 'BITCOIN/MTGOXUSD' '2012-01-19' '2012-01-20'))
(def url 'http://www.quandl.com/api/v1/datasets/BITCOIN/MTGOXUSD.csv?&trim_start=2010-07-17&trim_end=2013-07-08&sort_order=asc')
"""
(ns default
  (:require [clojure.set :as set])
  (:use (incanter core zoo stats charts io latex))
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
              "&trim_end=" y1 "-" m1 "-" d1 "&sort_order=asc"
              "&auth_token=" token)]
    (print uri)
    (-> (read-dataset uri :header true)
        ;;Date	Open	High	Low	Close	Volume (BTC)	Volume (Currency)	Weighted Price
        (col-names [:Date :open :high :low :close :volume1 :volume2 :adj-close])
      )
    )
  )

;; Running code starts here

;; Replace with your own token
(def quandl_token "PROVIDE YOUR OWN")
(def startDate "2013-07-01")
(def endDate "2013-09-01")


(def btc (get-symbol-from-quandl "BITCOIN/MTGOXUSD" startDate endDate quandl_token))
(def aapl (get-symbol-from-quandl "GOOG/NASDAQ_AAPL" startDate endDate quandl_token))


(same-dates? aapl btc)	; true

(def aapl-times (dates-long aapl))
(def aapl-ac ($ :close aapl))

(def btc-times (dates-long btc))
(def btc-ac ($ :adj-close btc))


(apply max ($ :adj-close btc))
(apply min ($ :adj-close btc))

;; log return equation
(def eq_ln (str "$rt = log ( 1 + R) = log\\left(\\frac{Price(t)}{Price(t-1)}\\right)"))

;; Convert to Zoo object
(def btc-zoo (zoo btc :Date))
;; ln(Pt)-ln(Pt-1) returns Zoo object
(def btc-z (zoo-apply #(apply - (log %)) 2 btc-zoo :adj-close))
;; TBD this doesn't work. Zoo needs to be sorted Log is negative

(view btc-z)

;; ln(Pt)-ln(Pt-1) returns Regular object
(def btc-r (roll-apply #(apply - (log %)) 2 ($ :adj-close btc)))

(view btc-r)

;; This works as Zoo Objects but can't be used with view
(view (time-series-plot aapl-times aapl-ac :x-label "Date" :y-label "AAPL Simple Returns"))
(view (time-series-plot btc-times btc-ac :x-label "Date" :y-label "BTC Simple Returns"))

(doto (time-series-plot btc-times btc-r :x-label "Date" :y-label "BTC Log Returns")
      (add-latex-subtitle eq_ln)
      view)

;; Mean
(def mu (mean btc-r))
(def var (variance btc-r))
(def sdv (sd btc-r))

;; Descriptive Statistics
(view (histogram btc-r))
(view (box-plot btc-r))

;; Bell Curve Equation
(def eq (str "$f(x,\\mu ,\\sigma^2) = \\frac{1}{\\sigma \\sqrt{2\\pi}}"
          "e^{-\\frac{1}{2}(\\frac{x-\\mu}{\\sigma})^2}"))

(doto (function-plot pdf-normal -3 3)
  (add-latex-subtitle eq)
  view)

(def x (range -3 3 0.01))

(doto (xy-plot x (pdf-normal x)
;;        :title "Normal PDF"
        :y-label "Density"
        :legend true)
  (add-latex-subtitle eq)
  (add-lines x (pdf-normal x :sd (sqrt 0.2)))
  (add-lines x (pdf-normal x :sd (sqrt 5.0)))
  (add-lines x (pdf-normal x :mean -2 :sd (sqrt 0.5)))
  (add-lines x (pdf-normal x :mean mu :sd sdv))
  view)

