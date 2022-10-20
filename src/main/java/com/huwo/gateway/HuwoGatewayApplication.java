package com.huwo.gateway;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.huwo.gateway.mapper")
public class HuwoGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuwoGatewayApplication.class, args);
    }

}
