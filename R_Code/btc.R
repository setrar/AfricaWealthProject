# btc.R  		script file for BTC comparison with Incanter
#
# author: Setra R
# created: September 18, 2013
# revised: 

# Incanter displays 16 digits
options(digits=16)

# Let's play with BTC/USD
url <-'http://www.quandl.com/api/v1/datasets/BITCOIN/MTGOXUSD.csv?&trim_start=2010-07-01&trim_end=2013-07-01&sort_order=asc'
btc <- read.csv(url, colClasses=c('Date'='Date'))

# Let's display some data
plot(btc$Date,btc$Close)

# Close Field will be our reference
btc.prices <- as.ts(btc$Close)
btc.times <- as.Date(bt$Date,"%Y-%b-%d") 

# Log Return Calculation
btc.pdf <- log(lag(btc.prices))-log(btc.prices)
# or 
btc.pdf <- diff(log(btc.prices))

# Descriptive Statistics
head(btc.pdf)     # -0.006557005809394667  0.093736255674301994  0.016720593904617331 -0.155638269158978382  0.014417683305550710
mean(btc.pdf)     # 0.006749139231492089
var(btc.pdf)      # 0.005825567863660683
sd(btc.pdf)       # 0.07632540772023877

library(PerformanceAnalytics)
skewness(btc.pdf) # -0.05107132946313504
kurtosis(btc.pdf) # 10.17657793661099

# Displays the histogram
hist(btc.pdf)

require(zoo)
require(forecast) 

btc.z <- zoo(btc.prices,btc.times)
plot(btc.z, xlab="Date", ylab="BTC Simple Returns")

btc.r <- zoo(btc.pdf,btc.times)
plot(btc.r, xlab="Date", ylab="BTC Log Returns")


