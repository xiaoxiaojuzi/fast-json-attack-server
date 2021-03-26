package org.example.poorApi.web;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/sensitive")
@RestController
public class SensitiveDataController {
    @PostMapping("")
    public ResponseEntity<String> getSensitiveData(@RequestBody Object payload) {
        final JSONObject json = (JSONObject) payload;
        if (!validateToken(json) && !validateCredential(json)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok("Some sensitive data");
    }

    private boolean validateToken(JSONObject token) {
        final String value = token.getString("value");
        if (value == null) { return false; }
        return value.equals("safe-token-value");
    }

    private boolean validateCredential(JSONObject credential) {
        final String username = credential.getString("username");
        final String password = credential.getString("password");
        if (username == null || password == null) { return false; }
        return username.equals("admin") && password.equals("p@ssword");
    }
}
