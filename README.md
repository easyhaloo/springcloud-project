## 注册中心集群配置

注册中心配置，主要由三个节点做负载均衡，分别为：

eureka-register-master: 对应端口： 8880

eureka-register-salve1: 对应端口： 8881

eureka-register-salve2: 对应端口： 8882


## zuul网关模块
zuul-service: 对应端口：8884


user-service:对应端口 8885
dept-service:对应端口 8886


@EnableEurekaServer
> 作为Eureka的服务注册中心

@EnableEurekaClient与@EnableDiscoveryClient
> @EnableDiscoveryClient是SpringCloud提供的通用服务发现标识，@EnableEurekaClient仅仅只支持Eureka作为注册中心时才有效。


application.yml配置

`eureka.instance.hostname`改选想

`eureka.client.serviceUrl.defaultZone`是一个默认的注册中心地址。配置该选项后，可以在服务中心进行注册。

`eureka.client.fetch-registry`检索服务选项，当设置为True(默认值)时，会进行服务检索,注册中心不负责检索服务。

`eureka.client.register-with-eureka`服务注册中心也会将自己作为客户端来尝试注册自己,为true（默认）时自动生效
 
所以一般情况下，当我们设置服务为注册中心时，需要关闭`eureka.client.fetch-registry`与`eureka.client.register-with-eureka`

在做注册中心集群的时候，`register-with-eureka`必须打开，因为需要进行相互注册，不然副本无法可用。

```yaml
eureka:
  instance:
    hostname: register-master #对应主机的ID，可通过hosts文件修改别名
  client:
    #通过设置fetch-registry与register-with-eureka 表明自己是一个eureka服务    fetch-registry: false
    register-with-eureka: false
    #注册register-salve1，register-salve2做注册中心集群
    service-url:
      defaultZone: http://register-salve1:8881/eureka/,http://register-salve2:8882/eureka/
```

`spring.application.name`应用名称，注册中心会将该名称作为服务名进行记录。其他服务调用一般通过服务名进行服务的调用。

```yaml
spring:
  application:
    name: register-center
```

Eureka中的`instance`与`client`

`instance`它会注册自己，使自己成为一个Eureka实例。在默认情况下它会设置为`spring.application.name`。所以需要确保
`spring.application.name`的存在

`client`即它可以查询注册表以找到其他服务。
`eureka.client.enabled`的设置可以用来控制服务发现。通过设置`false`可以禁用服务发现的功能。

Eureka中的也支持HTTPS的访问。

通过设置`eureka.instance.[nonSecurePortEnabled,securePortEnabled]=[false,true]`，SpringCloud `DiscoveryClient`将返回带有https的URL。

Eureka的健康检查

默认情况下Eureka使用客户端心跳来检测客户端是否启动。除非另有说明，否则Discovery Client不会根据Spring Boot Actuator传播应用程序的当前运行状况检查状态。
这意味着，当在Discovery Client注册成功后，Eureka将始终宣布该服务处在'UP'状态。即在线状态。通过启用Eureka的健康检查可以改变此行为，可以是应用程序的状态传播到Eureka.
Eureka注册中心可以实时监控Discovery Client的状态，确保其他应用程序不会调用处于非‘UP’状态的服务。

```yaml
eureka:
  client:
    healthcheck:
      enabled: true
```
`eureka.client.healthcheck.enabled=true`只能设置在application.yml中。

通过实现`com.netflix.appinfo.HealthCheckHandler`接口，可以自定义健康检查。

### Eureka客户端
使用Eureka客户端必须添加如下依赖：

```xml
 <!--eureka客户端-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-eureka</artifactId>
    </dependency>
```

@EurekaClient


通过在应用程序上注册@EurekaClient,便可以从Eureka服务器上发现服务实例。



```java
@EnableEurekaClient

@SpringBootApplication
public class ZuulServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZuulServiceApplication.class, args);
    }
}

```

不要在EurekaClient上使用@PostConstruct，或@Scheduled（或者任何ApplicationContext有可能为启动的地方。）

