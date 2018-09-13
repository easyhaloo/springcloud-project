package com.example.zuulservice.config;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置Swagger的资源文档
 */
@Component
@Primary
public class DocumentationConfig implements SwaggerResourcesProvider {
    @Override
    public List<SwaggerResource> get() {

        List<SwaggerResource> resources = new ArrayList<>();
        resources.add(swaggerResource("用户系统","/api-a/v2/api-docs","2.0"));
        resources.add(swaggerResource("部门系统","/api-b/v2/api-docs","2.0"));
        return resources;
    }

    /**
     * 创建swagger资源对象
     * @param name
     * @param location
     * @param version
     * @return
     */
    private SwaggerResource swaggerResource(String name,String location,String version){
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setSwaggerVersion(version);
        swaggerResource.setLocation(location);
        return swaggerResource;
    }
}
