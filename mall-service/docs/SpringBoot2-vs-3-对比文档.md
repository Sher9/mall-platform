# Spring Boot 2.x 与 Spring Boot 3.x 对比文档

> 以 `microservice-platform` 企业级微服务项目为例

## 目录

- [1. JDK 版本要求](#1-jdk-版本要求)
- [2. Jakarta EE 替代 Java EE](#2-jakarta-ee-替代-java-ee)
- [3. Spring Framework 版本](#3-spring-framework-版本)
- [4. Spring Security 配置变更](#4-spring-security-配置变更)
- [5. JWT 库变化](#5-jwt-库变化)
- [6. API 文档工具变化](#6-api-文档工具变化)
- [7. MyBatis-Plus 版本适配](#7-mybatis-plus-版本适配)
- [8. 配置文件变更](#8-配置文件变更)
- [9. 虚拟线程与 AOT 编译](#9-虚拟线程与-aot-编译)
- [10. 其他值得注意的变更](#10-其他值得注意的变更)
- [总结对比表](#总结对比表)

---

## 1. JDK 版本要求

| 项目 | Spring Boot 2.x | Spring Boot 3.x |
| :--- | :--- | :--- |
| **最低 JDK 版本** | Java 8 | **Java 17** |
| **推荐 JDK 版本** | Java 11 / 17 | Java 17 / 21 |
| **虚拟线程支持** | 不支持 | 支持（需 JDK 21+） |
| **Record 类支持** | 不支持 | 支持（JDK 16+） |
| **Sealed 类支持** | 不支持 | 支持（JDK 17+） |

### 本项目体现

```xml
<!-- pom.xml -->
<properties>
    <java.version>17</java.version>
</properties>
```

### 影响

- 必须升级 JDK 到 17 以上
- 可以使用 Java 新特性：Record、Sealed Class、Pattern Matching、Text Blocks
- 使用 JDK 21+ 时可开启虚拟线程

---

## 2. Jakarta EE 替代 Java EE

这是 Spring Boot 3 **最核心的变更**，影响几乎所有的企业级依赖。

| 包 | Spring Boot 2.x | Spring Boot 3.x |
| :--- | :--- | :--- |
| Servlet API | `javax.servlet.*` | **`jakarta.servlet.*`** |
| Validation | `javax.validation.*` | **`jakarta.validation.*`** |
| Persistence (JPA) | `javax.persistence.*` | **`jakarta.persistence.*`** |
| Transaction | `javax.transaction.*` | **`jakarta.transaction.*`** |
| Annotation | `javax.annotation.*` | **`jakarta.annotation.*`** |
| Mail | `javax.mail.*` | **`jakarta.mail.*`** |

### 迁移注意事项

1. **全局搜索替换**：将代码中所有 `javax.` 前缀替换为 `jakarta.`
2. **第三方库兼容**：确保所有依赖的第三方库也升级到了支持 Jakarta EE 的版本
3. **Servlet 容器**：Tomcat 10+、Jetty 11+ 才支持 Jakarta EE

---

## 3. Spring Framework 版本

| | Spring Boot 2.x | Spring Boot 3.x |
| :--- | :--- | :--- |
| Spring Framework | 5.3.x | **6.x** |
| AOT 编译 | 不支持 | 支持 |
| GraalVM Native Image | 不支持 | 支持 |
| HTTP Interface | 不支持 | 支持 |

### 新特性

- **声明式 HTTP 客户端**：使用 `@HttpExchange` 替代 Feign（可选）
- **ProblemDetail**：RFC 7807 标准的错误响应
- **AOT 编译**：大幅减少启动时间

---

## 4. Spring Security 配置变更

| 方式 | Spring Boot 2.x | Spring Boot 3.x |
| :--- | :--- | :--- |
| 配置基类 | `WebSecurityConfigurerAdapter`（已废弃） | **Bean 声明方式** |
| 授权配置 | `http.authorizeRequests()` | **`http.authorizeHttpRequests()`** |
| 配置风格 | 链式调用 `.and()` | **Lambda DSL** |
| CSRF | `.csrf().disable()` | **`.csrf(AbstractHttpConfigurer::disable)`** |

### 代码对比

```java
/* ========== Spring Boot 2.x ========== */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers("/login", "/register").permitAll()
            .anyRequest().authenticated()
            .and()
            .formLogin();
    }
}

/* ========== Spring Boot 3.x（本项目）========== */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/register").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> {});
        return http.build();
    }
}
```

### 关键变化

- 不再继承 `WebSecurityConfigurerAdapter`
- `antMatchers()` 改为 `requestMatchers()`
- 不再链式调用，改为 Lambda 表达式
- Bean 声明方式更灵活

---

## 5. JWT 库变化

| | Spring Boot 2.x | Spring Boot 3.x |
| :--- | :--- | :--- |
| jjwt 版本 | 0.9.x (已被淘汰) | **0.12.x** |
| 密钥创建 | `Keys.secretKeyFor(SignatureAlgorithm.HS256)` | **`Keys.hmacShaKeyFor(bytes)`** |
| 解析 token | `parser().setSigningKey()` | **`parser().verifyWith()`** |
| Builder API | `Jwts.builder().setSubject()` | **`Jwts.builder().claims().subject()`** |

### 代码对比

```java
/* ========== Spring Boot 2.x + jjwt 0.9 ========== */
String token = Jwts.builder()
    .setSubject(username)
    .setIssuedAt(new Date())
    .setExpiration(new Date(System.currentTimeMillis() + 86400000))
    .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
    .compact();

Claims claims = Jwts.parser()
    .setSigningKey(SECRET_KEY)
    .parseClaimsJws(token)
    .getBody();

/* ========== Spring Boot 3.x + jjwt 0.12（本项目）========= */
String token = Jwts.builder()
    .subject(username)
    .issuedAt(new Date())
    .expiration(new Date(System.currentTimeMillis() + 86400000))
    .signWith(getSigningKey())
    .compact();

Claims claims = Jwts.parser()
    .verifyWith(getSigningKey())
    .build()
    .parseSignedClaims(token)
    .getPayload();
```

---

## 6. API 文档工具变化

| | Spring Boot 2.x | Spring Boot 3.x |
| :--- | :--- | :--- |
| 推荐工具 | SpringFox (停更多年) | **Springdoc OpenAPI** |
| Maven 坐标 | `io.springfox` | **`org.springdoc:springdoc-openapi-starter-webmvc-ui`** |
| 访问地址 | `/swagger-ui.html` | **`/swagger-ui.html`** |
| 注解 | `@Api`, `@ApiOperation` | **`@Tag`, `@Operation`** |

---

## 7. MyBatis-Plus 版本适配

| | Spring Boot 2.x | Spring Boot 3.x |
| :--- | :--- | :--- |
| 依赖坐标 | `mybatis-plus-boot-starter` | **`mybatis-plus-boot3-starter`** |
| 分页插件 | `PaginationInterceptor` | **`PaginationInnerInterceptor`** |
| 配置方式 | 直接 new | **`MybatisPlusInterceptor` + addInnerInterceptor** |

### 代码对比

```xml
<!-- Spring Boot 2.x -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
</dependency>

<!-- Spring Boot 3.x（本项目） -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot3-starter</artifactId>
</dependency>
```

```java
/* ========== Spring Boot 2.x ========== */
@Bean
public PaginationInterceptor paginationInterceptor() {
    return new PaginationInterceptor();
}

/* ========== Spring Boot 3.x（本项目）========= */
@Bean
public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
    return interceptor;
}
```

---

## 8. 配置文件变更

| 配置 | Spring Boot 2.x | Spring Boot 3.x |
| :--- | :--- | :--- |
| 数据库驱动 | 可省略 `driver-class-name` | **建议显式声明** |
| 字符集 | `characterEncoding=utf8` | **`characterEncoding=utf8mb4`** |
| 时区 | `serverTimezone=GMT%2B8` | **`serverTimezone=Asia/Shanghai`** |
| Actuator 路径 | `/actuator/beans` 等 | 移除了部分端点 |
| 日志 | `logging.file.path` | 路径规则不变 |

### 本项目配置示例

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/example_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    driver-class-name: com.mysql.cj.jdbc.Driver  # 必须显式声明
```

---

## 9. 虚拟线程与 AOT 编译

| 特性 | Spring Boot 2.x | Spring Boot 3.x |
| :--- | :--- | :--- |
| 虚拟线程 | 不支持 | **支持** (JDK 21 + `spring.threads.virtual.enabled=true`) |
| AOT 编译 | 不支持 | **支持** (`mvn -Pnative spring-boot:build-image`) |
| 启动速度 | 秒级 | AOT 编译后**毫秒级** |
| 内存占用 | 较高 | AOT 编译后**大幅降低** |

---

## 10. 其他值得注意的变更

### 10.1 日期时间处理

```java
// Spring Boot 2.x
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

// Spring Boot 3.x 推荐
spring.jackson.date-format=yyyy-MM-dd HH:mm:ss  // 全局配置
```

### 10.2 Actuator 端点

| 移除的端点 | 替代方案 |
| :--- | :--- |
| `/actuator/beans` | 使用 DevTools |
| `/actuator/conditions` | 启动日志中查看 |
| `/actuator/configprops` | 使用 Spring Boot Admin |

### 10.3 观察性 (Observability)

| | Spring Boot 2.x | Spring Boot 3.x |
| :--- | :--- | :--- |
| 跟踪 | 依赖 Spring Cloud Sleuth | **Micrometer Tracing** |
| 指标 | Micrometer | Micrometer（不变） |
| 健康检查 | Actuator | Actuator（不变） |

---

## 总结对比表

| 对比维度 | Spring Boot 2.x | Spring Boot 3.x |
| :--- | :--- | :--- |
| **JDK 最低要求** | Java 8 | **Java 17** |
| **命名空间** | `javax.*` | **`jakarta.*`** |
| **Spring Framework** | 5.3.x | **6.x** |
| **Security 配置** | 继承 `WebSecurityConfigurerAdapter` | **`SecurityFilterChain` Bean** |
| **JWT 库 (jjwt)** | 0.9.x API | **0.12.x API** |
| **MyBatis-Plus** | `boot-starter` | **`boot3-starter`** |
| **接口文档** | SpringFox (已停更) | **Springdoc OpenAPI** |
| **虚拟线程** | 不支持 | **支持 (JDK 21+)** |
| **AOT/Native** | 不支持 | **支持** |
| **观察性追踪** | Spring Cloud Sleuth | **Micrometer Tracing** |
| **Tomcat 版本** | 9.x (Servlet 4) | **10.x (Servlet 5)** |
| **Java EE → Jakarta** | javax.persistence | **jakarta.persistence** |
| **Redis 客户端** | Jedis / Lettuce | Lettuce (默认) |

---

## 迁移检查清单

- [ ] JDK 升级到 17+
- [ ] 所有 `javax.*` 替换为 `jakarta.*`
- [ ] Spring Security 配置改用 `SecurityFilterChain` Bean
- [ ] jjwt 版本升级到 0.12.x 并调整 API 调用
- [ ] MyBatis-Plus 切换到 `boot3-starter`
- [ ] 分页插件切换到 `PaginationInnerInterceptor`
- [ ] 接口文档切换到 Springdoc OpenAPI
- [ ] 数据库驱动显式声明 `driver-class-name`
- [ ] 检查所有第三方依赖是否兼容 Jakarta EE
- [ ] 测试环境完整回归测试

---

> 文档生成日期：2026-06-24  
> 参考项目：microservice-platform (Spring Boot 3.2.5)
