/*
 * Copyright 2012-2015 the original author or authors.
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

package com.ufu.crokage.config;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import com.ufu.crokage.resources.QueryResource;

@Component
@ApplicationPath("/crokage")
@ComponentScan
public class JerseyConfig extends ResourceConfig {

	public JerseyConfig() {
		
		/*packages("com.ufu.crokage.config");
		packages("com.ufu.crokage.resources");
		packages("com.ufu.crokage.util");*/
		
		/*
		 * register(AppAux.class); register(SimpleCORSFilter.class);
		 * register(MyServiceWebAppInitializer.class);
		 */
        
        register(QueryResource.class);
        //register(QueryResource.class);
        
				
		
	}

}
