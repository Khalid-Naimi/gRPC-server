package de.fhaachen.si.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:stock.properties")
public class StockConfig {
    @Value("${erp.endpoint}")
    private String endpoint;
    @Value("${erp.username}")
    private String username;
    @Value("${erp.password}")
    private String password;

    public String endpoint() {
        return endpoint;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }
}