package com.yourcompany.jsontosql.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableDefinition {
    
    @NotBlank(message = "Table name is required")
    @JsonProperty("tableName")
    private String tableName;
    
    @NotEmpty(message = "At least one column definition is required")
    @Valid
    private List<ColumnDefinition> columns;
    
    private String schema; // Optional schema name (e.g., "public" for PostgreSQL)
    
    private String description; // Optional description of the table
}
