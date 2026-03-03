package com.chobbi.server;

import com.chobbi.server.seed.SeedProductsService;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

import java.nio.file.Path;
import java.util.Arrays;

public class ProductSeedRunner {

    public static void main(String[] args) throws Exception {
        String jsonArgPrefix = "--json=";
        String jsonArg = Arrays.stream(args)
                .filter(a -> a != null && a.startsWith(jsonArgPrefix))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Missing --json=/path/to/product_seed.json argument"));

        String jsonPathString = jsonArg.substring(jsonArgPrefix.length());
        if (jsonPathString.isBlank()) {
            throw new IllegalArgumentException("JSON path after --json= must not be blank");
        }

        ApplicationContext ctx = new SpringApplicationBuilder(ServerApplication.class)
                // Chạy với web context đầy đủ để SecurityConfig có HttpSecurity
                // nhưng dùng server.port=0 để tránh trùng port với app chính
                .properties("server.port=0")
                .web(WebApplicationType.SERVLET)
                .run(args);
        try {
            SeedProductsService seeder = ctx.getBean(SeedProductsService.class);
            seeder.seedFromJson(Path.of(jsonPathString));
        } finally {
            System.exit(0);
        }
    }
}

