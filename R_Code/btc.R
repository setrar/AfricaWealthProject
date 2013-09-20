# btc.R  		script file for BTC comparison with Incanter
#
# author: Setra R
# created: September 18, 2013
# revised: 

# Incanter displays 16 digits
options(digits=16)

# Let's play with BTC/USD, Always in ascending order
url <-'http://www.quandl.com/api/v1/datasets/BITCOIN/MTGOXUSD.csv?&trim_start=2010-07-01&trim_end=2013-07-01&sort_order=asc'
btc <- read.csv(url, colClasses=c('Date'='Date'))

# Let's display some data
plot(btc$Date,btc$Close)

# Close Field will be our reference
btc.prices <- as.ts(btc$Close)
btc.times <- as.Date(btc$Date,"%Y-%b-%d") 

require(zoo)
require(forecast) 

# Let's convert to zoo Object
# It's easier to work with daily data
btc.z <- zoo(btc.prices,btc.times)
plot(btc.z, xlab="Date", ylab="BTC Simple Returns", col = "red")

# Log Return Calculation
btc.r <- diff(log(btc.z))
plot(btc.r, xlab="Date", ylab="BTC Log Returns", col = "red")

# Displays the histogram
hist(btc.r)

# Descriptive Statistics
tail(btc.r)     #           2013-06-30           2013-07-01 
                #  0.02608857161767820 -0.10205010190481199 
mean(btc.r)     # 0.006929153189399093
var(btc.r)      # 0.005812258211367765
sd(btc.r)       # 0.07623816768107537

library(PerformanceAnalytics)
skewness(btc.r) # -0.04497924912362074
kurtosis(btc.r) # 10.29118737978867

