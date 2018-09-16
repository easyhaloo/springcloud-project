package com.example.userservice.config.monggo;

import com.google.common.collect.Collections2;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import java.util.ArrayList;

import java.util.List;

@Configuration
public class MongoConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoConfig.class);

    @Autowired
    private MongoProperties mongoProperties;
    /**
     * mongodb数据库连接池
     * @return
     */
    @Bean
    MongoDbFactory mongoDbFactory() {
        //客户端配置（连接数，副本集群验证）
        MongoClientOptions.Builder builer = new MongoClientOptions.Builder();
        builer.connectionsPerHost(mongoProperties.getConnectionsPerHost());
        builer.minConnectionsPerHost(mongoProperties.getMinConnectionsPerHost());
        if (null != mongoProperties.getReplicaSet()) {
            builer.requiredReplicaSetName(mongoProperties.getReplicaSet());
        }

        MongoClientOptions mongoClientOptions = builer.build();

        List<ServerAddress> serverAddresses = new ArrayList<>();

        List<Integer> ports = mongoProperties.getPorts().subList(0, mongoProperties.getPorts().size());
        ServerAddress serverAddress;
        for (String host : mongoProperties.getHosts()) {
            Integer index = mongoProperties.getHosts().indexOf(host);
            Integer port = ports.get(index);
            serverAddress = new ServerAddress(host, port);
            serverAddresses.add(serverAddress);
        }

        LOGGER.info("serverAddresses : {}", serverAddresses);

        //连接认证

        List<MongoCredential> mongoCredentialList = new ArrayList<>();
        if (mongoProperties.getUsername() != null) {
            mongoCredentialList.add(MongoCredential.createScramSha1Credential(
                    mongoProperties.getUsername(),
                    mongoProperties.getAuthenticationDatabase() != null ? mongoProperties.getAuthenticationDatabase() : mongoProperties.getDatabase(),
                    mongoProperties.getPassword().toCharArray()));
        }


        //创建客户端和Factory
        MongoClient mongoClient;

        if (null == mongoCredentialList || mongoCredentialList.size() == 0) {
            mongoClient = new MongoClient(serverAddresses, mongoClientOptions);
        } else {
            mongoClient = new MongoClient(serverAddresses, mongoCredentialList, mongoClientOptions);
            LOGGER.info("mongoCredentials: {}", mongoCredentialList.toString());
        }

        MongoDbFactory factory = new SimpleMongoDbFactory(mongoClient, mongoProperties.getDatabase());
        return factory;
    }
}
