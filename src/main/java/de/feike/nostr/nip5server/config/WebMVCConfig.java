package de.feike.nostr.nip5server.config;

import de.feike.nostr.nip5server.controller.AdminScopeInterceptor;
import de.feike.nostr.nip5server.controller.Nip5ServerController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMVCConfig implements WebMvcConfigurer {

    @Autowired
    private AdminScopeInterceptor adminScopeInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminScopeInterceptor).addPathPatterns(Nip5ServerController.ADMIN_PATH + "/*");
    }

}
