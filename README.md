# petstore

## building project
Run `./gradlew clean build` will build project and run tests, add `-x test` option to skip running tests

## Running tests
- `./gradlew cucumber` to invoke tests via cucumber cli
- `./gradlew test ` to invoke tests via cucumber junit runner
- Run junit cucumber runner directly via `RunTests` class

## debugging failures
Add @debug annotation above scenario to see full http traffic output

## test options 
@e2e will run against remote api located at: https://petstore.swagger.io/v2
@stubbed will start local wiremock server on localhost:8089 and run against stubs located at resources/mappings. 

## reports
In addition to console html report will be generated in: build/cucumber-reports.html