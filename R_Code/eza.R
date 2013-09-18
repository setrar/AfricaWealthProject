# eza.R  		script file for MSCI EZA comparison with Incanter
#
# author: Setra R
# created: September 18, 2013
# revised: 

# Incanter displays 16 digits
options(digits=16)

# Let's play with MSCI EZA from Yahoo
url <-"http://ichart.finance.yahoo.com/table.csv?s=EZA&a=01&b=01&c=2010&d=01&e=01&f=2013&g=d&ignore=.csv"
eza <- read.csv(url, colClasses=c('Date'='Date'))

# Let's display some data
plot(eza$Date,eza$Close)

# Close Field will be our reference
eza.prices <- as.ts(eza$Adj.Close)

# Log Return Calculation
eza.pdf <- log(eza.prices)-log(lag(eza.prices))

# Descriptive Statistics
head(eza.pdf)     # 0.01996134628454538  0.01306806205664746 -0.01199829806680075  0.01122484300465132 -0.02035732656792355
mean(eza.pdf)     # 0.0004099698056707165
var(eza.pdf)      # 0.0003574576005939714
sd(eza.pdf)       # 0.01890654914557311

library(PerformanceAnalytics)
skewness(eza.pdf) # -0.3196135749374513
kurtosis(eza.pdf) # 2.423887802277989

# Displays the histogram
hist(eza.pdf)

require(zoo)
require(forecast) 
eza.times <- as.Date(eza$Date,"%Y-%b-%d") 
eza.z <- zoo(eza.prices,eza.times)
plot(eza.z, xlab="Date", ylab="EZA Simple Returns")

eza.r <- zoo(eza.pdf,eza.times)
plot(eza.r, xlab="Date", ylab="EZA Log Returns")