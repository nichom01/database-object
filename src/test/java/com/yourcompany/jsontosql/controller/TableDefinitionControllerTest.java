package com.yourcompany.jsontosql.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourcompany.jsontosql.exception.TableDefinitionNotFoundException;
import com.yourcompany.jsontosql.model.ColumnDefinition;
import com.yourcompany.jsontosql.model.TableDefinition;
import com.yourcompany.jsontosql.service.TableDefinitionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TableDefinitionController.class)
class TableDefinitionControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private TableDefinitionService tableDefinitionService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private TableDefinition tableDefinition;
    
    @BeforeEach
    void setUp() {
        tableDefinition = TableDefinition.builder()
                .tableName("users")
                .columns(Arrays.asList(
                        ColumnDefinition.builder()
                                .name("id")
                                .type("BIGINT")
                                .nullable(false)
                                .primaryKey(true)
                                .autoIncrement(true)
                                .build(),
                        ColumnDefinition.builder()
                                .name("username")
                                .type("VARCHAR(255)")
                                .nullable(false)
                                .build()
                ))
                .build();
    }
    
    @Test
    void testGetAllTableDefinitions() throws Exception {
        List<TableDefinition> definitions = Collections.singletonList(tableDefinition);
        when(tableDefinitionService.getAllTableDefinitions()).thenReturn(definitions);
        
        mockMvc.perform(get("/api/v1/tables"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tableName").value("users"))
                .andExpect(jsonPath("$[0].columns").isArray());
        
        verify(tableDefinitionService).getAllTableDefinitions();
    }
    
    @Test
    void testGetTableDefinition_Success() throws Exception {
        when(tableDefinitionService.getTableDefinition("users")).thenReturn(tableDefinition);
        
        mockMvc.perform(get("/api/v1/tables/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tableName").value("users"))
                .andExpect(jsonPath("$.columns").isArray());
        
        verify(tableDefinitionService).getTableDefinition("users");
    }
    
    @Test
    void testGetTableDefinition_NotFound() throws Exception {
        when(tableDefinitionService.getTableDefinition("nonexistent"))
                .thenThrow(new TableDefinitionNotFoundException("nonexistent"));
        
        mockMvc.perform(get("/api/v1/tables/nonexistent"))
                .andExpect(status().isNotFound());
        
        verify(tableDefinitionService).getTableDefinition("nonexistent");
    }
    
    @Test
    void testCreateTableDefinition() throws Exception {
        when(tableDefinitionService.saveTableDefinition(any(TableDefinition.class)))
                .thenReturn(tableDefinition);
        
        mockMvc.perform(post("/api/v1/tables")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tableDefinition)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tableName").value("users"));
        
        verify(tableDefinitionService).saveTableDefinition(any(TableDefinition.class));
    }
    
    @Test
    void testUpdateTableDefinition() throws Exception {
        when(tableDefinitionService.saveTableDefinition(any(TableDefinition.class)))
                .thenReturn(tableDefinition);
        
        mockMvc.perform(put("/api/v1/tables/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tableDefinition)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tableName").value("users"));
        
        verify(tableDefinitionService).saveTableDefinition(any(TableDefinition.class));
    }
    
    @Test
    void testDeleteTableDefinition() throws Exception {
        doNothing().when(tableDefinitionService).deleteTableDefinition("users");
        
        mockMvc.perform(delete("/api/v1/tables/users"))
                .andExpect(status().isNoContent());
        
        verify(tableDefinitionService).deleteTableDefinition("users");
    }
    
    @Test
    void testDeleteTableDefinition_NotFound() throws Exception {
        doThrow(new TableDefinitionNotFoundException("nonexistent"))
                .when(tableDefinitionService).deleteTableDefinition("nonexistent");
        
        mockMvc.perform(delete("/api/v1/tables/nonexistent"))
                .andExpect(status().isNotFound());
        
        verify(tableDefinitionService).deleteTableDefinition("nonexistent");
    }
}
