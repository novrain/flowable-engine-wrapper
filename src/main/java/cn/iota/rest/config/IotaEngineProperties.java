package cn.iota.rest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "iota.engine")
public class IotaEngineProperties {

    /**
     * The servlet configuration for the iota engine Rest API.
     */
    @NestedConfigurationProperty
    private final IotaServlet servlet = new IotaServlet("/fl/iota", "Iota Engine Rest API");

    public IotaServlet getServlet() {
        return servlet;
    }
}
