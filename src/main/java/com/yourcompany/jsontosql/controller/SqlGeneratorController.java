package com.yourcompany.jsontosql.controller;

import com.yourcompany.jsontosql.model.SqlGenerationRequest;
import com.yourcompany.jsontosql.model.SqlGenerationResponse;
import com.yourcompany.jsontosql.service.SqlGeneratorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/sql")
@RequiredArgsConstructor
public class SqlGeneratorController {
    
    private final SqlGeneratorService sqlGeneratorService;
    
    @PostMapping("/generate")
    public ResponseEntity<SqlGenerationResponse> generateSql(@Valid @RequestBody SqlGenerationRequest request) {
        log.info("Generating SQL for table: {}", request.getTableName());
        SqlGenerationResponse response = sqlGeneratorService.generateSql(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateJson(
            @RequestParam String tableName,
            @RequestBody String jsonData) {
        log.info("Validating JSON against table: {}", tableName);
        Map<String, Object> result = sqlGeneratorService.validateJson(tableName, jsonData);
        return ResponseEntity.ok(result);
    }
}
