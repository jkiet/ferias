# ferias 

Toy-project for exercise purposes.


#### Assumptions about reasonable scope of predictable holiday events 

##### Assumption #1 - date range can be limited by implementation

First, because it is api-specific.
Secondly, I found a domain-specific observation in this study:

https://link.springer.com/article/10.1007/s40565-018-0385-5 

I quote it:
```
An important issue is that the occurrence of public holidays (or bank holidays) is usually known years in advance. 
Public holidays are set by governments of a country, state or region and affect the human activity by law. 
From the energy and load forecasting perspective, these can be modeled appropriately. 
However, sometimes it happens that governments designate, suspend or carry over public holidays. 
These structural breaks in the holiday pattern complicate load forecasting. 
However, their impact is minor and is ignored in the modeling part of this article.
```

This observation is clearly visible in the case of the UK: 

https://www.gov.uk/bank-holidays

https://en.wikipedia.org/wiki/Public_holidays_in_the_United_Kingdom#Changes

```
Assumption#1a -> scope for my implementation will be `this and next year` 
Assumption#1b -> when request will be out of date range -> will respond 418
Assumption#1c -> when request is within range but nothing found -> will respond 404
```

##### Assumption #2 - all dates should be treated as UTC 


## Usage

Examples for `*sh` or `PS7+` 

```
mvn clean && mvn package && java -jar target/ferias-0.0.1-SNAPSHOT.jar
```

```
## PS
Invoke-RestMethod 'http://localhost:8080/holiday/next?country=PL&date=2021-01-01'

date       name
----       ----
2021-01-06 Dzien Trzech Króli   

## *sh + curl + jq
curl 'http://localhost:8080/holiday/next-twin?country1=PL&country2=CZ&date=2021-01-08' | jq

{
  "date": "2021-04-05",
  "name1": "drugi dzien Wielkiej Nocy",
  "name2": "Velikonoční pondělí"
}

## PS
Invoke-RestMethod 'http://localhost:8080/holiday/next-twin?country1=PL&country2=DE&date=2020-01-08' | Format-List

date  : 2020-04-12
name1 : Pierwszy dzien Wielkiej Nocy
name2 : Ostersonntag 
```

### prod/dev specific stuff (e.g api key) 

```
SPRING_CONFIG_NAME=prod java -jar target/ferias-0.0.1-SNAPSHOT.jar
# or
java -jar target/ferias-0.0.1-SNAPSHOT.jar --spring.config.name=dev
```

## Some countries manually tested 

BY, CZ, DE, DK, FI, FR, IT, NL, PL, PT, SE, SK



## Known problems

- ~~encoding problems -> cyrillic does not work (e.g `RU`)~~ localised (ics-local-name) version isn't available (isc / ics-clean only)
- ~~nice to have some cache for already parsed ical~~


