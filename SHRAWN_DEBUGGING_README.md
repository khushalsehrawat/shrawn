# Shrawn Backend Debugging & Error Resolution Guide

> **Project:** Shrawn Expense Tracker Backend  
> **Stack:** Java, Spring Boot, Spring Security, JWT, PostgreSQL, Flyway, Redis, Docker Compose  
> **Purpose of this README:**  
> This file records all major errors we faced from project creation until the application finally started successfully. It explains **what happened**, **why it happened**, **how we fixed it**, and **how to solve similar errors in the future**.

---

## Table of Contents

1. [Big Picture: What We Built](#big-picture-what-we-built)
2. [How Spring Boot Starts This Project](#how-spring-boot-starts-this-project)
3. [Final Current Status](#final-current-status)
4. [Timeline of Errors We Fixed](#timeline-of-errors-we-fixed)
5. [Error 1: Wrong Spring Boot / Dependency Compatibility](#error-1-wrong-spring-boot--dependency-compatibility)
6. [Error 2: Main Class / Package Path Confusion](#error-2-main-class--package-path-confusion)
7. [Error 3: Wrong `CurrentUserArgumentResolver` Imports](#error-3-wrong-currentuserargumentresolver-imports)
8. [Error 4: Mapper Method Missing in User Module](#error-4-mapper-method-missing-in-user-module)
9. [Error 5: Analytics Repository Query Index Mistake](#error-5-analytics-repository-query-index-mistake)
10. [Error 6: JWT Issuer Was Blank](#error-6-jwt-issuer-was-blank)
11. [Error 7: JWT Secret Too Short for HS512](#error-7-jwt-secret-too-short-for-hs512)
12. [Error 8: Docker Compose and YAML Files Got Mixed](#error-8-docker-compose-and-yaml-files-got-mixed)
13. [Error 9: Duplicate `jdbc:` Key in YAML](#error-9-duplicate-jdbc-key-in-yaml)
14. [Error 10: PostgreSQL TimeZone `Asia/Calcutta` Failure](#error-10-postgresql-timezone-asiacalcutta-failure)
15. [Final Successful Startup](#final-successful-startup)
16. [Warnings Still Visible But Not Fatal](#warnings-still-visible-but-not-fatal)
17. [Final Recommended Files](#final-recommended-files)
18. [Debugging Rules You Should Remember Forever](#debugging-rules-you-should-remember-forever)
19. [Render Deployment Notes](#render-deployment-notes)
20. [Future Error Decision Tree](#future-error-decision-tree)
21. [Quick Commands](#quick-commands)
22. [Glossary for Beginners](#glossary-for-beginners)

---

# Big Picture: What We Built

Shrawn is an **Expense Tracker backend**.

The goal is:

- User can register/login.
- User can create categories.
- User can create tags.
- User can add expenses/income.
- User can filter expenses by date, category, tag, payment method, etc.
- User can create budgets.
- User can view analytics.
- Backend should be industry-style and deployable later.

## Architecture Style

We chose a **modular monolith**.

That means:

```text
One Spring Boot application
But code is divided into clean modules:
auth, user, expense, category, tag, budget, analytics, shared
```

## Why modular monolith first?

Because for a serious backend project, microservices too early creates too much complexity:

```text
Microservices need:
- service discovery
- API gateway
- distributed tracing
- network failure handling
- separate deployment
- separate databases sometimes
- message queues
- monitoring
```

For this project, modular monolith is better first:

```text
Clean code + easier deployment + future microservice migration possible
```

---

# How Spring Boot Starts This Project

Understanding startup order helps you debug errors faster.

```mermaid
flowchart TD
    A[Run ShrawnApplication.main] --> B[Load application.yml]
    B --> C[Load application-local.yml because local profile]
    C --> D[Bind configuration properties like JwtProperties]
    D --> E[Create Spring beans]
    E --> F[Start embedded Tomcat]
    F --> G[Create filters like JwtAuthenticationFilter]
    G --> H[Flyway connects to PostgreSQL]
    H --> I[Flyway runs migration SQL]
    I --> J[Hibernate validates JPA entities vs DB schema]
    J --> K[Spring Security completes]
    K --> L[Tomcat started on port 8080]
```

Important:

> **Flyway runs before Hibernate finishes.**  
> So if Flyway cannot connect to DB, the app stops before JPA can work.

---

# Final Current Status

The latest run reached successful startup:

```text
Successfully applied 1 migration to schema "public", now at version v001
Tomcat started on port 8080
Started ShrawnApplication
```

So these parts are working:

| Component | Status |
|---|---|
| Spring Boot startup | Working |
| JWT config | Working |
| PostgreSQL connection | Working |
| Flyway migration | Working |
| Hibernate/JPA | Working |
| Tomcat | Working |
| Swagger | Enabled |
| Actuator | Enabled |
| Redis dependency | Present, warning only |

---

# Timeline of Errors We Fixed

```mermaid
timeline
    title Shrawn Debugging Journey
    section Dependency setup
      Wrong/incompatible Springdoc/Spring Boot jars : Fixed springdoc version
      Main class/package mismatch : Fixed package path
    section Java code compile errors
      CurrentUserArgumentResolver wrong imports : Fixed MVC resolver imports
      User mapper missing method : Added toCredentials mapper
      Analytics Object[] index mistake : Fixed indexes
    section Configuration errors
      JWT issuer blank : Fixed properties prefix
      JWT secret too short : Added HS512-safe 64+ char secret
      Docker compose + application.yml mixed : Split files
      Duplicate jdbc key : Merged YAML jdbc keys
    section Runtime DB errors
      PostgreSQL TimeZone Asia/Calcutta : Forced UTC before DB connection
    section Final result
      Flyway migration applied : DB schema created
      Tomcat started : Backend running
```

---

# Error 1: Wrong Spring Boot / Dependency Compatibility

## Symptom

Earlier we saw classpath/version errors like missing Spring Boot classes or incompatible dependency behavior.

Typical examples:

```text
NoClassDefFoundError
ClassNotFoundException
package org.springframework.boot.autoconfigure... does not exist
```

## Root Cause

Spring Boot version and some dependencies were not compatible.

One major issue was using a wrong Springdoc version. A too-new or incompatible Springdoc can pull Spring Boot 4 related jars while the project is Spring Boot 3.x.

## Final Fix

Use:

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.5.7</version>
    <relativePath/>
</parent>
```

And:

```xml
<springdoc.version>2.8.14</springdoc.version>
```

Dependency:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>${springdoc.version}</version>
</dependency>
```

## Lifetime Rule

When you see classpath errors:

```text
NoClassDefFoundError
ClassNotFoundException
NoSuchMethodError
```

do not randomly edit code first.

First check:

```text
pom.xml versions
dependency tree
Spring Boot version compatibility
```

Command:

```powershell
.\mvnw.cmd dependency:tree
```

---

# Error 2: Main Class / Package Path Confusion

## Symptom

Spring Boot could not find or run the main class.

Example:

```text
Could not find or load main class com.expenseTracker.shrawn.ShrawnApplication
```

## Root Cause

In Java, package name must match folder path.

If the file says:

```java
package com.expenseTracker.shrawn;
```

then file must be located at:

```text
src/main/java/com/expenseTracker/shrawn/ShrawnApplication.java
```

## Final Correct Main Class

```java
package com.expenseTracker.shrawn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.TimeZone;

@SpringBootApplication
@EnableJpaAuditing
public class ShrawnApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(ShrawnApplication.class, args);
    }
}
```

## Why `TimeZone.setDefault(...)` is here

We added it because PostgreSQL/Flyway was failing with:

```text
TimeZone: Asia/Calcutta
```

Setting JVM timezone to UTC before Spring starts prevents PostgreSQL JDBC from sending the old timezone alias.

---

# Error 3: Wrong `CurrentUserArgumentResolver` Imports

## Symptom

Compile errors in:

```text
CurrentUserArgumentResolver
```

## Root Cause

The resolver accidentally used messaging-related imports:

```java
org.springframework.messaging.Message
org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver
```

But we are building a REST MVC backend, not WebSocket/message handler.

## Correct File

```java
package com.expenseTracker.shrawn.auth.infrastrucutre.security;

import com.expenseTracker.shrawn.shared.security.AuthenticatedUser;
import com.expenseTracker.shrawn.shared.security.CurrentUser;
import com.expenseTracker.shrawn.shared.security.SecurityUtils;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && AuthenticatedUser.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        return SecurityUtils.getCurrentUser();
    }
}
```

## Concept

This resolver lets controller methods do this:

```java
@GetMapping("/me")
public UserProfileResponse me(@CurrentUser AuthenticatedUser user) {
    ...
}
```

Instead of manually reading SecurityContext every time.

---

# Error 4: Mapper Method Missing in User Module

## Symptom

Compile error around:

```java
mapper::toCredentials
```

or missing method:

```text
cannot find symbol: method toCredentials(...)
```

## Root Cause

Repository adapter needed to convert `UserJpaEntity` into `UserAccountCredentials`, but mapper did not have that method.

## Fix

Add this method in `UserPersistenceMapper`:

```java
public UserAccountCredentials toCredentials(UserJpaEntity entity) {
    return new UserAccountCredentials(
            entity.getId(),
            new EmailAddress(entity.getEmail()),
            entity.getPasswordHash(),
            entity.getStatus()
    );
}
```

## Concept

Different layers use different objects:

```mermaid
flowchart LR
    A[Database Row] --> B[JPA Entity]
    B --> C[Domain Model]
    B --> D[Credentials View]
```

Why?

- JPA entity is for persistence.
- Domain model is for business logic.
- Credentials view is only for authentication.

This avoids leaking password hash everywhere.

---

# Error 5: Analytics Repository Query Index Mistake

## Symptom

Analytics repository failed because `Object[]` query result indexes were wrong.

## Root Cause

Native/JPQL aggregate queries return data in array positions:

```java
Object[] row
```

If query returns:

```sql
category_id, category_name, sum(amount)
```

then:

```text
row[0] = category_id
row[1] = category_name
row[2] = sum(amount)
```

Using wrong index causes wrong casting or wrong totals.

## Fix Pattern

Category analytics:

```java
Money total = totalAmount(rawRows, 2);
```

Payment method analytics:

```java
Money total = totalAmount(rawRows, 1);
```

Tag analytics:

```java
Money total = totalAmount(rawRows, 2);
```

## Lifetime Rule

Whenever you write query like:

```java
@Query("select a, b, sum(c) ...")
```

write a comment:

```java
// row[0] = a
// row[1] = b
// row[2] = sum(c)
```

This prevents future mistakes.

---

# Error 6: JWT Issuer Was Blank

## Symptom

The app failed with:

```text
JWT issuer must not be blank
```

## Root Cause

Spring Boot configuration binding did not find the issuer value.

We had mismatch between YAML root and Java configuration prefix.

Example problem:

```yaml
shrawn:
  security:
    jwt:
      issuer: shrawn-api
```

but Java class was reading:

```java
@ConfigurationProperties(prefix = "spendwise.security.jwt")
```

So Java looked for:

```yaml
spendwise:
  security:
    jwt:
      issuer: ...
```

but YAML had:

```yaml
shrawn:
```

Result: issuer became `null`.

## Final Fix

Use project name consistently.

In `JwtProperties.java`:

```java
@ConfigurationProperties(prefix = "shrawn.security.jwt")
```

In `application.yml`:

```yaml
shrawn:
  security:
    jwt:
      issuer: shrawn-api
      secret: ${JWT_SECRET:shrawn-local-dev-secret-key-for-hs512-must-be-longer-than-sixty-four-characters-2026}
      access-token-expiration: 15m
      refresh-token-expiration: 30d
```

## Visual

```mermaid
flowchart LR
    A[application.yml] -->|shrawn.security.jwt| B[JwtProperties]
    B --> C[issuer]
    B --> D[secret]
    B --> E[access-token-expiration]
    B --> F[refresh-token-expiration]
```

If the prefix is different:

```mermaid
flowchart LR
    A[application.yml: shrawn.security.jwt] -. mismatch .-> B[JwtProperties: spendwise.security.jwt]
    B --> C[issuer = null]
    C --> D[App crash]
```

## Lifetime Rule

When `@ConfigurationProperties` values are null:

1. Check prefix.
2. Check YAML indentation.
3. Check active profile.
4. Check environment variable override.
5. Check property name format.

---

# Error 7: JWT Secret Too Short for HS512

## Symptom

The app failed with:

```text
JWT secret must contain at least 64 characters for HS512
```

## Root Cause

We chose HS512 JWT signing. HS512 needs a strong secret. A short secret is unsafe and rejected by our validation.

## Fix

Use a long local development secret:

```yaml
secret: ${JWT_SECRET:shrawn-local-dev-secret-key-for-hs512-must-be-longer-than-sixty-four-characters-2026}
```

## Important Environment Variable Trap

This syntax:

```yaml
${JWT_SECRET:default-value}
```

means:

```text
If JWT_SECRET exists in environment, use that.
Otherwise use default-value.
```

So even if YAML default is long, a short Windows/IntelliJ environment variable can override it.

Check PowerShell:

```powershell
echo $env:JWT_SECRET
```

Set for current terminal:

```powershell
$env:JWT_SECRET="your-very-long-secret-at-least-64-characters-for-hs512-signing"
```

Remove for current terminal:

```powershell
Remove-Item Env:JWT_SECRET
```

## Lifetime Rule

For production:

```text
Never commit real JWT secret to GitHub.
Use Render/AWS/GitHub Actions environment variables.
```

---

# Error 8: Docker Compose and YAML Files Got Mixed

## Symptom

You pasted `compose.yaml` and `application.yml` content into one file.

Example broken ending:

```yaml
networks:
  shrawn-network:
    driver: bridgespring:
```

## Root Cause

This line accidentally joined two different files:

```yaml
driver: bridge
```

and:

```yaml
spring:
```

Result became:

```yaml
driver: bridgespring:
```

## Correct Separation

```mermaid
flowchart TD
    A[D:/shrawn/compose.yaml] --> B[Docker services: postgres, redis]
    C[src/main/resources/application.yml] --> D[Spring common config]
    E[src/main/resources/application-local.yml] --> F[Local DB, Redis, logs]
    G[D:/shrawn/.env] --> H[Local environment variables]
```

## Rule

Never put `spring:` inside `compose.yaml`.

Never put `services:` inside `application.yml`.

---

# Error 9: Duplicate `jdbc:` Key in YAML

## Symptom

The app failed before startup with:

```text
DuplicateKeyException
found duplicate key jdbc
```

## Root Cause

YAML had this:

```yaml
hibernate:
  jdbc:
    time_zone: UTC
  format_sql: false
  order_inserts: true
  order_updates: true
  jdbc:
    batch_size: 25
```

Same key repeated:

```yaml
jdbc:
jdbc:
```

YAML does not allow duplicate keys in the same block.

## Fix

Merge both values under one `jdbc:`:

```yaml
spring:
  jpa:
    properties:
      hibernate:
        jdbc:
          time_zone: UTC
          batch_size: 25
        format_sql: false
        order_inserts: true
        order_updates: true
```

## Lifetime Rule

YAML indentation is part of meaning.

Wrong:

```yaml
parent:
  child:
    a: 1
  child:
    b: 2
```

Correct:

```yaml
parent:
  child:
    a: 1
    b: 2
```

---

# Error 10: PostgreSQL TimeZone `Asia/Calcutta` Failure

## Symptom

Repeated error:

```text
FATAL: invalid value for parameter "TimeZone": "Asia/Calcutta"
```

It happened during Flyway connection.

## Why This Was Confusing

Your `.env` had:

```env
DB_URL=jdbc:postgresql://localhost:5432/shrawn
```

No timezone there.

Still PostgreSQL received:

```text
Asia/Calcutta
```

Why?

Because Java/JVM was running in India local timezone. PostgreSQL JDBC can send the JVM default timezone to the server when opening connection. On this system it sent old alias:

```text
Asia/Calcutta
```

PostgreSQL rejected it.

## Why It Did Not Happen in Other Projects

Because this project has:

```text
Spring Boot + Flyway + PostgreSQL + Docker Compose + JPA validation
```

Flyway connects very early during startup. Some previous projects may not have used Flyway, or may not have connected to PostgreSQL at startup in this strict way.

## Fix We Used

In `ShrawnApplication.java`:

```java
TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
```

Full file:

```java
package com.expenseTracker.shrawn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.TimeZone;

@SpringBootApplication
@EnableJpaAuditing
public class ShrawnApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(ShrawnApplication.class, args);
    }
}
```

## Alternative Fixes

### Option A: JVM option

```text
-Duser.timezone=UTC
```

### Option B: DB URL option

```env
DB_URL=jdbc:postgresql://localhost:5432/shrawn?options=-c%20TimeZone=UTC
```

### Option C: Docker environment

```yaml
TZ: UTC
PGTZ: UTC
```

## Which Fix Is Best?

For this project:

```text
Best practical fix:
TimeZone.setDefault(UTC) in main class
+
Keep DB URL clean
```

Why?

Because it works in IntelliJ, terminal, and deployment.

## Timezone Mental Model

```mermaid
flowchart LR
    A[User in India] -->|sees time as IST| B[Frontend]
    B -->|sends date/time request| C[Backend]
    C -->|stores Instant/TIMESTAMPTZ| D[Database UTC]
    D -->|returns UTC data| C
    C --> B
    B -->|displays local time| A
```

Rule:

```text
Database/server = UTC
User display = local timezone
```

---

# Final Successful Startup

The final log showed:

```text
Successfully applied 1 migration to schema "public", now at version v001
Tomcat started on port 8080
Started ShrawnApplication
```

This means:

```mermaid
flowchart TD
    A[Spring Boot started] --> B[JWT config loaded]
    B --> C[PostgreSQL connection opened]
    C --> D[Flyway migration validated]
    D --> E[Flyway migration applied]
    E --> F[Hibernate EntityManagerFactory initialized]
    F --> G[Tomcat started]
    G --> H[Application running]
```

---

# Warnings Still Visible But Not Fatal

After the app started, there were some warnings.

## Warning 1: Redis Repository Warning

Message:

```text
Spring Data Redis - Could not safely identify store assignment
```

## Meaning

Spring saw both:

```text
Spring Data JPA
Spring Data Redis
```

So it scanned repositories and asked:

```text
Are these Redis repositories?
```

They are not; they are JPA repositories.

## Fix

In `application.yml`:

```yaml
spring:
  data:
    redis:
      repositories:
        enabled: false
```

If you already have:

```yaml
spring:
  data:
    web:
      pageable:
```

then use:

```yaml
spring:
  data:
    redis:
      repositories:
        enabled: false

    web:
      pageable:
        default-page-size: 20
        max-page-size: 100
        one-indexed-parameters: false
```

---

## Warning 2: Generated Security Password

Message:

```text
Using generated security password
```

## Meaning

Spring Boot created a default in-memory user because it still detected default security behavior.

This is not fatal for startup, but we should clean it before production.

## Fix Direction

Check `SecurityConfiguration` and ensure we have:

```java
@Bean
SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        JwtAuthenticationFilter jwtAuthenticationFilter
) throws Exception {
    return http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            "/api/v1/auth/**",
                            "/actuator/health",
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/v3/api-docs/**"
                    ).permitAll()
                    .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
}
```

Also define:

```java
@Bean
PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

---

## Warning 3: Springdoc Production Warning

Message:

```text
SpringDoc /v3/api-docs endpoint is enabled by default.
Swagger UI endpoint is enabled by default.
```

For local development, this is okay.

For production:

```yaml
springdoc:
  api-docs:
    enabled: false
  swagger-ui:
    enabled: false
```

---

# Final Recommended Files

## `compose.yaml`

Location:

```text
D:\shrawn\compose.yaml
```

```yaml
services:
  postgres:
    image: postgres:16-alpine
    container_name: shrawn-postgres
    restart: unless-stopped
    environment:
      POSTGRES_DB: ${POSTGRES_DB:-shrawn}
      POSTGRES_USER: ${POSTGRES_USER:-shrawn}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD:-shrawn}
      TZ: UTC
      PGTZ: UTC
    ports:
      - "${POSTGRES_PORT:-5432}:5432"
    volumes:
      - shrawn-postgres-data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U $${POSTGRES_USER:-shrawn} -d $${POSTGRES_DB:-shrawn}"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - shrawn-network

  redis:
    image: redis:7-alpine
    container_name: shrawn-redis
    restart: unless-stopped
    command: redis-server --appendonly yes
    environment:
      TZ: UTC
    ports:
      - "${REDIS_PORT:-6379}:6379"
    volumes:
      - shrawn-redis-data:/data
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - shrawn-network

volumes:
  shrawn-postgres-data:
  shrawn-redis-data:

networks:
  shrawn-network:
    driver: bridge
```

---

## `.env`

Location:

```text
D:\shrawn\.env
```

```env
POSTGRES_DB=shrawn
POSTGRES_USER=shrawn
POSTGRES_PASSWORD=shrawn
POSTGRES_PORT=5432

DB_URL=jdbc:postgresql://localhost:5432/shrawn
DB_USERNAME=shrawn
DB_PASSWORD=shrawn

REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=local

FRONTEND_URL=http://localhost:5173

LOCAL_STORAGE_PATH=./data/uploads
```

---

## `application.yml`

Location:

```text
src/main/resources/application.yml
```

```yaml
spring:
  application:
    name: shrawn

  profiles:
    default: local

  lifecycle:
    timeout-per-shutdown-phase: 30s

  jackson:
    default-property-inclusion: non_null
    deserialization:
      fail-on-unknown-properties: true
    serialization:
      write-dates-as-timestamps: false
    time-zone: UTC

  datasource:
    hikari:
      pool-name: ShrawnHikariPool
      auto-commit: false
      connection-timeout: 30000
      validation-timeout: 5000
      idle-timeout: 600000
      max-lifetime: 1800000

  jpa:
    open-in-view: false
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        jdbc:
          time_zone: UTC
          batch_size: 25
        format_sql: false
        order_inserts: true
        order_updates: true

  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: false
    clean-disabled: true
    validate-on-migrate: true

  servlet:
    multipart:
      enabled: true
      max-file-size: 10MB
      max-request-size: 12MB

  data:
    redis:
      repositories:
        enabled: false

    web:
      pageable:
        default-page-size: 20
        max-page-size: 100
        one-indexed-parameters: false

server:
  port: ${PORT:${SERVER_PORT:8080}}
  shutdown: graceful
  forward-headers-strategy: framework

  compression:
    enabled: true
    min-response-size: 2KB
    mime-types:
      - application/json
      - application/xml
      - text/html
      - text/plain
      - text/css
      - application/javascript

  error:
    include-message: never
    include-binding-errors: never
    include-stacktrace: never
    include-exception: false
    whitelabel:
      enabled: false

management:
  endpoints:
    web:
      base-path: /actuator
      exposure:
        include:
          - health
          - info
          - metrics

  endpoint:
    health:
      probes:
        enabled: true
      show-details: never

  health:
    livenessstate:
      enabled: true
    readinessstate:
      enabled: true

logging:
  level:
    root: INFO
    com.expenseTracker.shrawn: INFO
    org.springframework.web: INFO
    org.hibernate.SQL: WARN

shrawn:
  api:
    base-path: /api/v1

  pagination:
    maximum-page-size: 100

  upload:
    maximum-receipt-size: 10MB
    allowed-content-types:
      - image/jpeg
      - image/png
      - application/pdf

  security:
    resource-ownership-checks-enabled: true
    jwt:
      issuer: shrawn-api
      secret: ${JWT_SECRET:shrawn-local-dev-secret-key-for-hs512-must-be-longer-than-sixty-four-characters-2026}
      access-token-expiration: 15m
      refresh-token-expiration: 30d
```

---

## `application-local.yml`

Location:

```text
src/main/resources/application-local.yml
```

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/shrawn}
    username: ${DB_USERNAME:shrawn}
    password: ${DB_PASSWORD:shrawn}
    driver-class-name: org.postgresql.Driver
    hikari:
      minimum-idle: 2
      maximum-pool-size: 10
      leak-detection-threshold: 60000

  flyway:
    enabled: true
    url: ${DB_URL:jdbc:postgresql://localhost:5432/shrawn}
    user: ${DB_USERNAME:shrawn}
    password: ${DB_PASSWORD:shrawn}

  jpa:
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        generate_statistics: false

  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}
      timeout: 2s

server:
  port: ${PORT:${SERVER_PORT:8080}}

management:
  endpoint:
    health:
      show-details: always

logging:
  level:
    com.expenseTracker.shrawn: DEBUG
    org.springframework.web: INFO
    org.springframework.security: INFO
    org.hibernate.SQL: DEBUG
    org.hibernate.orm.jdbc.bind: TRACE

shrawn:
  storage:
    provider: local
    local:
      root-directory: ${LOCAL_STORAGE_PATH:./data/uploads}

  frontend:
    allowed-origins:
      - ${FRONTEND_URL:http://localhost:5173}

  security:
    secure-cookies: false
```

---

## `ShrawnApplication.java`

Location:

```text
src/main/java/com/expenseTracker/shrawn/ShrawnApplication.java
```

```java
package com.expenseTracker.shrawn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.TimeZone;

@SpringBootApplication
@EnableJpaAuditing
public class ShrawnApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(ShrawnApplication.class, args);
    }
}
```

---

# Debugging Rules You Should Remember Forever

## Rule 1: Read the "Caused by" from bottom to top

Spring errors are long. Do not panic.

Example:

```text
ApplicationContextException
BeanCreationException
FlywaySqlException
PSQLException: FATAL invalid TimeZone
```

The real error is usually near the bottom:

```text
PSQLException: FATAL invalid TimeZone
```

## Rule 2: One error can create many fake-looking errors

Example:

```text
entityManagerFactory failed
flywayInitializer failed
database connection failed
timezone invalid
```

Only the last one is root cause:

```text
timezone invalid
```

## Rule 3: Config prefix must match Java class

YAML:

```yaml
shrawn:
  security:
    jwt:
```

Java:

```java
@ConfigurationProperties(prefix = "shrawn.security.jwt")
```

These must match.

## Rule 4: Environment variables override YAML defaults

YAML:

```yaml
secret: ${JWT_SECRET:default-secret}
```

If environment has `JWT_SECRET`, Spring uses that.

## Rule 5: Docker Compose is not Spring config

Docker file:

```yaml
services:
```

Spring file:

```yaml
spring:
```

Never mix them.

## Rule 6: `ddl-auto: validate` means Hibernate will not create tables

With Flyway:

```yaml
ddl-auto: validate
```

Meaning:

```text
Flyway creates schema.
Hibernate only checks schema.
```

This is industry-style.

## Rule 7: Backend stores time in UTC

```text
Database = UTC
Backend Instant = UTC
Frontend display = user timezone
```

---

# Render Deployment Notes

Before deploying to Render:

## Required Environment Variables

```env
SPRING_PROFILES_ACTIVE=prod
PORT=8080
DB_URL=jdbc:postgresql://<host>:5432/<database>
DB_USERNAME=<username>
DB_PASSWORD=<password>
JWT_SECRET=<at-least-64-character-production-secret>
```

## Important

Render PostgreSQL sometimes gives URL like:

```text
postgresql://user:password@host:port/db
```

Spring JDBC needs:

```text
jdbc:postgresql://host:port/db
```

Do not paste normal PostgreSQL URL directly unless your code converts it.

## Do not use local Docker Compose in Render

```text
compose.yaml is for local development.
Render should use Render-managed PostgreSQL.
```

---

# Future Error Decision Tree

```mermaid
flowchart TD
    A[App failed?] --> B{Compile error or runtime error?}
    B -->|Compile| C[Check Java file, imports, method names, packages]
    B -->|Runtime| D[Read bottom Caused by]
    D --> E{Mentions YAML?}
    E -->|Yes| F[Check indentation, duplicate keys, profile file]
    E -->|No| G{Mentions JWT?}
    G -->|Yes| H[Check JwtProperties prefix, issuer, secret length]
    G -->|No| I{Mentions Flyway or DB?}
    I -->|Yes| J[Check DB_URL, Docker, Postgres running, migration SQL]
    I -->|No| K{Mentions port?}
    K -->|Yes| L[Check server.port and whether port 8080 is busy]
    K -->|No| M[Check dependency compatibility and classpath]
```

---

# Quick Commands

## Start Docker services

```powershell
docker compose up -d
```

## Stop Docker services

```powershell
docker compose down
```

## Delete local DB volume

Only for local development:

```powershell
docker compose down -v
```

## Run app

```powershell
.\mvnw.cmd clean spring-boot:run
```

## Compile only

```powershell
.\mvnw.cmd clean compile
```

## Check environment variables in PowerShell

```powershell
echo $env:DB_URL
echo $env:JWT_SECRET
echo $env:JAVA_TOOL_OPTIONS
```

## Check containers

```powershell
docker ps
```

## Check logs

```powershell
docker compose logs postgres
docker compose logs redis
```

---

# Glossary for Beginners

## Spring Boot

Framework that starts your backend application easily.

## Bean

An object managed by Spring.

Example:

```java
@Service
public class AuthenticationService { }
```

Spring creates and manages this object.

## ApplicationContext

Spring's container where beans live.

If a bean fails, app startup fails.

## Flyway

Database migration tool.

It runs files like:

```text
V001__create_initial_schema.sql
```

to create/update database schema.

## Migration

A versioned database change.

Example:

```text
V001 create tables
V002 add column
V003 add index
```

## PostgreSQL

Database used by this project.

## Redis

Fast in-memory store. In this project it may later be used for cache/rate-limit/session-like features.

## JWT

Token used for authentication.

Access token proves the user is logged in.

## HS512

JWT signing algorithm. Needs strong secret.

## YAML

Configuration format used by Spring Boot.

Indentation matters.

## `.env`

Environment variable file used by Docker Compose and sometimes tools.

## Docker Compose

Runs local services like PostgreSQL and Redis.

## HikariCP

Database connection pool used by Spring Boot.

## Hibernate

JPA implementation that maps Java entities to database tables.

## `ddl-auto: validate`

Hibernate checks tables exist and match entities, but does not create them.

---

# Final Mental Model

```mermaid
flowchart LR
    A[You write code] --> B[Spring Boot starts]
    B --> C[Reads YAML + env]
    C --> D[Creates beans]
    D --> E[Connects DB]
    E --> F[Flyway migrates]
    F --> G[Hibernate validates]
    G --> H[Tomcat starts]
    H --> I[API ready]
```

If app fails, ask:

```text
At which step did it fail?
```

That one question makes debugging much easier.

---

# Current Success Checklist

- [x] Spring Boot version fixed
- [x] Springdoc version fixed
- [x] Main package/class fixed
- [x] Resolver imports fixed
- [x] Mapper method fixed
- [x] Analytics query indexes fixed
- [x] JWT issuer prefix fixed
- [x] JWT secret length fixed
- [x] YAML duplicate key fixed
- [x] Docker Compose separated from Spring YAML
- [x] PostgreSQL timezone issue fixed
- [x] Flyway migration applied
- [x] Tomcat running on port 8080

---

**End of README.**