EurelaClient默认使用Jersey进行HTTP通信，如果您希望避免来自Jersey的依赖项，您可以将其从依赖项中排除。SpringCLoud
将自动配置基于Spring RestTemplate.的传输方式。

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-eureka</artifactId>
    <exclusions>
        <exclusion>
            <groupId>com.sun.jersey</groupId>
            <artifactId>jersey-client</artifactId>
        </exclusion>
        <exclusion>
            <groupId>com.sun.jersey</groupId>
            <artifactId>jersey-core</artifactId>
        </exclusion>
        <exclusion>
            <groupId>com.sun.jersey.contribs</groupId>
            <artifactId>jersey-apache-client4</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

服务注册缓慢

由于服务从实例化后，还要到注册中心进行定期的心跳检查，默认的持续时间为30s。
在实例后，服务器与客户端都会在本地缓存元数据，只有当客户端与服务器的元数据相同时，客户端才可能发现服务。这个过程大概需要三个心跳时间来进行。
通过设置`eureka.instance.leaseRenewalIntervalInSeconds`来更改期限，可以加快客户端连接到其他服务的过程。


### Eureka服务器

使用Eureka服务器需要使用如下以来：

```xml
 <!--eureka server -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-eureka-server</artifactId>
    </dependency>
```

然后在服务启动类上添加@EnableEurekaServer注解，作为Eureka服务器使用

```java

@EnableEurekaServer
@SpringBootApplication
public class EurekaRegisterMasterApplication {

public static void main(String[] args) {
    SpringApplication.run(EurekaRegisterMasterApplication.class, args);
}
}
```


Eureka服务器没有后端存储。但是所有注册的服务都必须向注册中心发送心跳保证服务可靠以及注册更新。都是在内存中进行的。客户端具有eureka注册的内存缓存（因此对于）

    

Eureka有限通过IP地址进行发布服务，而不是主机名。将`eureka.instance.preferIpAddress`设置为`true`,当进行服务注册的时候，它将使用IP地址，然不是其主机名称。



##  断路器Hystrix客户端

1. Hystrix有如下功能：

    - 通过第三方客户端库访问（通常通过网络）依赖关系，以防止和控制延迟和故障。
    - 在复杂的分布式系统中停止级联故障。
    - 快速失败并迅速恢复。
    - 在可能的情况下，后退并优雅地降级。
    - 实现近实时监控，警报和操作控制。

2. Hystrix 的工作内容：

    - 防止任何单个依赖项用尽所有容器（例如Tomcat）用户线程。
    - 脱落负载和快速失败代替排队
    - 使用隔离技术（例如隔板，甬道和断路器模式）来限制任何一个依赖项的影响。
    - 通过近实时指标，监控和警报优化服务发现时间。
    - 通过更改配置，降低延迟传播和恢复时间，并在Hystrix的大多数方面支持动态属性更改，它允许您使用低延迟反馈循环进行实时操作修改。
    - 防止整个依赖关系客户端执行中的故障，而不仅仅是网络流量。


> 在微服务的架构下，服务之间的依赖关系错综复杂，当一个服务调用失败后，它会影响到与此服务相关的全部依赖，这极大程度的降低了服务的可靠性与稳定性。
使用Hystrix可以尽量降低服务的级联故障，对服务进行隔离。当一个服务调用失败后，Hystrix会帮我们进行重试机制，当重试到达一定的阈值后，Hystrix
会调用它的备选方案（fallback），而不至于让错误扩散下去。提升了系统的容错性。

