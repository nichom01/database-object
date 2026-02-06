package com.yourcompany.jsontosql.model;

import com.fasterxml.jackson.annotation.JsonRawValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SqlGenerationRequest {
    
    @NotBlank(message = "Table name is required")
    private String tableName;
    
    @NotNull(message = "JSON data is required")
    @JsonRawValue
    private String jsonData; // Raw JSON string
    
    private Boolean includeDdl = false; // Whether to include CREATE TABLE statement
    
    private Boolean batchMode = false; // If true, expects array of JSON objects
    
    private String dialect = "STANDARD"; // SQL dialect: STANDARD, MYSQL, POSTGRESQL, ORACLE
}
