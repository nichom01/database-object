package com.yourcompany.jsontosql.service;

import com.yourcompany.jsontosql.generator.SqlScriptGenerator;
import com.yourcompany.jsontosql.model.SqlGenerationRequest;
import com.yourcompany.jsontosql.model.SqlGenerationResponse;
import com.yourcompany.jsontosql.model.TableDefinition;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SqlGeneratorService {
    
    private final TableDefinitionService tableDefinitionService;
    private final JsonMappingService jsonMappingService;
    private final SqlScriptGenerator sqlScriptGenerator;
    
    public SqlGeneratorService(TableDefinitionService tableDefinitionService, 
                              JsonMappingService jsonMappingService,
                              SqlScriptGenerator sqlScriptGenerator) {
        this.tableDefinitionService = tableDefinitionService;
        this.jsonMappingService = jsonMappingService;
        this.sqlScriptGenerator = sqlScriptGenerator;
    }
    
    /**
     * Generates SQL script from request
     */
    public SqlGenerationResponse generateSql(SqlGenerationRequest request) {
        // Get table definition
        TableDefinition tableDefinition = tableDefinitionService.getTableDefinition(request.getTableName());
        
        // Validate JSON against schema (optional, but good practice)
        jsonMappingService.validateJsonAgainstSchema(tableDefinition, request.getJsonData());
        
        // Generate SQL script
        return sqlScriptGenerator.generateScript(request, tableDefinition);
    }
    
    /**
     * Validates JSON data against table definition
     */
    public Map<String, Object> validateJson(String tableName, String jsonData) {
        TableDefinition tableDefinition = tableDefinitionService.getTableDefinition(tableName);
        return jsonMappingService.validateJsonAgainstSchema(tableDefinition, jsonData);
    }
}