![image](https://springcloud.cc/images/HystrixFallback.png)


#### 如何使用Hystrix？

先要添加Hystrix的依赖：

```xml
<!--熔断器-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-hystrix</artifactId>
        </dependency>
```

然后在应用程序的入口，添加@EnabledHystrix，注意在Ribbon版本需要手动开启，Fegin版本是默认与Hystrix整合的。
但是Fegin需要通过修改配置生效：`feign.hystrix.enabled:true`

在使Fegin版本的时候需要使用
`@FeignClient(fallback = DeptService.DeptFallBack.class)`注解来进行降级处理。如果调用服务确认需要熔断，则会
调用`DeptService.DeptFallBack.class`里面的方法来进行处理。`DeptService.DeptFallBack.class`通过实现远程服务调用的接口来做熔断处理。

例子如下：
```java
@FeignClient(value = "dept-service",path = "/dept",fallback = DeptService.DeptFallBack.class)
@Component
public interface DeptService {
    @GetMapping("/getDepts")
    List<Dept> getDeptList();

    @GetMapping("/getDeptById/{id}")
    Dept getDeptById(@PathVariable("id") String id);

    @PostMapping("/save")
    String save(Dept dept);

    @PutMapping("/update/{id}")
    String update(@PathVariable("id") String id,Dept dept);

    @DeleteMapping("/delete/{id}")
    String delete(@PathVariable("id") String id);

    /**降級处理类
     * 需要@Component注解，方便Spring进行扫描，并实例化对象
     */
    @Component
    class DeptFallBack implements DeptService{

        @Override
        public List<Dept> getDeptList() {
            return null;
        }

        @Override
        public Dept getDeptById(String id) {
            return null;
        }

        @Override
        public String save(Dept dept) {
            return "存储失败 dept:"+dept;
        }

        @Override
        public String update(String id, Dept dept) {
            return "更新失败,id :"+id+" ,dept:"+dept;
        }

        @Override
        public String delete(String id) {
            return "删除失败 ,id: "+id;
        }
    }
}
```

需要注意的是：`DeptFallBack`，需要调用`@Component`注解，这样才能将实例对象注册到spring容器中。
                   
Hystrix的熔断策略（Isolation Strategy）分为两种：
 
 - — Thread模式。当使用thread进行隔离的时候，Hystrix命令会通过从线程池分离一个单独的线程来执行。 Hystrix会暂停这个持有请求的线程，直到下游服务器收到响应，或者发生超时。
    
    - 上游就是，需要调用你的服务的接口的服务器
    - 下游就是，你需要去调接口的服务器
    
 - — SEMAPHORE(信号量)模式，使用SEMAPHORE隔离时，会在请求线程上执行Hystrix命令.仅在从下游服务器收到响应后才检测超时.因此，如果您将Zuul / Hystrix配置为超时5秒，并且您的服务需要30秒才能完成.只有在30秒后，您的客户才会收到超时通知 - 即使服务响应成功。
 
   
#### 声明式Rest客户端：Fegin

> Fegin是一个声明式的Web服务客户端，伪Http客户端。，直接使用注解的方式就可以进行restApi的调用。Fegin
默认集成ribbon,ribbon是一款优秀的负载均衡客户端。可以很好的控制htt和tcp的一些行为。
所以Fegin天然支持负载均衡。

添加依赖：

```xml

    <!--feign版本的服务消费提供-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-feign</artifactId>
    </dependency>


```

示例应用：
```
@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableEurekaClient
@EnableFeignClients
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

```java
@FeignClient("stores")
public interface StoreClient {
    @RequestMapping(method = RequestMethod.GET, value = "/stores")
    List<Store> getStores();

    @RequestMapping(method = RequestMethod.POST, value = "/stores/{storeId}", consumes = "application/json")
    Store update(@PathVariable("storeId") Long storeId, Store store);
}
```

必须开启`@EnableFeignClients`才可以使用`@FeginClient`的功能。@FeignClient("stores")中
"stores"代表我们需要调用的服务名，该名称对用服务注册时的`spring.application.name`属性。

当调用的服务存在多路径时，可以使用`path`来修饰路径前缀。

```
@FeignClient(value = "dept-service",path = "/dept",fallback = DeptService.DeptFallBack.class)
```

value代表的是服务名，path代表的是路径前缀修，例如：/dept/getAll,修饰/dept,类似，类层次的@RequestMapping()映射。

`fallback`可以参见上述熔断器`Hystrix`的使用。

可以通过`qualifier`属性来制定接口的别名，类似@Qualifier
还可以设置`url`属性，url必须为绝对URL地址或可解析的主机名。

在使用Fegin后，如果你的应用程序是Eureka客户端，那么它将解析Eureka服务注册表中的服务。

如果你不想使用Fegin进行远程服务，你可以通过配置文件，恢复到ribbon+restTemplate模式，在配置文件中进行如下配置：

```yaml
stores:
  ribbon:
    listOfServers: example.com,google.com
```
`listOfServers`代表需要调用的服务器列表


fegin的核心概念是指定客户端。每个fegin客户端都是组件集合的一部分，它们协同工作以按需联系远程服务器,
并且该集合具有一个名称，您可以使用@FeignClient注解将其作为应用，将其提供给程序开发人员

Spring Cloud允许你通过声明额外制定的配置类来完全控制 feign 客户端。（在FeignClientsConfiguration之上）

在这种情况下，客户端由FeignClientsConfiguration中已有的组件以及FooConfiguration中的所有组件组成（后者将覆盖前者）。

FooConfiguration不需要使用@Configuration进行中注解，否则，它将成为feign.Decoder，feign.Encoder，feign.Contract的默认来源。
如果使用@Configuration属性后，注意从所有`@ComponentScan`中排出此类。或者是将FooConfiguration放置在
@ComponentScan或@SpringBootApplication以外的包中。

示例如下：

```java
@FeignClient(name = "stores", configuration = FooConfiguration.class)
public interface StoreClient {
    //..
}



/**
*创建其中一种类型的bean并将其放在
*@FeignClient配置中允许您覆盖所描述的每个bean。
*/
@Configuration
public class FooConfiguration {
    @Bean
    public Contract feignContract() {
        return new feign.Contract.Default();
    }

    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor("user", "password");
    }
}

