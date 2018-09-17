package com.example.userservice.config.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Component;

/**
 * redis集群必须redis 3.0以上版本的支持
 * redis集群
 */
@Configuration
@Component
    public class JedisClusterConfig {


    private static final Logger LOGGER = LoggerFactory.getLogger(JedisClusterConfig.class);

//    @Autowired
//    private RedisClusterProperty redisClusterProperty;


    @Autowired
    private RedisProperties redisProperties;

//    /**
//     * Spring Data Redis 1.7 支持redis集群
//     * jedis集群配置
//     *
//     * @return
//     */
//    @Bean
//    @Primary
//    public RedisConnectionFactory connectionFactory() {
//
//        RedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory(
//                new RedisClusterConfiguration(redisProperties.getCluster().getNodes()));
//
//        return redisConnectionFactory;
//    }






//    @Bean
//    public JedisCluster getJedisCluster() {
//        List<String> servers = redisClusterProperty.getNodes();
//        Set<HostAndPort> nodes = new HashSet<>();
//        Integer port = null;
//        try {
//            for (String server : servers) {
//                String[] ipPortPair = server.split(":");
//                port = Integer.parseInt(ipPortPair[1]);
//                nodes.add(new HostAndPort(ipPortPair[0], port));
//            }
//        } catch (NumberFormatException e) {
//            LOGGER.error("paser port :{} error", port);
//        }
//        JedisCluster jedisCluster = new JedisCluster(nodes, redisClusterProperty.getTimeout(), redisClusterProperty.getMaxAttempts());
//        return jedisCluster;
//    }
}
