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

# 禁用hystrix
feign:
  hystrix:
    enabled: false

# 设置隔离方式为SEMAPHORE
hystrix:
  command:
    default:
      execution:
        isolation:
          strategy: SEMAPHORE
```

在某些情况下，你可能需要定制你的客户端，而不是像上面那样通过注册的方式来调用。在这种情况下，你可以通过使用[Feign Builder API](https://github.com/OpenFeign/feign/#basics)来构建自定义的Fegin。

下面给出一个例子，使用同一个接口，创建两个`Feign Clients`,但是为每个`Feign`配置独立的请求拦截。

```java
@Import(FeignClientsConfiguration.class)
class FooController {

	private FooClient fooClient;

	private FooClient adminClient;

    	@Autowired
	public FooController(
			Decoder decoder, Encoder encoder, Client client) {
		this.fooClient = Feign.builder().client(client)
				.encoder(encoder)
				.decoder(decoder)
				.requestInterceptor(new BasicAuthRequestInterceptor("user", "user"))
				.target(FooClient.class, "http://PROD-SVC");
		this.adminClient = Feign.builder().client(client)
				.encoder(encoder)
				.decoder(decoder)
				.requestInterceptor(new BasicAuthRequestInterceptor("admin", "admin"))
				.target(FooClient.class, "http://PROD-SVC");
    }
}
```

> 在上面的例子中，`FeignClientsConfiguration`是一个`Spring Cloud Netflix`提供的默认配置

> "PROD-SVC"是调用的远程服务名称，也就是`spring.application.name`。

#### Feign Hystrix 支持

如果你引入了`Hystrix`依赖，而且开启`feign.hystrix.enabled=true`，`Feign`将通过`circuit breaker`(断路器)包装所有的方法。还可以返回
一个可用的`com.netflix.hystrix.HystrixCommand`对象。它允许你使用`reactive patterns`(流式编程/响应式编程)的模式进行API调用。

> **_注意：_** 在 Spring Cloud Dalston release之前的版本是默认包装，在 Spring Cloud Dalston release中改变为选择添加的方式。

> 调用`.toObservable()` 或 `.observe()`。
>   - [observe()](http://netflix.github.io/Hystrix/javadoc/com/netflix/hystrix/HystrixCommand.html#observe()/)返回一个’hot’`Observable`它会立即订阅底层的`Observable`，即使`Observable`通过ReplaySubject进行过滤，在您有机会订阅之前，您不会丢失它所发出的任何项目。
>   - [toObservable()](http://netflix.github.io/Hystrix/javadoc/com/netflix/hystrix/HystrixCommand.html#toObservable%28%29/)返回一个’cold’`Observable`,不会立即订阅底层的`Observable`，直到你订阅`Observable`才会开始发布结果 。
 
> 调用[.queue()](http://netflix.github.io/Hystrix/javadoc/com/netflix/hystrix/HystrixCommand.html#queue%28%29/)，进行异步编程。

使用异步编程模式

```code
Future<String> fs = new CommandHelloWorld("World").queue();
String s = fs.get();
```

使用订阅模式
```code
Observable<String> ho = new CommandHelloWorld("World").observe();
// or Observable<String> co = new CommandHelloWorld("World").toObservable()

ho.subscribe(new Action1<String>() {

    @Override
    public void call(String s) {
         // value emitted here
    }

});
```
简化代码，JAVA lambdas/closures 语法
```code
fWorld.subscribe((v) -> {
    System.out.println("onNext: " + v);
})

// - 可以包含错误处理

fWorld.subscribe((v) -> {
    System.out.println("onNext: " + v);
}, (exception) -> {
    exception.printStackTrace();
})
```



#### Feign Hystrix Fallbacks
Hystrix支持`fallback`(备选方案的意思):在电路打开，或者执行出现错误，Hystrix会进行降级处理，执行默认的`fallback`选项代码。减少错误的传播。
只需要在`@FeignClient(name = "hello", fallback = HystrixClientFallback.class)`实现`HystrixClientFallback`该类即可。

如果你想访问导致`fallback`触发的原因，你可以使用`@FeignClient`的`fallbackFactory`属性.

```java
@FeignClient(name = "hello", fallbackFactory = HystrixClientFallbackFactory.class)
protected interface HystrixClient {
	@RequestMapping(method = RequestMethod.GET, value = "/hello")
	Hello iFailSometimes();
}

@Component
static class HystrixClientFallbackFactory implements FallbackFactory<HystrixClient> {
	@Override
	public HystrixClient create(Throwable cause) {
		return new HystrixClient() {
			@Override
			public Hello iFailSometimes() {
				return new Hello("fallback; reason was: " + cause.getMessage());
			}
		};
	}
}
```

> 注意：在Feign中执行回退以及Hystrix回退的工作方式存在局限性，当前返回`com.netflix.hystrix.HystrixCommand`和`rx.Observable`的方法目前不支持回退。


#### Feign and @Primary
当我们使用Feign进行Hystrix fallback时，可能会存在`ApplicationContext`中有多个相同类型的bean。这将导致@Autowried无法正常工作
没有一个合适的bean(`isn’t exactly one bean 错误`)，或者没有一个`primary bean`。

为了解决这个问题，Spring Cloud Netflix将所有Feign实例标记为`@Primary`，因此Spring Framework将知道要注入哪个bean。
在某些情况下，你可能并不需要这样做，通过设置可以关闭此行为。

```java
@FeignClient(name = "hello", primary = false)
public interface HelloClient {
	// methods here
}
```

#### Feign继承支持

Feign通过单继承接口支持样板apis。通过继承接口，将常用的基本操作分组，方便操作。


##### UserService.java.
```java
public interface UserService {

