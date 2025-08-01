package com.iemr.admin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.iemr.admin.utils.JwtAuthenticationUtil;
import com.iemr.admin.utils.JwtUserIdValidationFilter;

@Configuration
public class SecurityFilterConfig {

    @Autowired
    private JwtAuthenticationUtil jwtAuthenticationUtil;
    
    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public FilterRegistrationBean<JwtUserIdValidationFilter> jwtFilterRegistration() {
        FilterRegistrationBean<JwtUserIdValidationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new JwtUserIdValidationFilter(jwtAuthenticationUtil, allowedOrigins));
        registration.addUrlPatterns("/*");
        
        // Exclude health and version endpoints
        registration.addInitParameter("excludedUrls", "/health,/version");
        
        return registration;
    }
}