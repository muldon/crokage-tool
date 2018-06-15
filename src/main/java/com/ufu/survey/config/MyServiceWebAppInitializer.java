package com.ufu.survey.config;

import javax.annotation.Priority;
import javax.servlet.ServletContext;

import org.springframework.stereotype.Component;
import org.springframework.web.WebApplicationInitializer;

@Component
@Priority(value = 1)
public class MyServiceWebAppInitializer implements WebApplicationInitializer
{
    @Override
    public void onStartup(ServletContext container)
    {
        //Tell jersey-spring3 the context is already initialized
        container.setInitParameter("contextConfigLocation", "NOTNULL");
        
    }
}