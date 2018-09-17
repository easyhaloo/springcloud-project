package com.example.userservice.config.monggo;


import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * mongodb 集群连接属性配置
 */
@Component
@Validated
//@Data
//@ConfigurationProperties(prefix = "spring.data.mongodb.custom")
public class MongoProperties {


    /**
     * 连接的主机集合
     */
    @NotEmpty
    @Value("${spring.data.mongodb.custom.hosts}")
    private List<String> hosts;

    /**
     * 使用的数据库
     */
    @NotBlank
    @Value("${spring.data.mongodb.custom.database}")
    private String database;
    /**
     * 连接的端口号集合
     */
    @NotEmpty
    @Value("${spring.data.mongodb.custom.ports}")
    private List<Integer> ports;



    /**
     * mongo 连接mongo所需要使用的账号
     */
    @Value("${spring.data.mongodb.custom.username}")
    private String username;
    /**
     * mongo 连接mongo所需要使用密码
     */
    @Value("${spring.data.mongodb.custom.password}")
    private String password;

    /**
     * 集群副本所需要的名称
     */
    @Value("${spring.data.mongodb.custom.replicaSet}")
    private String replicaSet;
    /**
     * 需要认证连接的数据库
     */
    @Value("${spring.data.mongodb.custom.authenticationDatabase}")
    private String authenticationDatabase;
    /**
     *  每个主机支持的最小连接数
     */
    @Value("${spring.data.mongodb.custom.minConnectionsPerHost}")
    private Integer minConnectionsPerHost = 2;
    /**
     * 每个主机支持的最大连接数
     */
    @Value("${spring.data.mongodb.custom.connectionsPerHost}")
    private Integer connectionsPerHost = 10;

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public List<String> getHosts() {
        return hosts;
    }

    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }

    public List<Integer> getPorts() {
        return ports;
    }

    public void setPorts(List<Integer> ports) {
        this.ports = ports;
    }

    public String getReplicaSet() {
        return replicaSet;
    }

    public void setReplicaSet(String replicaSet) {
        this.replicaSet = replicaSet;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthenticationDatabase() {
        return authenticationDatabase;
    }

    public void setAuthenticationDatabase(String authenticationDatabase) {
        this.authenticationDatabase = authenticationDatabase;
    }

    public Integer getMinConnectionsPerHost() {
        return minConnectionsPerHost;
    }

    public void setMinConnectionsPerHost(Integer minConnectionsPerHost) {
        this.minConnectionsPerHost = minConnectionsPerHost;
    }

    public Integer getConnectionsPerHost() {
        return connectionsPerHost;
    }

    public void setConnectionsPerHost(Integer connectionsPerHost) {
        this.connectionsPerHost = connectionsPerHost;
    }
}
