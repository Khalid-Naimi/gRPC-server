package de.fhaachen.si.gateway.config;

import java.util.ResourceBundle;

public class Config {

    private final String endpoint;
    private final String username;
    private final String password;

    public Config() {
        ResourceBundle bundle = ResourceBundle.getBundle("config");
        this.endpoint = bundle.getString("erp.endpoint");
        this.username = bundle.getString("erp.username");
        this.password = bundle.getString("erp.password");
    }

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
