##Overall info
This app is compiled using Java 17. It runs on top of Spring Boot and uses H2 as database.

## Building and tests
./gradlew build

## Starting the app 
./gradlew bootRun
Application runs at http://localhost:8080/

## Swagger
http://localhost:8080/swagger-ui/#/

## Curls examples
You can find curl examples in root/examples directory
Test cases also provides examples and data to test corner cases.

## Know issues
- Resource names (Weapon, Ammunition, Water and Food ) are case-sensitive and are not being validated
during Rebel creation.
- Rebel Entity is returned in some APIs instead of and proper DTO.
- Missing test cases to some edge cases such division by 0 in the reports.

### This app was created to address Let's Code challenge and was created by Guilherme Mendes.