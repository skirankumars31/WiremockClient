package com.nav;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.restassured.response.Response;
import net.minidev.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.Result;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.RestAssured.given;

/**
 * Unit test for simple App.
 * Start remote wiremock server with command - java -jar wiremock-standalone-2.20.0.jar --port 9090 --local-response-templating
 */
public class AppTest extends TestWatcher {

    //Private WireMockServer wireMockServer;

    private static final Log LOGGER = LogFactory.getLog("vtp-testing");

    //= new WireMock("localhost", 9090);

    //wireMockServer.configureFor("localhost", 9090);

    @Override
    protected void succeeded(Description description) {
        LOGGER.debug(generateJsonFromLogMap("passed"));
    }

    @Override
    protected void failed(Throwable e, Description description) {
        LOGGER.debug(generateJsonFromLogMap("failed"));
    }

    @BeforeEach
    void configureSystemUnderTest() {
//        this.wireMockServer = new WireMockServer(options()
//                .extensions(new ResponseTemplateTransformer(false))
//                .port(9090));
//        this.wireMockServer.start();
        setupStub();
    }

   /* @AfterEach
    void stopWireMockServer() {

        this.wireMockServer.stop();
    }*/

    public void setupStub() {
        configureFor("localhost", 9090);
        stubFor(post(urlEqualTo("/an/endpoint"))
                .willReturn(aResponse().withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBodyFile("json/glossary.json")
                        .withTransformers("response-template")
                ));
    }

    private String generateJsonFromLogMap(String result) {

        Map<String, String> logMap = new HashMap<>();

        // START_DATE is a preset session value
        // Use @Given step when using JBehave to initialize START_DATE as a session value
        //Date startDate = (Date) Serenity.getCurrentSession().get(SessionValues.START_DATE);
        Date endDate = Calendar.getInstance().getTime();
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        long testDuration = endDate.getTime() - endDate.getTime(); //replace the date here

        // setting test outcome result
        logMap.put("result", result);

        // time-related values
        logMap.put("timestamp", df.format(endDate));
        logMap.put("duration", String.valueOf(testDuration));

        // system properties, provided via VM options on run, especially from Jenkins
        logMap.put("os", System.getProperty("os")); // vm option -Dos=OS_Name
        logMap.put("build_number", System.getProperty("buildNumber")); // vm option -DbuildNumber=${BUILD_NUMBER}

        JSONObject logEntries = new JSONObject(logMap);
        return logEntries.toJSONString();

    }

    @Test
    public void shouldAnswerWithTrue() {
        assert (true);
    }

    @Test
    @Disabled
    public void testStatusCodePositive() {

        JSONObject requestParams = new JSONObject();
        requestParams.put("name", "Rini");

        given().
                when().
                get("http://localhost:9090/an/endpoint", requestParams).
                then().
                assertThat().statusCode(200);
    }

    @Test
    @Disabled
    public void testStatusCodeNegative() {
        given().
                when().
                get("http://localhost:9090/another/endpoint").
                then().
                assertThat().statusCode(404);
    }

    @Test
    public void testResponseContents() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("name", "Rini");
        Response response = given().body(requestParams).when().post("http://localhost:9090/an/endpoint");
        String title = response.jsonPath().get("country");
        String name = response.jsonPath().get("Firstname");
        System.out.println(title);
        System.out.println(name);
        Assert.assertEquals("Norgee", title);
    }

}