```

name和url属性支持占位符。
```java
@FeignClient(name = "${feign.name}", url = "${feign.url}")
public interface StoreClient {
    //..
}

```

切换feign使用的http客户端。通过设置`feign.okhttp.enabled=true`或者`feign.httpclient.enabled=true`，然后添加
OkHttp或是ApacheHttp的三方依赖，来使用OkHttpClient和ApacheHttpClient feign客户端。


Spring Cloud Netflix默认为feign（BeanType beanName：ClassName）提供以下bean：
 
 -  Decoder feignDecoder：ResponseEntityDecoder（其中包含SpringDecoder）
 -  Encoder feignEncoder：SpringEncoder
 -  Logger feignLogger：Slf4jLogger
 -  Contract feignContract：SpringMvcContract 
 -  Feign.Builder feignBuilder：HystrixFeign.Builder
 -  Client feignClient：如果Ribbon启用，则为LoadBalancerFeignClient，否则将使用默认的feign客户端。 
 
Spring Cloud Netflix 默认情况下不提供以下bean，但是仍然从应用程序上下文中查找这些类型的bean以创建Fegin Client：
 
 - Logger.Level
 - Retryer
 - ErrorDecoder
 - Request.Options
 - Collection<RequestInterceptor>
 - SetterFactory
 
 
```java

/**
*创建其中一种类型的bean并将其放在
*@FeignClient配置中允许您覆盖所描述的每个bean。
*/
@Configuration
public class FooConfiguration {
    @Bean
    public Contract feignContract() {
        return new feign.Contract.Default();
    }

    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor("user", "password");
    }
}

```

这将使用feign.Contract.Default替换SpringMvcContract，并将RequestInterceptor添加到RequestInterceptor的集合中。
 
@FeignClient也可以使用配置属性进行配置:

application.yml

```yaml
feign:
  client:
    config:
      feignName:
        connectTimeout: 5000
        readTimeout: 5000
        loggerLevel: full
        errorDecoder: com.example.SimpleErrorDecoder
        retryer: com.example.SimpleRetryer
        requestInterceptors:
          - com.example.FooRequestInterceptor
          - com.example.BarRequestInterceptor
        decode404: false
```

可以以与上述类似的方式在@EnableFeignClients属性defaultConfiguration中指定默认配置。
不同之处在于此配置将适用于所有feign client。

配置属性文件的优先级高于`@Configuration`注解的实例对象。配置文件中的值会覆盖`@Configuration`中的值。
如果要将优先级更改为`@Configuration`，则可以将`feign.client.default-to-properties`更改为false。

> 如果你需要使用ThreadLocal绑定变量，你需要将隔离策略从`Thread`替换为`SEMAPHORE`,或者禁用Hystrix


application.yml

```yaml

# To disable Hystrix in Feign
feign:
  hystrix:
    enabled: false

# To set thread isolation to SEMAPHORE
hystrix:
  command:
    default:
      execution:
        isolation:
          strategy: SEMAPHORE
```

 