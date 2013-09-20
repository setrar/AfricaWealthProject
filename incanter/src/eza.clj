(ns default
  (:use (incanter zoo core stats charts io))
  (:use (clj-time [format :only (formatter formatters parse)] [coerce :only (to-long)]))
  )

(defn dates-long
  "returns the dates as Unix long"
  [data]
  (let [ymd-formatter (formatters :year-month-day)
        dates-str	($ :Date data)]
    (map #(to-long (parse ymd-formatter %)) dates-str)))

;; Works but needs manipulation
;;(def url "http://www.oanda.com/currency/historical-rates/download?quote_currency=CNY&end_date=2012-9-3&start_date=2012-3-8&period=daily&display=absolute&rate=0&data_range=d180&price=bid&view=graph&base_currency_0=INR&base_currency_1=&base_currency_2=&base_currency_3=&base_currency_4=&download=csv")
;; Define the MSCI EZA Yahoo URL
;;(def url "http://ichart.finance.yahoo.com/table.csv?s=EZA&ql=5&ignore=.csv")

;; Define the MSCI EZA Yahoo URL where the FROM date is: &a=01&b=10&c=2010 and the TO date is: &d=01&e=19&f=2010
(def url "http://ichart.finance.yahoo.com/table.csv?s=EZA&a=01&b=01&c=2010&d=01&e=01&f=2013&g=d&ignore=.csv")
(def eza (read-dataset url :header true))


;; Scatter Plot Graphs
(view (scatter-plot :High :Low :data eza))
(view (scatter-plot :Close (keyword "Adj Close") :data eza))
(view eza)

;; Creating 2 separate sets of data, Prices and Time
(def eza-prices ($ (keyword "Adj Close")  eza))
(def eza-times (dates-long eza))
;; Calculating the Log Return Time Series
(def eza-pdf (roll-apply #(apply - (log %)) 2 eza-prices))

(view eza-prices)
(view eza-pdf)

;; Descriptive Statistics
(head eza-pdf) ;; (0.019961346284545378 0.013068062056647456 -0.01199829806680075 0.01122484300465132 -0.0203573265679235
(mean eza-pdf) ;;  4.0996980567071655E-4
(variance eza-pdf) ;; 3.57457600593971E-4
(sd eza-pdf) ;; 0.0189065491455731
(skewness eza-pdf) ;; -0.3189796307721639
(kurtosis eza-pdf) ;; 4.233620319575951E-7

(kurtosis (sample-normal 100000))
(kurtosis (sample-gamma 100000))

(def eza-hist (histogram eza-pdf))
(save eza-hist "eza-hist.png" :width 1000)

;; Let's view the Simple Return plot
(def eza-sr-plot (time-series-plot eza-times eza-prices :x-label "Date" :y-label "EZA Simple Return"))
(save eza-sr-plot "eza-sr-plot.png" :width 1000)
;; Let's view the Log Return plot
(def eza-lr-plot (time-series-plot eza-times eza-pdf :x-label "Date" :y-label "EZA Log Return"))
(save eza-lr-plot "eza-lr-plot.png" :width 1000)
