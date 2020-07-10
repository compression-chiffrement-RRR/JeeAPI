package com.cyphernet.api;

import com.cyphernet.api.storage.AmazonClient;
import com.cyphernet.api.storage.interceptor.DownloadFileInterceptor;
import com.cyphernet.api.storage.service.UserFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    private final UserFileService userFileService;
    private final AmazonClient amazonClient;

    @Autowired
    public WebConfig(UserFileService userFileService, AmazonClient amazonClient) {
        this.userFileService = userFileService;
        this.amazonClient = amazonClient;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new DownloadFileInterceptor(userFileService, amazonClient)).addPathPatterns("/api/file/download/**");
    }
}
