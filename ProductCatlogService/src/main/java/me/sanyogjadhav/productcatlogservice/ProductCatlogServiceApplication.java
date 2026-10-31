package me.sanyogjadhav.productcatlogservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductCatlogServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductCatlogServiceApplication.class, args);
        System.out.println("Product Service Started...");
    }

}
