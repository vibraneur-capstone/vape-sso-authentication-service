package com.vape.sso;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootApplication
@ComponentScan(
        basePackages = {"com.vape", "com.vape.sso"}
)
public class SsoApplicationTests {

    public static void main(String args[]) {
        SpringApplication.run(SsoApplicationTests.class, args);
    }
}
