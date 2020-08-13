package org.gba.petstore;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class Hooks {

    private WireMockServer wireMockServer;

    @Before(value = "@e2e", order = 0)
    public void beforeE2e(Scenario scenario) {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
    }

    @Before("@stubbed")
    public void beforeStubbed(Scenario scenario) {
        wireMockServer = new WireMockServer(wireMockConfig().port(8089));
        wireMockServer.start();
        RestAssured.baseURI = "http://localhost:8089";
    }

    @After("@stubbed")
    public void afterStubbed(Scenario scenario) {
        wireMockServer.stop();
    }

    @Before("@debug")
    public void beforeDebug(Scenario scenario) {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @After("@debug")
    public void afterDebug(Scenario scenario) {
        RestAssured.reset();
    }

    @Before(value = "@v1")
    public void beforeV1(Scenario scenario) {
        RestAssured.baseURI = "https://petstore.swagger.io/v1";
    }

    @After(value = "@v1")
    public void afterV1(Scenario scenario) {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
    }

}
