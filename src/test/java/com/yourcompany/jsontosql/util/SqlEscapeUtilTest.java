package com.yourcompany.jsontosql.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class SqlEscapeUtilTest {
    
    private SqlEscapeUtil sqlEscapeUtil;
    
    @BeforeEach
    void setUp() {
        sqlEscapeUtil = new SqlEscapeUtil();
    }
    
    @Test
    void testEscapeString_NullValue() {
        String result = sqlEscapeUtil.escapeString(null);
        assertEquals("NULL", result);
    }
    
    @Test
    void testEscapeString_SimpleString() {
        String result = sqlEscapeUtil.escapeString("test");
        assertEquals("'test'", result);
    }
    
    @Test
    void testEscapeString_StringWithSingleQuote() {
        String result = sqlEscapeUtil.escapeString("O'Reilly");
        assertEquals("'O''Reilly'", result);
    }
    
    @Test
    void testEscapeString_StringWithMultipleQuotes() {
        String result = sqlEscapeUtil.escapeString("It's a 'test'");
        assertEquals("'It''s a ''test'''", result);
    }
    
    @Test
    void testEscapeString_Integer() {
        String result = sqlEscapeUtil.escapeString(123);
        assertEquals("123", result);
    }
    
    @Test
    void testEscapeString_Long() {
        String result = sqlEscapeUtil.escapeString(123L);
        assertEquals("123", result);
    }
    
    @Test
    void testEscapeString_Double() {
        String result = sqlEscapeUtil.escapeString(123.45);
        assertEquals("123.45", result);
    }
    
    @Test
    void testEscapeString_BooleanTrue() {
        String result = sqlEscapeUtil.escapeString(true);
        assertEquals("1", result);
    }
    
    @Test
    void testEscapeString_BooleanFalse() {
        String result = sqlEscapeUtil.escapeString(false);
        assertEquals("0", result);
    }
    
    @Test
    void testEscapeString_LocalDate() {
        LocalDate date = LocalDate.of(2024, 1, 15);
        String result = sqlEscapeUtil.escapeString(date);
        assertEquals("'2024-01-15'", result);
    }
    
    @Test
    void testEscapeString_LocalTime() {
        LocalTime time = LocalTime.of(14, 30, 0);
        String result = sqlEscapeUtil.escapeString(time);
        assertTrue(result.startsWith("'14:30"));
    }
    
    @Test
    void testEscapeString_LocalDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 14, 30, 0);
        String result = sqlEscapeUtil.escapeString(dateTime);
        assertTrue(result.startsWith("'2024-01-15T14:30"));
    }
    
    @Test
    void testEscapeIdentifier_SimpleName() {
        String result = sqlEscapeUtil.escapeIdentifier("users");
        assertEquals("\"users\"", result);
    }
    
    @Test
    void testEscapeIdentifier_WithExistingQuotes() {
        String result = sqlEscapeUtil.escapeIdentifier("\"users\"");
        assertEquals("\"users\"", result);
    }
    
    @Test
    void testEscapeIdentifier_WithBackticks() {
        String result = sqlEscapeUtil.escapeIdentifier("`users`");
        assertEquals("\"users\"", result);
    }
    
    @Test
    void testEscapeIdentifier_WithBrackets() {
        String result = sqlEscapeUtil.escapeIdentifier("[users]");
        assertEquals("\"users\"", result);
    }
    
    @Test
    void testEscapeIdentifier_NullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            sqlEscapeUtil.escapeIdentifier(null);
        });
    }
    
    @Test
    void testEscapeIdentifier_EmptyThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            sqlEscapeUtil.escapeIdentifier("");
        });
    }
    
    @Test
    void testFormatValueForType_Varchar() {
        String result = sqlEscapeUtil.formatValueForType("test", "VARCHAR(255)");
        assertEquals("'test'", result);
    }
    
    @Test
    void testFormatValueForType_Integer() {
        String result = sqlEscapeUtil.formatValueForType(123, "INT");
        assertEquals("123", result);
    }
    
    @Test
    void testFormatValueForType_BigInt() {
        String result = sqlEscapeUtil.formatValueForType(123L, "BIGINT");
        assertEquals("123", result);
    }
    
    @Test
    void testFormatValueForType_Decimal() {
        String result = sqlEscapeUtil.formatValueForType(123.45, "DECIMAL(10,2)");
        assertEquals("123.45", result);
    }
    
    @Test
    void testFormatValueForType_Boolean() {
        String result = sqlEscapeUtil.formatValueForType(true, "BOOLEAN");
        assertEquals("1", result);
    }
    
    @Test
    void testFormatValueForType_Bit() {
        String result = sqlEscapeUtil.formatValueForType(false, "BIT");
        assertEquals("0", result);
    }
    
    @Test
    void testFormatValueForType_Null() {
        String result = sqlEscapeUtil.formatValueForType(null, "VARCHAR(255)");
        assertEquals("NULL", result);
    }
    
    @Test
    void testFormatValueForType_StringAsNumber() {
        String result = sqlEscapeUtil.formatValueForType("123", "INT");
        assertEquals("123", result);
    }
    
    @Test
    void testFormatValueForType_InvalidNumber() {
        String result = sqlEscapeUtil.formatValueForType("not-a-number", "INT");
        assertEquals("'not-a-number'", result);
    }
}
