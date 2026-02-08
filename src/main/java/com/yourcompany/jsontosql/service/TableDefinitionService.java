package com.yourcompany.jsontosql.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourcompany.jsontosql.exception.TableDefinitionNotFoundException;
import com.yourcompany.jsontosql.model.TableDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TableDefinitionService {
    
    private static final Logger log = LoggerFactory.getLogger(TableDefinitionService.class);
    
    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;
    private final Map<String, TableDefinition> tableDefinitionCache = new ConcurrentHashMap<>();
    
    @Value("${app.table-definitions.storage-path:${user.home}/.json-to-sql/table-definitions}")
    private String storagePath;
    
    @Value("${app.table-definitions.default-path:classpath:table-definitions}")
    private String defaultPath;
    
    public TableDefinitionService(ObjectMapper objectMapper, ResourceLoader resourceLoader) {
        this.objectMapper = objectMapper;
        this.resourceLoader = resourceLoader;
        loadDefaultDefinitions();
    }
    
    /**
     * Loads default table definitions from classpath
     */
    private void loadDefaultDefinitions() {
        try {
            Resource resource = resourceLoader.getResource(defaultPath);
            if (resource.exists()) {
                if (resource.getFile().exists() && resource.getFile().isDirectory()) {
                    // File system resource
                    File[] files = resource.getFile().listFiles((dir, name) -> name.endsWith(".json"));
                    if (files != null) {
                        for (File file : files) {
                            loadTableDefinitionFromFile(file);
                        }
                    }
                } else {
                    // JAR resource - use classpath scanning
                    try {
                        org.springframework.core.io.support.PathMatchingResourcePatternResolver resolver =
                                new org.springframework.core.io.support.PathMatchingResourcePatternResolver(resourceLoader);
                        org.springframework.core.io.Resource[] resources = 
                                resolver.getResources(defaultPath + "/*.json");
                        for (org.springframework.core.io.Resource res : resources) {
                            try {
                                TableDefinition definition = objectMapper.readValue(
                                        res.getInputStream(), TableDefinition.class);
                                tableDefinitionCache.put(definition.getTableName().toLowerCase(), definition);
                                log.info("Loaded default table definition: {}", definition.getTableName());
                            } catch (Exception e) {
                                log.warn("Failed to load table definition from {}: {}", 
                                        res.getFilename(), e.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        log.debug("No default table definitions found in JAR: {}", e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.debug("No default table definitions found or error loading: {}", e.getMessage());
        }
    }
    
    private void loadTableDefinitionFromFile(File file) {
        try {
            TableDefinition definition = objectMapper.readValue(file, TableDefinition.class);
            tableDefinitionCache.put(definition.getTableName().toLowerCase(), definition);
            log.info("Loaded default table definition: {}", definition.getTableName());
        } catch (Exception e) {
            log.warn("Failed to load table definition from {}: {}", file.getName(), e.getMessage());
        }
    }
    
    /**
     * Gets a table definition by name
     */
    public TableDefinition getTableDefinition(String tableName) {
        String key = tableName.toLowerCase();
        TableDefinition definition = tableDefinitionCache.get(key);
        
        if (definition == null) {
            // Try to load from file system
            definition = loadFromFileSystem(tableName);
            if (definition != null) {
                tableDefinitionCache.put(key, definition);
            }
        }
        
        if (definition == null) {
            throw new TableDefinitionNotFoundException(tableName);
        }
        
        return definition;
    }
    
    /**
     * Gets all table definitions
     */
    public List<TableDefinition> getAllTableDefinitions() {
        // Ensure file system definitions are loaded
        loadFromFileSystem();
        return new ArrayList<>(tableDefinitionCache.values());
    }
    
    /**
     * Creates or updates a table definition
     */
    public TableDefinition saveTableDefinition(TableDefinition tableDefinition) {
        validateTableDefinition(tableDefinition);
        
        String key = tableDefinition.getTableName().toLowerCase();
        tableDefinitionCache.put(key, tableDefinition);
        
        // Optionally save to file system
        saveToFileSystem(tableDefinition);
        
        log.info("Saved table definition: {}", tableDefinition.getTableName());
        return tableDefinition;
    }
    
    /**
     * Deletes a table definition
     */
    public void deleteTableDefinition(String tableName) {
        String key = tableName.toLowerCase();
        TableDefinition removed = tableDefinitionCache.remove(key);
        
        if (removed == null) {
            throw new TableDefinitionNotFoundException(tableName);
        }
        
        // Optionally delete from file system
        deleteFromFileSystem(tableName);
        
        log.info("Deleted table definition: {}", tableName);
    }
    
    /**
     * Validates a table definition
     */
    private void validateTableDefinition(TableDefinition tableDefinition) {
        if (tableDefinition == null) {
            throw new IllegalArgumentException("Table definition cannot be null");
        }
        if (tableDefinition.getTableName() == null || tableDefinition.getTableName().trim().isEmpty()) {
            throw new IllegalArgumentException("Table name is required");
        }
        if (tableDefinition.getColumns() == null || tableDefinition.getColumns().isEmpty()) {
            throw new IllegalArgumentException("At least one column is required");
        }
    }
    
    /**
     * Loads table definition from file system
     */
    private TableDefinition loadFromFileSystem(String tableName) {
        try {
            Path filePath = Paths.get(storagePath, tableName + ".json");
            if (Files.exists(filePath)) {
                return objectMapper.readValue(filePath.toFile(), TableDefinition.class);
            }
        } catch (Exception e) {
            log.debug("Failed to load table definition from file system: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * Loads all table definitions from file system
     */
    private void loadFromFileSystem() {
        try {
            Path storageDir = Paths.get(storagePath);
            if (Files.exists(storageDir) && Files.isDirectory(storageDir)) {
                Files.list(storageDir)
                        .filter(path -> path.toString().endsWith(".json"))
                        .forEach(path -> {
                            try {
                                TableDefinition definition = objectMapper.readValue(path.toFile(), TableDefinition.class);
                                tableDefinitionCache.put(definition.getTableName().toLowerCase(), definition);
                            } catch (Exception e) {
                                log.warn("Failed to load table definition from {}: {}", path, e.getMessage());
                            }
                        });
            }
        } catch (Exception e) {
            log.debug("Error loading table definitions from file system: {}", e.getMessage());
        }
    }
    
    /**
     * Saves table definition to file system
     */
    private void saveToFileSystem(TableDefinition tableDefinition) {
        try {
            Path storageDir = Paths.get(storagePath);
            Files.createDirectories(storageDir);
            
            Path filePath = storageDir.resolve(tableDefinition.getTableName() + ".json");
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), tableDefinition);
        } catch (Exception e) {
            log.warn("Failed to save table definition to file system: {}", e.getMessage());
        }
    }
    
    /**
     * Deletes table definition from file system
     */
    private void deleteFromFileSystem(String tableName) {
        try {
            Path filePath = Paths.get(storagePath, tableName + ".json");
            Files.deleteIfExists(filePath);
        } catch (Exception e) {
            log.warn("Failed to delete table definition from file system: {}", e.getMessage());
        }
    }
}
