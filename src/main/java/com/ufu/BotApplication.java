/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ufu;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
//@ComponentScan(basePackages={"br.ufu.sp","br.ufu.sp.service","br.ufu.sp.repository"})
@EnableTransactionManagement
public class BotApplication extends SpringBootServletInitializer {

	/*
	 * Para rodar essa app no modo debug, rode essa classe no mode debug. 	  
	 */
	public static void main(String[] args) {
		new BotApplication().configure(new SpringApplicationBuilder(BotApplication.class)).run(args);
	}
	
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(BotApplication.class);
    }
	
	@Bean
	protected ServletContextListener listener() {
		return new ServletContextListener() {

			@Override
			public void contextInitialized(ServletContextEvent sce) {
				//logger.info("ServletContext initialized");
			}

			@Override
			public void contextDestroyed(ServletContextEvent sce) {
				//logger.info("ServletContext destroyed");
			}

		};
	}
	
	@Bean
	public ResourceBundleMessageSource messageSource() {
	 ResourceBundleMessageSource source = new ResourceBundleMessageSource();
	 source.setBasenames("messages");  // name of the resource bundle 
	 source.setUseCodeAsDefaultMessage(true);
	 return source;
	}

}
