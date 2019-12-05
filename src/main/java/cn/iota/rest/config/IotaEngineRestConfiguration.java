package cn.iota.rest.config;

import org.flowable.rest.service.api.BpmnRestApiInterceptor;
import org.flowable.rest.service.api.RestResponseFactory;
import org.flowable.spring.boot.DispatcherServletConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.iota.flowable.process.api.interceptors.FlowableRestApiInterceptor;

@Import(DispatcherServletConfiguration.class)
@ComponentScan("cn.iota.rest.api")
public class IotaEngineRestConfiguration {

    @Autowired
    protected ObjectMapper objectMapper;

    @Bean
    public RestResponseFactory restResponseFactory() {
        return new RestResponseFactory(objectMapper);
    }

    @Bean
    public BpmnRestApiInterceptor restApiInterceptor() {
        return new FlowableRestApiInterceptor();
    }
}
