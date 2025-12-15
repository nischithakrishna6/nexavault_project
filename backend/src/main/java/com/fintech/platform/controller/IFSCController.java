package com.fintech.platform.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ifsc")
@RequiredArgsConstructor
public class IFSCController {

    @GetMapping("/{ifscCode}")
    public ResponseEntity<?> verifyIFSC(@PathVariable String ifscCode) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://ifsc.razorpay.com/" + ifscCode;

            // Make request from backend (no CORS issue)
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            return ResponseEntity.ok(response);

        } catch (HttpClientErrorException.NotFound e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid IFSC code");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to verify IFSC code");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}