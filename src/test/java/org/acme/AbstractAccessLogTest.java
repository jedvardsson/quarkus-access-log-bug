package org.acme;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logmanager.ExtLogRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;

abstract class AbstractAccessLogTest {

    @ConfigProperty(name = "quarkus.management.enabled")
    boolean managementEnabled;

    @ConfigProperty(name = "quarkus.management.test-port")
    int managementTestPort;

    @ConfigProperty(name = "quarkus.http.test-port")
    int httpTestPort;

    @Test
    void testGreetingEndpoint() throws InterruptedException {
        try (LogCaptor captor = new LogCaptor("io.quarkus.http.access-log")) {
            given()
                .when().get("/hello")
                .then()
                .statusCode(200);

            BlockingQueue<ExtLogRecord> records = captor.getRecords();
            ExtLogRecord record = records.poll(1, TimeUnit.SECONDS);
            assertHttpRequest(record, "GET /hello HTTP/1.1");
        }
    }

    @Test
    void testHealthEndpoint() throws InterruptedException {
        String baseUri = "http://localhost:" + (managementEnabled ? managementTestPort : httpTestPort);
        try (LogCaptor captor = new LogCaptor("io.quarkus.http.access-log")) {
            given()
                .baseUri(baseUri)
                .when().get("/q/health")
                .then()
                .statusCode(200);

            BlockingQueue<ExtLogRecord> records = captor.getRecords();
            ExtLogRecord record = records.poll(1, TimeUnit.SECONDS);
            assertHttpRequest(record, "GET /q/health HTTP/1.1");
        }
    }

    private static void assertHttpRequest(ExtLogRecord record, String httpRequest) {
        Assertions.assertNotNull(record, "No log record found");
        Assertions.assertTrue(record.getMessage().contains(httpRequest), "contains: " + httpRequest);
    }
}
