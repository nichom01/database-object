package com.yourcompany.jsontosql.controller;

import com.yourcompany.jsontosql.model.TableDefinition;
import com.yourcompany.jsontosql.service.TableDefinitionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tables")
public class TableDefinitionController {
    
    private static final Logger log = LoggerFactory.getLogger(TableDefinitionController.class);
    
    private final TableDefinitionService tableDefinitionService;
    
    public TableDefinitionController(TableDefinitionService tableDefinitionService) {
        this.tableDefinitionService = tableDefinitionService;
    }
    
    @GetMapping
    public ResponseEntity<List<TableDefinition>> getAllTableDefinitions() {
        log.info("Retrieving all table definitions");
        List<TableDefinition> definitions = tableDefinitionService.getAllTableDefinitions();
        return ResponseEntity.ok(definitions);
    }
    
    @GetMapping("/{name}")
    public ResponseEntity<TableDefinition> getTableDefinition(@PathVariable String name) {
        log.info("Retrieving table definition: {}", name);
        TableDefinition definition = tableDefinitionService.getTableDefinition(name);
        return ResponseEntity.ok(definition);
    }
    
    @PostMapping
    public ResponseEntity<TableDefinition> createTableDefinition(@Valid @RequestBody TableDefinition tableDefinition) {
        log.info("Creating table definition: {}", tableDefinition.getTableName());
        TableDefinition created = tableDefinitionService.saveTableDefinition(tableDefinition);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{name}")
    public ResponseEntity<TableDefinition> updateTableDefinition(
            @PathVariable String name,
            @Valid @RequestBody TableDefinition tableDefinition) {
        log.info("Updating table definition: {}", name);
        
        // Ensure the table name matches the path variable
        if (!name.equalsIgnoreCase(tableDefinition.getTableName())) {
            tableDefinition.setTableName(name);
        }
        
        TableDefinition updated = tableDefinitionService.saveTableDefinition(tableDefinition);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteTableDefinition(@PathVariable String name) {
        log.info("Deleting table definition: {}", name);
        tableDefinitionService.deleteTableDefinition(name);
        return ResponseEntity.noContent().build();
    }
}
