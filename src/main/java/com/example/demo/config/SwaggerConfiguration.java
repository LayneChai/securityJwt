package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author: zsqt
 * Package:  com.zsqt.security.config
 * @date: 2024/1/4 14:29
 * @Description:
 * @version: 1.0
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Bean
    public Docket createDocApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .groupName("接口文档")
                .pathMapping("/")
                .apiInfo(DocApi());
    }

    /**
     * 构建 api文档的详细信息函数,注意这里的注解引用的是哪个
     */
    private ApiInfo DocApi() {
        return new ApiInfoBuilder()
                //页面标题
                .title("接口测试工具")
                //创建人
                .contact(new Contact("", "", ""))
                //版本号
                .version("1.0")
                //描述
                .description("接口测试工具")
                .build();
    }

}
