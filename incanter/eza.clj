(ns examples.CourseraTs
  (:use (incanter zoo core stats charts io))
  (:use (clj-time [format :only (formatter formatters parse)] [coerce :only (to-long)]))
  )
;;(def url "http://www.oanda.com/currency/historical-rates/download?quote_currency=CNY&end_date=2012-9-3&start_date=2012-3-8&period=daily&display=absolute&rate=0&data_range=d180&price=bid&view=graph&base_currency_0=INR&base_currency_1=&base_currency_2=&base_currency_3=&base_currency_4=&download=csv")
;; Define the MSCI EZA Yahoo URL
(def ezaUrl "http://ichart.finance.yahoo.com/table.csv?s=EZA&ql=5&ignore=.csv")
;; Fetch the data
(def eza (read-dataset ezaUrl :header true))
;; Plot a graph
(view (scatter-plot :High :Low :data eza))
(view (scatter-plot :Close (keyword "Adj Close") :data eza))

;; clj-time uses Joda Time
;; In this case we only want to use clj-time.format and clj-time.coerce
(defn dates-long "returns the dates as long" [eza]
  (let [ymd-formatter (formatters :year-month-day) dates-str ($ :Date eza)]
    (map #(to-long (parse ymd-formatter %)) dates-str))
  )
;; Bug when using :Adj-Close
;;(def eza-open ($ :Open eza))
(def eza-ac ($ (keyword "Adj Close")  eza))
;; no replace col func (def gdx-times (dates-long gdx))
(def eza-times (dates-long eza))

;;(view (time-series-plot eza-times eza-open :x-label "Date" :y-label "EZA"))
(view (time-series-plot eza-times eza-ac :x-label "Date" :y-label "EZA"))

(def eza.zoo (zoo eza))