    @RequestMapping(method = RequestMethod.GET, value ="/users/{id}")
    User getUser(@PathVariable("id") long id);
}
```
##### UserResource.java.
```java
@RestController
public class UserResource implements UserService {

}
```

##### UserClient.java. 
```java


@FeignClient("users")
public interface UserClient extends UserService {

}
```
> 通常不建议在服务器和客户端之间共享接口。它引入了紧耦合，并且实际上并不适用于当前形式的Spring MVC（方法参数映射不被继承）。

#### Feign请求/响应压缩

开启请求相应进行`GZIP`压缩：
```yaml
feign.compression.request.enabled=true
feign.compression.response.enabled=true
```
压缩还支持类似web请求头的设置：
```yaml
feign.compression.request.enabled=true
feign.compression.request.mime-types=text/xml,application/xml,application/json
feign.compression.request.min-request-size=2048
```
通过这些属性可以让你对压缩媒体类型和最小请求阈值长度有选择性。

#### Feign 日志支持

可以为每个Feign客户端创建一个日志记录器。默认情况下，记录器的名称是用于创建Feign客户端接口的完整类名。Feign日志记录仅响应DEBUG级别。

application.yml
```yaml
logging.level.project.user.UserClient: DEBUG
```

你可以为每个客户端配置`Logger.Level`来告诉Fegin记录多少。
    
   - `NONE`,无记录(默认设置)。
   - `BASIC`,只记录请求方法和URL以及响应状态码和执行时间。
   - `HEADERS`,记录基本信息以及请求响应头（request headers/response headers）。
   - `FULL`,记录request和response的headers,body,和一些metadata元数据。
   
示例设置日志追踪等级，
```java
@Configuration
public class FooConfiguration {
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
```


- - -


## 路由与过滤：Zuul

路由是微服务架构的一部分。`/`可能映射到你的web应用程序，`/api/users`映射到你的用户服务，`/api/shop`映射到你的购物服务。
[Zuul](https://github.com/Netflix/zuul)是Netflix的基于JVM的路由器和服务器端负载均衡器。

Netflix 使用 Zuul做如下功能：
  
  - 认证
  - 洞察
  - 压力测试
  - (Candary Testing)金丝雀测试 
  - 动态路由
  - 服务迁移
  - 负载脱落
  - 安全
  - 静态响应处理
  - 主动 / 主动流量管理
  
Zuul的规则引擎允许任何基于JVM平台的语言编写规则和过滤器，如：内置的JAVA和Groovy。

> 注意： 配置属性`zuul.max.host.connections`现在已经被两个新的属性替换，`zuul.host.maxTotalConnections` 和 `zuul.host.maxPerRouteConnections`
> 它们默认为200和20。

> 所有路由的默认Hystrix隔离模式`(ExecutionIsolationStrategy)`是SEMAPHORE,可以更改此设置`zuul.ribbonIsolationStrategy`将隔离模式切换为`Thread`。

如果想要使用Zuul，首先要添加依赖：
```xml
<!--网关-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-zuul</artifactId>
        </dependency>
```
在应用程序中注入依赖：
```java
/**
 * @EnableEurekaClient 连接注册中心
 * @EnableZuulProxy 开启网关路由功能
 */
@EnableEurekaClient
@EnableZuulProxy
@SpringBootApplication
public class ZuulServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZuulServiceApplication.class, args);
    }
}

```

#### 嵌入式的Zuul反向代理
Spring Cloud提供一个嵌入式的Zuul代理来简化开发，在大多数情况下，我们都需要通过使用UI代理来调用一个或多个的后端服务，然后通过Zuul我们可以减少对于UI的依赖。
此功能对于使用用户界面代理其所需要的后端服务非常有用。避免需要为所有后端独立管理CORS（跨域问题）和身份验证问题。

> 注意：Zuul启动后不包含服务发现的功能，因此对基于服务ID来进行路由的形式，你需要提供一个客户端。例如`@EnableEurekaClient`。

通过设置`zuul.ignored-services`来匹配需要忽略的服务。

application.yml

```yaml
 zuul:
  ignoredServices: '*'
  routes:
    users: /myusers/**
```
这种情况下，除了“users”服务被路由转发外，其他的服务都会忽略。

当调用"/myusers"请求时，会自动转发到"users"服务。（例如："/myusers/101" 会转发成 "/101"）。


如果想对路由进行更细粒度的控制，可以为路由指定`path`以及独立的服务ID(`service id`)


application.yml
```yaml
 zuul:
  routes:
    users:
      path: /myusers/**
      serviceId: users_service
```

这意味着对“/myusers”的http调用将转发到“users_service”服务.

路由必须有一个`path`，`path`支持ant-style模式匹配，"/myusers/\*" 仅仅匹配一个等级,"/myusers/**" 匹配多个级别.

后端的服务位置可以被指定为“serviceId”（用于来自发现的服务）或“url”（用于物理位置），例:
application.yml
```yaml
 zuul:
  routes:
    users:
      path: /myusers/**
      url: http://example.com/users_service
```

