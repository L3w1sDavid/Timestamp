package com.Lewis.TimeStamp.controller;

import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TimestampController {

    private final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH)
            .withZone(ZoneId.of("GMT"));

    // Requirement: Empty date returns current time
    @GetMapping({""})
    public TimestampResponse getCurrentTime() {
        return createResponse(Instant.now());
    }

    // Requirement: Handle specific date strings or unix timestamps
    @GetMapping("/{dateString}")
    public Map<String, Object> getTimestamp(@PathVariable String dateString) {
        try {
            Instant instant;
            if (dateString.matches("\\d+")) {
                // If it's all numbers, treat as Unix Milli
                instant = Instant.ofEpochMilli(Long.parseLong(dateString));
            } else {
                // Otherwise, try to parse as date string
                instant = Instant.parse(dateString.contains("T") ? dateString : dateString + "T00:00:00Z");
            }
            // Spring converts the 'record' to JSON automatically
            TimestampResponse response = createResponse(instant);
            return Map.of("unix", response.unix(), "utc", response.utc());
        } catch (Exception e) {
            return Map.of("error", "Invalid Date");
        }
    }

    private TimestampResponse createResponse(Instant instant) {
        return new TimestampResponse(instant.toEpochMilli(), formatter.format(instant));
    }
    record TimestampResponse(Long unix, String utc) {}
}