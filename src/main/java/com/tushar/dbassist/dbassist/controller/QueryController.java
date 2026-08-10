package com.tushar.dbassist.dbassist.controller;

import com.tushar.dbassist.dbassist.model.QueryRequest;
import com.tushar.dbassist.dbassist.model.QueryResponse;
import com.tushar.dbassist.dbassist.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class QueryController {

    @Autowired
    private GeminiService geminiService;

    @GetMapping("/health")
    public String healthCheck() {
        return "API is running!";
    }
    @PostMapping("/query")
    public ResponseEntity<QueryResponse> query(@RequestBody QueryRequest request) {
        QueryResponse response = geminiService.processQuery(request);
        return ResponseEntity.ok(response);
    }
}