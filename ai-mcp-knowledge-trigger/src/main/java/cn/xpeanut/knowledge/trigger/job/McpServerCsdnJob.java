package cn.xpeanut.knowledge.trigger.job;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class McpServerCsdnJob {

    @Resource
    private ChatClient chatClient;

    @Scheduled(cron = "0 0 * * * ?")
    public void exec() {
        // 检查当前时间是否在允许执行的时间范围内（8点到23点之间）
        int currentHour = java.time.LocalDateTime.now().getHour();
//        if (currentHour >= 23 || currentHour < 8) {
//            log.info("当前时间 {}点 不在任务执行时间范围内，跳过执行", currentHour);
//            return;
//        }
        try {
            String userInput = """
                    我需要你帮我生成一篇面向编程小白的技术栈学习文章，要求如下：
                    
                    1. 文章围绕某一种具体技术栈展开（例如 JVM、MyBatis、Spring Cloud、Docker、Jenkins、Hadoop、Spark、Elasticsearch、Flink、RabbitMQ、Flyway、Prometheus、OAuth2 等），目标是帮助读者在较短时间内完成从“是什么、能做什么”到“能自己上手开发”的完整学习闭环。
                       可选技术栈如下：
                          核心语言与平台: Java SE (8/11/17), Jakarta EE (Java EE), JVM
                          构建工具: Maven, Gradle, Ant
                          Web框架: Spring Boot, Spring MVC, Spring WebFlux, Jakarta EE, Micronaut, Quarkus, Play Framework, Struts (Legacy)
                          数据库与ORM: Hibernate, MyBatis, JPA, Spring Data JDBC, HikariCP, C3P0, Flyway, Liquibase
                          测试框架: JUnit 5, TestNG, Mockito, PowerMock, AssertJ, Selenium, Cucumber
                          微服务与云原生: Spring Cloud, Netflix OSS (Eureka, Zuul), Consul, gRPC, Apache Thrift, Kubernetes Client, OpenFeign, Resilience4j
                          安全框架: Spring Security, Apache Shiro, JWT, OAuth2, Keycloak, Bouncy Castle
                          消息队列: Kafka, RabbitMQ, ActiveMQ, JMS, Apache Pulsar, Redis Pub/Sub
                          缓存技术: Redis, Ehcache, Caffeine, Hazelcast, Memcached, Spring Cache
                          日志框架: Log4j2, Logback, SLF4J, Tinylog
                          监控与运维: Prometheus, Grafana, Micrometer, ELK Stack, New Relic, Jaeger, Zipkin
                          模板引擎: Thymeleaf, FreeMarker, Velocity, JSP/JSTL
                          REST与API工具: Swagger/OpenAPI, Spring HATEOAS, Jersey, RESTEasy, Retrofit
                          序列化: Jackson, Gson, Protobuf, Avro
                          CI/CD工具: Jenkins, GitLab CI, GitHub Actions, Docker, Kubernetes
                          大数据处理: Hadoop, Spark, Flink, Cassandra, Elasticsearch
                          版本控制: Git, SVN
                          工具库: Apache Commons, Guava, Lombok, MapStruct, JSch, POI
                          其他: JUnit Pioneer, Dubbo, R2DBC, WebSocket
                    2. 讲解方式要分阶段、由浅入深，整体结构至少包含以下五个部分：
                       ① 技术栈用途介绍：用通俗的语言说明该技术栈能解决哪些问题、典型应用场景是什么，并结合一个贴近实际的业务场景帮助读者建立直观认识。
                       ② 环境准备与安装配置：详细说明如何下载、安装和基础配置，包括主要依赖、常用开发工具或运行环境的关键步骤，以及容易踩的坑和排查思路。
                       ③ 入门实践（快速上手 Demo）：从零开始搭建一个最小可运行的示例工程，逐步讲解核心概念与常用 API，用代码示例说明“如何创建项目、如何运行、如何完成一个简单业务功能”。
                       ④ 进阶与原理（高级用法或深入机制）：在入门 Demo 的基础上，进一步介绍该技术栈的高级特性、最佳实践或底层原理（例如性能优化、扩展机制、与其他组件集成、常见架构设计等），让读者对其工作机制有更深理解。
                       ⑤ 总结与评估：从优点、局限性、适用场景、与其他同类技术对比等角度，对该技术栈做一个系统的总结，并给出后续学习建议或延伸阅读方向。
                    3. 行文风格要兼顾“系统性”和“可操作性”：既要结构清晰、层次分明，又要在关键步骤给出足够详细的说明和代码示例，让没有基础的读者也能一步步跟着操作完成。
                    4. 全文内容要以中文呈现，避免过多堆砌术语，对必要的专业概念要配合类比或图景化描述，帮助读者快速建立心智模型。
                    
                    根据以上要求，请直接提供：文章标题（需要含带具体技术栈名称或核心技术点）、完整文章内容、文章标签（多个用英文逗号隔开）、约 100 字的文章简述，不要输出与正文无关的额外说明。
                    
                    请将整体内容整理成适合直接发布到 CSDN 的格式。
                    
                    将以上内容发布文章到CSDN
                    """;

            log.info("执行结果:{} {}", userInput, chatClient.prompt(userInput).call().content());
        } catch (Exception e) {
            log.error("定时任务，定时发送CSDN文章任务失败", e);
        }
    }

}
