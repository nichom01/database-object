package com.yourcompany.jsontosql.model;

import java.util.List;

public class SqlGenerationResponse {
    
    private String sqlScript;
    
    private List<String> statements; // Individual SQL statements
    
    private String tableName;
    
    private Integer statementCount;
    
    private List<String> warnings; // Any warnings during generation
    
    private List<String> errors; // Any errors during generation
    
    // Constructors
    public SqlGenerationResponse() {
    }
    
    public SqlGenerationResponse(String sqlScript, List<String> statements, String tableName, 
                                 Integer statementCount, List<String> warnings, List<String> errors) {
        this.sqlScript = sqlScript;
        this.statements = statements;
        this.tableName = tableName;
        this.statementCount = statementCount;
        this.warnings = warnings;
        this.errors = errors;
    }
    
    // Getters and Setters
    public String getSqlScript() {
        return sqlScript;
    }
    
    public void setSqlScript(String sqlScript) {
        this.sqlScript = sqlScript;
    }
    
    public List<String> getStatements() {
        return statements;
    }
    
    public void setStatements(List<String> statements) {
        this.statements = statements;
    }
    
    public String getTableName() {
        return tableName;
    }
    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public Integer getStatementCount() {
        return statementCount;
    }
    
    public void setStatementCount(Integer statementCount) {
        this.statementCount = statementCount;
    }
    
    public List<String> getWarnings() {
        return warnings;
    }
    
    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }
    
    public List<String> getErrors() {
        return errors;
    }
    
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String sqlScript;
        private List<String> statements;
        private String tableName;
        private Integer statementCount;
        private List<String> warnings;
        private List<String> errors;
        
        public Builder sqlScript(String sqlScript) {
            this.sqlScript = sqlScript;
            return this;
        }
        
        public Builder statements(List<String> statements) {
            this.statements = statements;
            return this;
        }
        
        public Builder tableName(String tableName) {
            this.tableName = tableName;
            return this;
        }
        
        public Builder statementCount(Integer statementCount) {
            this.statementCount = statementCount;
            return this;
        }
        
        public Builder warnings(List<String> warnings) {
            this.warnings = warnings;
            return this;
        }
        
        public Builder errors(List<String> errors) {
            this.errors = errors;
            return this;
        }
        
        public SqlGenerationResponse build() {
            return new SqlGenerationResponse(sqlScript, statements, tableName, statementCount, warnings, errors);
        }
    }
}
