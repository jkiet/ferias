# ferias (from latin)

## Assumptions

### Assumption1 - date range can be limited by implementation

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
Assumption1a -> scope for my implementation will be `this and next year` 
Assumption1b -> when request will be out of date range -> will respond 418
Assumption1c -> when request is within range but nothing found -> will respond 418
```

### Assumption2 - all dates should be treated as UTC 




## Usage

```
mvn clean && mvn package && java -jar target/ferias-0.0.1-SNAPSHOT.jar
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

- encoding problems -> cyrillic does not work (e.g `RU`)
- nice to have some cache for already parsed ical