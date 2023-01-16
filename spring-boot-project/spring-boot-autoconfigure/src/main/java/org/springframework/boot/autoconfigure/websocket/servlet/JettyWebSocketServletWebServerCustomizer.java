/*
 * Copyright 2012-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.autoconfigure.websocket.servlet;

import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.webapp.AbstractConfiguration;
import org.eclipse.jetty.ee10.webapp.WebAppContext;
import org.eclipse.jetty.websocket.core.server.WebSocketMappings;
import org.eclipse.jetty.websocket.core.server.WebSocketServerComponents;
import org.eclipse.jetty.ee10.websocket.jakarta.server.internal.JakartaWebSocketServerContainer;
import org.eclipse.jetty.ee10.websocket.server.JettyWebSocketServerContainer;
import org.eclipse.jetty.ee10.websocket.servlet.WebSocketUpgradeFilter;

import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;

/**
 * WebSocket customizer for {@link JettyServletWebServerFactory}.
 *
 * @author Dave Syer
 * @author Phillip Webb
 * @author Andy Wilkinson
 * @since 2.0.0
 */
public class JettyWebSocketServletWebServerCustomizer
		implements WebServerFactoryCustomizer<JettyServletWebServerFactory>, Ordered {

	@Override
	public void customize(JettyServletWebServerFactory factory) {
		factory.addConfigurations(new AbstractConfiguration() {

			@Override
			public void configure(WebAppContext context) throws Exception {
				if (JettyWebSocketServerContainer.getContainer(context.getServletContext()) == null
						&& context.getServletContext() instanceof ServletContextHandler.ServletContextApi servletContextApi) {
					WebSocketServerComponents.ensureWebSocketComponents(context.getServer(),
							servletContextApi.getContextHandler());
					JettyWebSocketServerContainer.ensureContainer(context.getServletContext());
				}
				if (JakartaWebSocketServerContainer.getContainer(context.getServletContext()) == null
						&& context.getServletContext() instanceof ServletContextHandler.ServletContextApi servletContextApi) {
					WebSocketServerComponents.ensureWebSocketComponents(context.getServer(),
							servletContextApi.getContextHandler());
					WebSocketUpgradeFilter.ensureFilter(servletContextApi);
					WebSocketMappings.ensureMappings(servletContextApi.getContextHandler());
					JakartaWebSocketServerContainer.ensureContainer(servletContextApi);
				}
			}

		});
	}

	@Override
	public int getOrder() {
		return 0;
	}

}
