package com.yourcompany.jsontosql.generator;

import com.yourcompany.jsontosql.model.SqlGenerationRequest;
import com.yourcompany.jsontosql.model.SqlGenerationResponse;
import com.yourcompany.jsontosql.model.TableDefinition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SqlScriptGenerator {
    
    private final InsertStatementGenerator insertStatementGenerator;
    private final DdlGenerator ddlGenerator;
    
    public SqlScriptGenerator(InsertStatementGenerator insertStatementGenerator, DdlGenerator ddlGenerator) {
        this.insertStatementGenerator = insertStatementGenerator;
        this.ddlGenerator = ddlGenerator;
    }
    
    /**
     * Generates a complete SQL script from request and table definition
     */
    public SqlGenerationResponse generateScript(SqlGenerationRequest request, TableDefinition tableDefinition) {
        List<String> statements = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        // Generate DDL if requested
        if (request.getIncludeDdl() != null && request.getIncludeDdl()) {
            statements.add(ddlGenerator.generateCreateTable(tableDefinition));
        }
        
        // Generate INSERT statements
        if (request.getBatchMode() != null && request.getBatchMode()) {
            List<String> inserts = insertStatementGenerator.generateBatchInserts(tableDefinition, request.getJsonData());
            statements.addAll(inserts);
        } else {
            statements.add(insertStatementGenerator.generateInsert(tableDefinition, request.getJsonData()));
        }
        
        // Combine into single script
        String sqlScript = String.join("\n\n", statements);
        
        return SqlGenerationResponse.builder()
                .sqlScript(sqlScript)
                .statements(statements)
                .tableName(tableDefinition.getTableName())
                .statementCount(statements.size())
                .warnings(warnings)
                .errors(new ArrayList<>())
                .build();
    }
}
