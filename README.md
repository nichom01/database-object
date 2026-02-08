# JSON-to-SQL Script Generator Service

A Spring Boot application that generates SQL scripts from JSON objects using configurable table definitions.

## Features

- **Table Definition Management**: Create, read, update, and delete table definitions via REST API
- **SQL Generation**: Generate INSERT statements from JSON data
- **DDL Generation**: Generate CREATE TABLE statements from table definitions
- **JSONPath Support**: Extract values from nested JSON using JSONPath expressions
- **Batch Processing**: Support for generating multiple INSERT statements from JSON arrays
- **Validation**: Validate JSON data against table definitions before generating SQL

## Technology Stack

- Spring Boot 3.2.0
- Java 17+ (tested with Java 25)
- Jackson (JSON processing)
- JSONPath (for JSON path extraction)
- Spring Validation
- JUnit 5 & MockMvc (testing)
- Mockito 5.21.0 (with experimental ByteBuddy support for Java 25)

## Project Structure

```
src/main/java/com/yourcompany/jsontosql/
├── config/
│   ├── ApplicationConfig.java
│   └── WebConfig.java
├── controller/
│   ├── SqlGeneratorController.java
│   └── TableDefinitionController.java
├── service/
│   ├── SqlGeneratorService.java
│   ├── TableDefinitionService.java
│   └── JsonMappingService.java
├── model/
│   ├── TableDefinition.java
│   ├── ColumnDefinition.java
│   ├── SqlGenerationRequest.java
│   └── SqlGenerationResponse.java
├── generator/
│   ├── SqlScriptGenerator.java
│   ├── InsertStatementGenerator.java
│   └── DdlGenerator.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── TableDefinitionNotFoundException.java
│   └── JsonMappingException.java
└── util/
    ├── JsonPathExtractor.java
    └── SqlEscapeUtil.java
```

## API Endpoints

### Table Definition Management

- `GET /api/v1/tables` - List all table definitions
- `GET /api/v1/tables/{name}` - Get specific table definition
- `POST /api/v1/tables` - Create table definition
- `PUT /api/v1/tables/{name}` - Update table definition
- `DELETE /api/v1/tables/{name}` - Delete table definition

### SQL Generation

- `POST /api/v1/sql/generate` - Generate SQL from JSON
- `POST /api/v1/sql/validate` - Validate JSON against schema

## Usage Examples

### 1. Create a Table Definition

```bash
curl -X POST http://localhost:8080/api/v1/tables \
  -H "Content-Type: application/json" \
  -d '{
    "tableName": "users",
    "columns": [
      {
        "name": "id",
        "type": "BIGINT",
        "nullable": false,
        "primaryKey": true,
        "autoIncrement": true
      },
      {
        "name": "username",
        "type": "VARCHAR(255)",
        "nullable": false,
        "jsonPath": "user.name"
      },
      {
        "name": "email",
        "type": "VARCHAR(255)",
        "nullable": true,
        "jsonPath": "user.email"
      }
    ]
  }'
```

### 2. Generate SQL from JSON

```bash
curl -X POST http://localhost:8080/api/v1/sql/generate \
  -H "Content-Type: application/json" \
  -d '{
    "tableName": "users",
    "jsonData": "{\"user\":{\"name\":\"john_doe\",\"email\":\"john@example.com\"}}",
    "includeDdl": false,
    "batchMode": false
  }'
```

Response:
```json
{
  "sqlScript": "INSERT INTO \"users\" (\"username\", \"email\") VALUES ('john_doe', 'john@example.com');",
  "statements": [
    "INSERT INTO \"users\" (\"username\", \"email\") VALUES ('john_doe', 'john@example.com');"
  ],
  "tableName": "users",
  "statementCount": 1,
  "warnings": [],
  "errors": []
}
```

### 3. Generate SQL with DDL

```bash
curl -X POST http://localhost:8080/api/v1/sql/generate \
  -H "Content-Type: application/json" \
  -d '{
    "tableName": "users",
    "jsonData": "{\"user\":{\"name\":\"john_doe\",\"email\":\"john@example.com\"}}",
    "includeDdl": true
  }'
```

## Table Definition Schema

```json
{
  "tableName": "users",
  "schema": "public",
  "description": "User table definition",
  "columns": [
    {
      "name": "id",
      "type": "BIGINT",
      "nullable": false,
      "primaryKey": true,
      "autoIncrement": true
    },
    {
      "name": "username",
      "type": "VARCHAR(255)",
      "nullable": false,
      "jsonPath": "user.name"
    },
    {
      "name": "email",
      "type": "VARCHAR(255)",
      "nullable": true,
      "jsonPath": "user.email",
      "defaultValue": "unknown@example.com"
    }
  ]
}
```

## Column Definition Fields

- `name` (required): Column name
- `type` (required): SQL data type (e.g., VARCHAR(255), BIGINT, TIMESTAMP)
- `nullable` (required): Whether the column can be NULL
- `primaryKey` (optional): Whether this column is a primary key
- `autoIncrement` (optional): Whether this column auto-increments
- `jsonPath` (optional): JSONPath expression to extract value from input JSON
- `defaultValue` (optional): Default value if jsonPath doesn't resolve

## Building and Running

### Prerequisites

- **Java 17 or higher** (tested with Java 25)
- Maven 3.6+

**Note:** This project uses standard Java (no Lombok) and works with Java 25. Mockito tests require the experimental ByteBuddy flag configured in `pom.xml` for Java 25 compatibility.

### Build

```bash
mvn clean install
```

### Run

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### API Documentation

Once running, API documentation is available at:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Configuration

Table definitions can be stored in:
1. **File System**: `~/.json-to-sql/table-definitions/` (configurable via `app.table-definitions.storage-path`)
2. **Classpath**: `src/main/resources/table-definitions/` (default definitions)

## Testing

This project includes comprehensive unit tests (81 tests total):
- ✅ All tests passing with Java 25 (using Mockito with experimental ByteBuddy support)
- Tests cover utilities, generators, services, controllers, and exception handling
- See `TESTING.md` and `MOCKITO_JAVA25_SOLUTION.md` for testing details

## Project Features

- ✅ **No Lombok** - Uses standard Java getters/setters and builder patterns
- ✅ **Java 25 Compatible** - Works with latest Java versions
- ✅ **Comprehensive Tests** - 81 unit tests covering all components
- ✅ **Mockito Support** - Works with Java 25 using experimental ByteBuddy flag

## Future Enhancements

- Multiple table support (relationships)
- UPDATE/DELETE statement generation
- Database dialect support (MySQL, PostgreSQL, Oracle)
- Conditional mapping rules
- Schema versioning
- Transaction script generation
- Custom SQL templates

## License

This project is provided as-is for demonstration purposes.
