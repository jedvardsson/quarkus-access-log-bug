package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;

import java.util.Map;

@QuarkusTest
@TestProfile(ManagementEnabledAccessLogTest.TestProfile.class)
class ManagementEnabledAccessLogTest extends AbstractAccessLogTest {

    public static class TestProfile implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of("quarkus.management.enabled", "true");
        }
    }
}
