package com.example.aptechstudentcaredserver.controller;

import com.example.aptechstudentcaredserver.bean.request.SroRequest;
import com.example.aptechstudentcaredserver.bean.response.SroResponse;
import com.example.aptechstudentcaredserver.service.AuthService;
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
    private final AuthService authService;

    @PostMapping("/add")
    public ResponseEntity<String> registerSro(@RequestBody SroRequest sroRequest) {
        try {
            sroService.registerSro(sroRequest);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .body("{\"message\": \"Sro added successfully\"}");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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
    public ResponseEntity<SroResponse> getSroById(@PathVariable("sroId") int sroId) {
        SroResponse sroResponse = sroService.findSroById(sroId);
        return ResponseEntity.ok(sroResponse);
    }

    @PutMapping("/{sroId}")
    public ResponseEntity<SroResponse> updateSro(
            @PathVariable int sroId,
            @RequestBody SroRequest sroRequest) {
        SroResponse updatedSro = sroService.updateSro(sroId, sroRequest);
        return ResponseEntity.ok(updatedSro);
    }

    @DeleteMapping("/{sroId}")
    public ResponseEntity<String> deleteSro(@PathVariable("sroId") int sroId) {
        sroService.deleteSro(sroId);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body("{\"message\": \"Sro deleted successfully\"}");
    }
}
