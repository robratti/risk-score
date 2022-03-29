#Credit Risk Score
The application is consuming the input data, calculating the financial ratios and determining a credit risk 
rating for each app. The application has been tested on a Linux environment and should work even on Mac. No guarantees
will work even on PC.

##How to Run (Linux/Mac)
1. execute from the root project folder the following command: ```docker build --tag risk-score-app .```
2. once the image is built execute the following generating the report:
```
docker run -v $PWD:/home/app --rm risk-score-app
```
---
##Tests
1. run from the root project the following command: 
    ```./mvnw test```
2. Code Coverage Report: ```./mvnw -s ./.mvn/wrapper/settings.xml clean jacoco:prepare-agent install jacoco:report```