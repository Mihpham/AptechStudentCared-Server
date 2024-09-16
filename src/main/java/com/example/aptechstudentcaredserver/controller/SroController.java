package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.SroRequest;
import com.example.aptechstudentcaredserver.bean.response.SroResponse;
import com.example.aptechstudentcaredserver.service.SroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/sros")
public class SroController {
    private final SroService sroService;

    @PostMapping("/add")
    public ResponseEntity<String> registerSro(@RequestBody SroRequest sroRequest) {
        try {
            sroService.registerSro(sroRequest);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .body("{\"message\": \"SRO added successfully\"}");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping
    public ResponseEntity<List<SroResponse>> getAllSros() {
        try {
            List<SroResponse> sros = sroService.findAllSro();
            return new ResponseEntity<>(sros, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{sroId}")
    public ResponseEntity<Object> getSroById(@PathVariable("sroId") int sroId) {
        try {
            SroResponse sroResponse = sroService.findSroById(sroId);
            return ResponseEntity.ok(sroResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"SRO not found with id " + sroId + "\"}");
        }
    }

    @PutMapping("/{sroId}")
    public ResponseEntity<Object> updateSro(
            @PathVariable int sroId,
            @RequestBody SroRequest sroRequest) {
        try {
            SroResponse updatedSro = sroService.updateSro(sroId, sroRequest);
            return ResponseEntity.ok(updatedSro);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"Failed to update SRO: " + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping("/{sroId}")
    public ResponseEntity<String> deleteSro(@PathVariable("sroId") int sroId) {
        try {
            sroService.deleteSro(sroId);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .body("{\"message\": \"SRO deleted successfully\"}");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"SRO not found with id " + sroId + "\"}");
        }
    }
}
