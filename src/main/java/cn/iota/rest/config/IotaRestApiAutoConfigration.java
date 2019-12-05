package cn.iota.rest.config;

import javax.servlet.MultipartConfigElement;

import org.flowable.common.rest.resolver.ContentTypeResolver;
import org.flowable.engine.ProcessEngine;
import org.flowable.rest.service.api.BpmnRestApiInterceptor;
import org.flowable.rest.service.api.RestUrls;
import org.flowable.spring.boot.RestApiAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import cn.iota.flowable.process.api.interceptors.FlowableRestApiInterceptor;

@Configuration
@ConditionalOnClass(ContentTypeResolver.class)
@ConditionalOnWebApplication
@AutoConfigureAfter(RestApiAutoConfiguration.class)
public class IotaRestApiAutoConfigration {

    @ConditionalOnClass(RestUrls.class)
    @ConditionalOnBean(ProcessEngine.class)
    @EnableConfigurationProperties(IotaEngineProperties.class)
    public static class RestApiConfigration implements ApplicationContextAware {

        protected ApplicationContext applicationContext;

        protected MultipartConfigElement multipartConfigElement;

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        @Bean
        public ServletRegistrationBean<DispatcherServlet> iotaService(IotaEngineProperties iotaEngineProperties) {
            IotaServlet servletProperties = iotaEngineProperties.getServlet();
            AnnotationConfigWebApplicationContext dispatcherServletConfiguration = new AnnotationConfigWebApplicationContext();
            dispatcherServletConfiguration.setParent(applicationContext);
            dispatcherServletConfiguration.register(IotaEngineRestConfiguration.class);
            DispatcherServlet servlet = new DispatcherServlet(dispatcherServletConfiguration);
            String path = servletProperties.getPath();
            String urlMapping = (path.endsWith("/") ? path + "*" : path + "/*");
            ServletRegistrationBean<DispatcherServlet> registrationBean = new ServletRegistrationBean<>(servlet, urlMapping);
            registrationBean.setName(servletProperties.getName());
            registrationBean.setLoadOnStartup(servletProperties.getLoadOnStartup());
            registrationBean.setAsyncSupported(true);
            if (multipartConfigElement != null) {
                registrationBean.setMultipartConfig(multipartConfigElement);
            }
            return registrationBean;
        }

    }
}