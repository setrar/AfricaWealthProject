(ns default
  (:use (incanter zoo core stats charts io))
  (:use (clj-time [format :only (formatter formatters parse)] [coerce :only (to-long)]))
  )

;;(def url "http://www.oanda.com/currency/historical-rates/download?quote_currency=CNY&end_date=2012-9-3&start_date=2012-3-8&period=daily&display=absolute&rate=0&data_range=d180&price=bid&view=graph&base_currency_0=INR&base_currency_1=&base_currency_2=&base_currency_3=&base_currency_4=&download=csv")
;; Define the MSCI EZA Yahoo URL
(def ezaUrl "http://ichart.finance.yahoo.com/table.csv?s=EZA&ql=5&ignore=.csv")

;; Fetch the data
(def eza (read-dataset ezaUrl :header true))

;; Scatter Plot Graphs
(view (scatter-plot :High :Low :data eza))
(view (scatter-plot :Close (keyword "Adj Close") :data eza))

;; clj-time uses Joda Time
;; In this case we only want to use clj-time.format and clj-time.coerce
(defn dates-long "returns the dates as long" [eza]
  (let [ymd-formatter (formatters :year-month-day) dates-str ($ :Date eza)]
    (map #(to-long (parse ymd-formatter %)) dates-str))
  )

;; Creating 2 separate sets of data, Prices and Time
(def eza-prices ($ (keyword "Adj Close")  eza))
(def eza-times (dates-long eza))

;; Let's view the Simple Return plot
(view (time-series-plot eza-times eza-prices :x-label "Date" :y-label "EZA Simple Return"))

;; Calculating the Log Return Time Series
(def eza-pdf (roll-apply #(apply - (log %)) 2 eza-prices))

;; Let's view the Log Return plot
(view (time-series-plot eza-times eza-pdf :x-label "Date" :y-label "EZA Log Return"))


(view eza)
(view eza-times)
(view eza-prices)
(view eza-pdf)

(log (/ 65.04 65.72))
