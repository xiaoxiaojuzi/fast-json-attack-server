package org.example.poorApi.web;

import org.example.poorApi.web.core.BaseApiTestWithServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SensitiveDataControllerTest extends BaseApiTestWithServer {
    private ResponseEntity<String> getSensitiveData(Map<String, String> payload) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        return client.postForEntity(
            "/api/sensitive",
            new HttpEntity<>(payload, headers),
            String.class);
    }

    @Test
    void should_fail_to_get_sensitive_data_if_nothing_is_provided() {
        final ResponseEntity<String> response = getSensitiveData(Collections.emptyMap());

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void should_fail_to_get_sensitive_data_if_provided_json_contains_no_token_value_or_credential() {
        Map<String, String> invalidPayload = new HashMap<>();
        invalidPayload.put("unknownKey", "unknownValue");
        invalidPayload.put("anotherUnknownKey", "p@ssword");

        final ResponseEntity<String> response = getSensitiveData(invalidPayload);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void should_fail_to_get_sensitive_data_if_token_value_is_invalid() {
        Map<String, String> invalidToken = Collections.singletonMap("value", "invalid-token-value");

        final ResponseEntity<String> response = getSensitiveData(invalidToken);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void should_get_sensitive_data_if_token_value_is_correct() {
        Map<String, String> token = Collections.singletonMap("value", "safe-token-value");

        final ResponseEntity<String> response = getSensitiveData(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Some sensitive data", response.getBody());
    }

    private static Stream<Arguments> createInvalidCredentials() {
        return Stream.of(
            Arguments.of("invalid-username", "invalid-password"),
            Arguments.of("invalid-username", "p@ssword"),
            Arguments.of("admin", "invalid-password")
        );
    }

    @ParameterizedTest
    @MethodSource("createInvalidCredentials")
    void should_fail_to_get_sensitive_data_if_credential_is_incorrect(String username, String password) {
        Map<String, String> invalidCredential = new HashMap<>();
        invalidCredential.put("username", username);
        invalidCredential.put("password", password);

        final ResponseEntity<String> response = getSensitiveData(invalidCredential);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void should_get_sensitive_data_if_credential_is_correct() {
        Map<String, String> correctCredential = new HashMap<>();
        correctCredential.put("username", "admin");
        correctCredential.put("password", "p@ssword");

        final ResponseEntity<String> response = getSensitiveData(correctCredential);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Some sensitive data", response.getBody());
    }

    @Test
    void should_return_bad_request_if_request_is_of_malformed_type(){
        Map<String, String> correctCredential = new HashMap<>();
        correctCredential.put("@type", "com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl");

        final ResponseEntity<String> response = getSensitiveData(correctCredential);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}