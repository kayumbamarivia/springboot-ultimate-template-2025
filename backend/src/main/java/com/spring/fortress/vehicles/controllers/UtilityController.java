package com.spring.fortress.vehicles.controllers;

import com.spring.fortress.vehicles.utils.MyGenerator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/utility")
public class UtilityController {

    private final MyGenerator myGenerator;

    public UtilityController(MyGenerator myGenerator) {
        this.myGenerator = myGenerator;
    }

    /**
     * Generate a single random national ID
     * GET /utility/national-id
     */
    @GetMapping("/national-id")
    public ResponseEntity<Map<String, String>> generateNationalId() {
        String nationalId = myGenerator.generateNationalId();
        Map<String, String> response = new HashMap<>();
        response.put("nationalId", nationalId);
        return ResponseEntity.ok(response);
    }

    /**
     * Generate a single random car plate
     * GET /utility/car-plate
     */
    @GetMapping("/car-plate")
    public ResponseEntity<Map<String, String>> generateCarPlate() {
        String carPlate = myGenerator.generateCarPlate();
        Map<String, String> response = new HashMap<>();
        response.put("carPlate", carPlate);
        return ResponseEntity.ok(response);
    }

    /**
     * Generate a single random 6-digit meter number
     * GET /utility/meter-number
     */
    @GetMapping("/meter-number")
    public ResponseEntity<Map<String, String>> generateMeterNumber() {
        String meterNumber = myGenerator.generateMeterNumber();
        Map<String, String> response = new HashMap<>();
        response.put("meterNumber", meterNumber);
        return ResponseEntity.ok(response);
    }

    /**
     * Generate a single random 17-character chassis number
     * GET /utility/chassis-number
     */
    @GetMapping("/chassis-number")
    public ResponseEntity<Map<String, String>> generateChassisNumber() {
        String chassisNumber = myGenerator.generateChassisNumber();
        Map<String, String> response = new HashMap<>();
        response.put("chassisNumber", chassisNumber);
        return ResponseEntity.ok(response);
    }
}