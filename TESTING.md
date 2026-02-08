# Testing with Java 25

## Issue

Mockito has compatibility issues with Java 25. Tests that use `@Mock` or `@MockBean` fail with:
```
MockitoException: Mockito cannot mock this class
Could not modify all classes
```

## Solutions

### Option 1: Use Integration Tests (Recommended)

Instead of mocking dependencies, use real Spring beans and test the actual integration. This is more reliable and tests the real behavior.

**Example:**
```java
@SpringBootTest
class SqlGeneratorServiceIntegrationTest {
    
    @Autowired
    private SqlGeneratorService sqlGeneratorService;
    
    @Autowired
    private TableDefinitionService tableDefinitionService;
    
    @Test
    void testGenerateSql_Integration() {
        // Create a real table definition
        TableDefinition tableDef = TableDefinition.builder()
            .tableName("test_users")
            .columns(...)
            .build();
        
        tableDefinitionService.saveTableDefinition(tableDef);
        
        // Test with real dependencies
        SqlGenerationRequest request = SqlGenerationRequest.builder()
            .tableName("test_users")
            .jsonData("{\"user\":{\"name\":\"test\"}}")
            .build();
        
        SqlGenerationResponse response = sqlGeneratorService.generateSql(request);
        assertNotNull(response);
    }
}
```

### Option 2: Test Without Mocks

For utility classes and simple components, test without mocking:

```java
// No mocks needed - test the real implementation
class JsonPathExtractorTest {
    private JsonPathExtractor extractor = new JsonPathExtractor();
    
    @Test
    void testExtractValue() {
        String json = "{\"user\":{\"name\":\"test\"}}";
        Optional<Object> result = extractor.extractValue(json, "user.name");
        assertEquals("test", result.get());
    }
}
```

### Option 3: Use Java 17/21 for Testing

Run tests with Java 17 or 21:
```bash
# Install Java 17
brew install openjdk@17

# Set JAVA_HOME
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# Run tests
mvn test
```

### Option 4: Skip Mocking Tests

For now, you can skip tests that require mocking:
```bash
mvn test -Dtest='!*Mock*Test'
```

## Current Test Status

✅ **Working Tests (No Mocks):**
- `JsonPathExtractorTest` - 15 tests passing
- `SqlEscapeUtilTest` - 27 tests passing
- `GlobalExceptionHandlerTest` - 4 tests passing

❌ **Failing Tests (Require Mocks):**
- `SqlGeneratorServiceTest`
- `JsonMappingServiceTest`
- `InsertStatementGeneratorTest`
- `DdlGeneratorTest`
- `SqlScriptGeneratorTest`
- Controller tests with `@MockBean`

## Recommendation

1. **Keep working tests** - They validate core functionality
2. **Convert mocking tests to integration tests** - More reliable and tests real behavior
3. **Use real dependencies** - Spring Boot's dependency injection makes this easy
4. **Test at the API level** - Use `@WebMvcTest` or `@SpringBootTest` with `TestRestTemplate`

## Example: Converting a Mocking Test to Integration Test

**Before (with mocks):**
```java
@ExtendWith(MockitoExtension.class)
class SqlGeneratorServiceTest {
    @Mock private TableDefinitionService tableDefinitionService;
    @Mock private JsonMappingService jsonMappingService;
    @InjectMocks private SqlGeneratorService service;
    
    @Test
    void testGenerateSql() {
        when(tableDefinitionService.getTableDefinition("users"))
            .thenReturn(tableDefinition);
        // ...
    }
}
```

**After (integration test):**
```java
@SpringBootTest
class SqlGeneratorServiceIntegrationTest {
    @Autowired private SqlGeneratorService service;
    @Autowired private TableDefinitionService tableDefinitionService;
    
    @Test
    void testGenerateSql() {
        // Setup real data
        tableDefinitionService.saveTableDefinition(tableDefinition);
        
        // Test with real dependencies
        SqlGenerationResponse response = service.generateSql(request);
        assertNotNull(response);
    }
}
```
