package com.yourcompany.jsontosql.generator;

import com.yourcompany.jsontosql.model.ColumnDefinition;
import com.yourcompany.jsontosql.model.TableDefinition;
import com.yourcompany.jsontosql.util.SqlEscapeUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DdlGenerator {
    
    private final SqlEscapeUtil sqlEscapeUtil;
    
    public DdlGenerator(SqlEscapeUtil sqlEscapeUtil) {
        this.sqlEscapeUtil = sqlEscapeUtil;
    }
    
    /**
     * Generates a CREATE TABLE statement from table definition
     */
    public String generateCreateTable(TableDefinition tableDefinition) {
        StringBuilder sql = new StringBuilder();
        
        String tableName = tableDefinition.getTableName();
        String schema = tableDefinition.getSchema();
        
        // Build table name with optional schema
        String fullTableName = schema != null && !schema.isEmpty() 
            ? sqlEscapeUtil.escapeIdentifier(schema) + "." + sqlEscapeUtil.escapeIdentifier(tableName)
            : sqlEscapeUtil.escapeIdentifier(tableName);
        
        sql.append("CREATE TABLE ").append(fullTableName).append(" (\n");
        
        List<String> columnDefinitions = new ArrayList<>();
        List<String> primaryKeys = new ArrayList<>();
        
        for (ColumnDefinition column : tableDefinition.getColumns()) {
            StringBuilder columnDef = new StringBuilder("  ");
            columnDef.append(sqlEscapeUtil.escapeIdentifier(column.getName()));
            columnDef.append(" ").append(column.getType());
            
            // Add NOT NULL constraint
            if (!column.getNullable()) {
                columnDef.append(" NOT NULL");
            }
            
            // Add AUTO_INCREMENT if applicable
            if (column.getAutoIncrement()) {
                columnDef.append(" AUTO_INCREMENT");
            }
            
            // Track primary keys
            if (column.getPrimaryKey()) {
                primaryKeys.add(sqlEscapeUtil.escapeIdentifier(column.getName()));
            }
            
            columnDefinitions.add(columnDef.toString());
        }
        
        sql.append(String.join(",\n", columnDefinitions));
        
        // Add PRIMARY KEY constraint if any columns are marked as primary key
        if (!primaryKeys.isEmpty()) {
            sql.append(",\n  PRIMARY KEY (");
            sql.append(String.join(", ", primaryKeys));
            sql.append(")");
        }
        
        sql.append("\n);");
        
        return sql.toString();
    }
    
    /**
     * Generates DROP TABLE statement
     */
    public String generateDropTable(TableDefinition tableDefinition, boolean ifExists) {
        StringBuilder sql = new StringBuilder();
        
        String tableName = tableDefinition.getTableName();
        String schema = tableDefinition.getSchema();
        
        String fullTableName = schema != null && !schema.isEmpty() 
            ? sqlEscapeUtil.escapeIdentifier(schema) + "." + sqlEscapeUtil.escapeIdentifier(tableName)
            : sqlEscapeUtil.escapeIdentifier(tableName);
        
        sql.append("DROP TABLE ");
        if (ifExists) {
            sql.append("IF EXISTS ");
        }
        sql.append(fullTableName).append(";");
        
        return sql.toString();
    }
}
