# Logger Integration Documentation

## Overview
The Temple Billing project has been integrated with SLF4J and Logback for comprehensive logging. All logs are written to both console and files with configurable levels and rotation policies.

## Logger Configuration

### Configuration File
**Location**: `src/main/resources/logback-spring.xml`

### Log File Locations
- **Main Application Logs**: `logs/temple-billing.log`
- **Error Logs**: `logs/temple-billing-error.log`
- **Archived Logs**: `logs/temple-billing-YYYY-MM-DD.i.log` (rotated daily)

### Log Levels
- **DEBUG**: Detailed information for debugging (Page requests, search operations)
- **INFO**: General information about application flow (User creation, login, updates)
- **WARN**: Warning messages (Login failures, validation errors, access denials)
- **ERROR**: Error messages (Exceptions, unexpected errors)

### Rolling Policy
- **Max File Size**: 10MB per file
- **Retention**: 30 days
- **Frequency**: Daily rotation

## Classes with Logging

### 1. GlobalExceptionHandler
- **Purpose**: Logs all exceptions caught by the application
- **Log Levels**:
  - `WARN`: Validation errors, access denied, not found errors
  - `ERROR`: Unexpected exceptions and runtime errors
- **Example Logs**:
  ```
  WARN - Validation exception: username - Username is required; 
  ERROR - Unexpected exception occurred
  ```

### 2. AuthController
- **Purpose**: Logs authentication attempts and results
- **Log Levels**:
  - `INFO`: Login attempts and successes
  - `WARN`: Failed login attempts
- **Example Logs**:
  ```
  INFO - Login attempt for username: admin
  INFO - Login successful for username: admin
  WARN - Login failed for username: admin - Error: Invalid credentials
  ```

### 3. BookingController
- **Purpose**: Logs receipt/booking operations
- **Log Levels**:
  - `INFO`: Create, update, delete operations
  - `DEBUG`: Fetch and search operations
  - `ERROR`: PDF generation failures
- **Example Logs**:
  ```
  INFO - Creating batch receipt for user: admin
  INFO - Batch receipt created successfully with ID: 123
  DEBUG - Fetching receipts for user: admin - Page: 0, Size: 5
  INFO - Deleting receipt ID: 123 by user: admin
  ```

### 4. UserController
- **Purpose**: Logs user management operations
- **Log Levels**:
  - `INFO`: Create, update, delete, password change operations
  - `DEBUG`: Fetch operations
  - `WARN`: Security-related warnings (self-delete attempts)
- **Example Logs**:
  ```
  INFO - Creating new user: john_doe
  INFO - User created successfully: john_doe
  INFO - Updating user ID: 5
  WARN - User admin attempted to delete their own account
  INFO - Password changed successfully for user: john_doe
  ```

## How to Use Loggers

### In a Controller
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/example")
public class ExampleController {
    private static final Logger logger = LoggerFactory.getLogger(ExampleController.class);
    
    @GetMapping
    public String example() {
        logger.info("Example endpoint called");
        logger.debug("Debug information");
        logger.warn("Warning message");
        logger.error("Error message", exception);
        return "response";
    }
}
```

### In a Service
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ExampleService {
    private static final Logger logger = LoggerFactory.getLogger(ExampleService.class);
    
    public void doSomething() {
        logger.info("Service method called");
        try {
            // Business logic
        } catch (Exception e) {
            logger.error("Error occurred in doSomething", e);
        }
    }
}
```

## Viewing Logs

### Console Logs
Logs are printed to console during application runtime:
```
2026-04-09 16:05:00 - com.example.temple_billing.controller.AuthController - Login attempt for username: admin
2026-04-09 16:05:01 - com.example.temple_billing.controller.AuthController - Login successful for username: admin
```

### File Logs
View application logs:
```bash
# View main application log
tail -f logs/temple-billing.log

# View error log
tail -f logs/temple-billing-error.log

# Search for specific logs
grep "user: admin" logs/temple-billing.log
```

## Configuration Customization

### Change Log Level
Edit `logback-spring.xml`:
```xml
<!-- For application logs -->
<logger name="com.example.temple_billing" level="DEBUG"/>

<!-- For Spring Framework logs -->
<logger name="org.springframework" level="DEBUG"/>
```

### Add Custom Appenders
Add new file appender in `logback-spring.xml`:
```xml
<appender name="CUSTOM" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>${LOG_FILE_PATH}/custom.log</file>
    <!-- Configuration -->
</appender>

<logger name="com.example.temple_billing.service" level="DEBUG">
    <appender-ref ref="CUSTOM"/>
</logger>
```

## Best Practices

1. **Use Appropriate Log Levels**:
   - `DEBUG`: Detailed information for developers
   - `INFO`: Important business events
   - `WARN`: Warning situations that should be investigated
   - `ERROR`: Error events that might still allow the app to continue

2. **Use Placeholders**: Use `{}` placeholders instead of string concatenation
   ```java
   // Good
   logger.info("User {} created with ID: {}", username, id);
   
   // Avoid
   logger.info("User " + username + " created with ID: " + id);
   ```

3. **Include Context**: Log enough information to understand what happened
   ```java
   logger.info("Receipt {} updated by user: {}", receiptId, username);
   ```

4. **Log Exceptions with Full Stack Trace**:
   ```java
   logger.error("Operation failed", exception); // Includes stack trace
   ```

## Security Considerations

- **Never log passwords or sensitive data**
- **Avoid logging personal information (PII)**
- **Sanitize user inputs before logging**
- **Use appropriate log retention policies**

## Performance Notes

- Logging has minimal performance impact
- File I/O is asynchronous in Logback
- Log rotation prevents disk space issues
- DEBUG logs are disabled in production by default

