package com.yourcompany.jsontosql.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class ColumnDefinition {
    
    @NotBlank(message = "Column name is required")
    private String name;
    
    @NotBlank(message = "Column type is required")
    private String type;
    
    @NotNull(message = "Nullable flag is required")
    @Builder.Default
    private Boolean nullable = true;
    
    @Builder.Default
    private Boolean primaryKey = false;
    
    @Builder.Default
    private Boolean autoIncrement = false;
    
    @JsonProperty("jsonPath")
    private String jsonPath; // JSONPath expression to extract value from input JSON
    
    private String defaultValue; // Default value if jsonPath doesn't resolve
    
    private Integer maxLength; // For VARCHAR types
    
    private Integer precision; // For DECIMAL types
    
    private Integer scale; // For DECIMAL types
}
