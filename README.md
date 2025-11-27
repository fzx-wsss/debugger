# debugger 项目说明文档

## 项目简介

debugger 是一个轻量级的远程调试工具，用于在分布式环境中远程调用和调试 Spring Bean 方法。该项目通过动态代理技术拦截 Spring Bean 的方法调用，将调用信息序列化后通过 HTTP 协议发送到远程服务器执行，再将执行结果返回给调用方。

## 主要功能

- **动态代理拦截**：自动为标记的 Spring Bean 生成动态代理
- **远程方法调用**：将方法调用转发至远程服务器执行
- **灵活的标记机制**：支持通过注解、类名和 Bean 名称进行标记
- **安全认证**：支持密码认证确保安全性

## 核心组件

### 1. 配置类

- **debuggerConfig**：负责管理 debugger 的所有配置信息

### 2. 代理生成与方法拦截

- **debuggerBeanPostProcessor**：Spring Bean 后置处理器，负责为需要代理的 Bean 生成动态代理
- **debuggerInvocationHandler**：方法调用处理器，拦截方法调用并转发至远程服务器
- **debugger 注解**：用于标记需要被代理的 Spring Bean

### 3. HTTP 接口与数据模型

- **debuggerController**：处理远程调试请求的 HTTP 接口控制器
- **DebugRequest**：封装方法调用的请求对象
- **DebugResponse**：封装方法调用结果的响应对象

## 工作原理
### 本地端-服务端交互流程

```
+------------------+                     +-------------------+
|  本地端应用       |                     |   服务端应用       |
|  (Local Mode)   |                     |   (Server Mode)   |
+------------------+                     +-------------------+
        |                                       |
        | 1. 方法调用触发动态代理                 |
        |                                       |
        | 2. 拦截方法调用，封装DebugRequest      |
        |                                       |
        | 3. 序列化并通过HTTP发送请求            |
        |-------------------------------------->| 4. 接收HTTP请求
        |                                       |
        |                                       | 5. 反序列化DebugRequest
        |                                       |
        |                                       | 6. 验证密码
        |                                       |
        |                                       | 7. 查找并调用目标方法
        |                                       |
        |                                       | 8. 封装DebugResponse
        |                                       |
        | 9. 接收HTTP响应                       | 9. 序列化并返回响应
        |<--------------------------------------|
        |                                       |
        | 10. 反序列化DebugResponse             |
        |                                       |
        | 11. 返回结果或抛出异常                |
        v                                       v
```

### 本地端模式（local）详细说明

**核心流程**：
1. Spring 容器启动时，`debuggerBeanPostProcessor` 会检查所有初始化后的 Bean
2. 根据配置或注解，识别需要被代理的 Bean
3. 为识别到的 Bean 生成动态代理，代理类包含原始 Bean 的所有方法
4. 当代理 Bean 的方法被调用时，`debuggerInvocationHandler` 会拦截方法调用
5. 将调用信息（Bean 名称、方法名、参数等）封装为 `DebugRequest` 对象
6. 序列化 `DebugRequest` 对象并通过 HTTP POST 请求发送到远程服务器
7. 接收远程服务器返回的 `DebugResponse` 对象
8. 根据响应内容返回结果或抛出异常


**重要提示**：
- 必须配置 `wsss.debugger.proxy.url` 指向远程服务端
- 必须配置 `wsss.debugger.proxy.password` 且不为空
- 必须配置 `wsss.debugger.proxy.enable` 为 true 以启用代理功能
- 必须配置 `wsss.debugger.mode` 为 local，以启用代理功能

- 需要通过以下配置代理目标，或使用@Debugger注解在类上标识代理对象

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `wsss.debugger.class.names` | List<String> | 空列表 | 需要被代理的类名列表，支持全限定名。 |
| `wsss.debugger.bean.names` | Set<String> | 空集合 | 需要被代理的 Spring Bean 名称列表。 |


### 服务端模式（server）详细说明

**核心流程**：
1. `debuggerController` 接收来自客户端的 HTTP POST 请求
2. 反序列化请求数据为 `DebugRequest` 对象
3. 验证请求中的密码是否与配置匹配
4. 根据请求中的 Bean 名称从 Spring 容器获取目标 Bean
5. 根据方法名和参数查找匹配的方法
6. 调用目标方法并记录执行时间
7. 将执行结果或异常信息封装为 `DebugResponse` 对象
8. 序列化 `DebugResponse` 对象并返回给客户端


**重要提示**：
- 必须配置 `wsss.debugger.proxy.password` 且不为空
- 默认自动启用（matchIfMissing = true）
- 不会生成任何代理类，不会对原有程序或性能有任何影响
- 仅会增加http接口，用以接收http请求


## 配置选项

### 1. 基本配置

| 配置项 | 类型 | 默认值 | 说明                                                                              |
|--------|------|--------|---------------------------------------------------------------------------------|
| `wsss.debugger.mode` | String | server | 运行模式，可选值：local、server。<br/>local 模式下，方法调用会被转发至远程；<br/>server 模式下，接收并处理远程方法调用请求。 |
| `wsss.debugger.proxy.enable` | Boolean | false | 是否启用代理功能。<br/>仅local模式生效。<br/>server模式下引入本包就会生效                                           |

### 2. 认证与连接配置

| 配置项 | 类型 | 默认值 | 说明                                   |
|--------|------|--------|--------------------------------------|
| `wsss.debugger.proxy.password` | String | 无 | 远程调用的认证密码，必须设置且不为空。 local和server需要相同 |
| `wsss.debugger.proxy.url` | String | 无 | 远程服务器 URL，仅在 local 模式下需要配置。          |

### 3. 代理目标配置

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `wsss.debugger.class.names` | List<String> | 空列表 | 需要被代理的类名列表，支持全限定名。 |
| `wsss.debugger.bean.names` | Set<String> | 空集合 | 需要被代理的 Spring Bean 名称列表。 |




## 使用方法

### 1. 添加依赖

将项目打包为 Jar 并添加到您的 Spring Boot 项目中。

### 2. 客户端配置

```yaml
wsss:
  debugger:
    mode: local
    proxy:
      enable: true
      password: your_secure_password
      url: http://remote-server:port/debugger/invoke
    # 可选配置，指定需要被代理的类或Bean
    class.names:
      - com.example.YourServiceClass
    bean.names:
      - yourServiceBeanName
```

### 3. 服务端配置

```yaml
wsss:
  debugger:
    proxy:
      password: your_secure_password
```

### 4. 使用注解标记

除了通过配置文件指定需要代理的类或 Bean 外，还可以使用 `@debugger` 注解直接标记类：

```java
import com.wsss.debugger.annotation.debugger;

@debugger
@Service
public class YourService {
    // 方法实现...
}
```

## 注意事项

1. **密码保护**：请确保设置强密码并妥善保管，防止未授权访问
2. **谨慎启用**：建议仅在开发或调试环境中启用，生产环境中应禁用
3. **访问控制**：建议在生产环境中配合防火墙或网络隔离策略使用
4. **HTTPS**：生产环境使用时，建议配置 HTTPS 以加密传输数据
5. **代码同步**：因涉及到数据类的序列化，以及尽可能复现远端问题，本地与远端的代码应保持一致

## 性能影响

启用代理后，被代理的 Bean 方法调用会产生额外开销：

- 序列化/反序列化开销
- 网络传输延迟
- 远程服务器处理时间

建议仅对需要远程访问的 Bean 启用代理，以减少对系统整体性能的影响。
