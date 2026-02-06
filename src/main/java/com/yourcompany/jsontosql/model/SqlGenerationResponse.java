package com.yourcompany.jsontosql.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SqlGenerationResponse {
    
    private String sqlScript;
    
    private List<String> statements; // Individual SQL statements
    
    private String tableName;
    
    private Integer statementCount;
    
    private List<String> warnings; // Any warnings during generation
    
    private List<String> errors; // Any errors during generation
}
