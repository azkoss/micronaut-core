/*
 * Copyright 2017 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.particleframework.http.server.greenlightning;

import com.ociweb.gl.api.Builder;
import com.ociweb.gl.api.GreenApp;
import com.ociweb.gl.api.GreenRuntime;
import com.ociweb.gl.api.RestListener;
import org.particleframework.core.io.socket.SocketUtils;
import org.particleframework.http.server.HttpServerConfiguration;
import org.particleframework.web.router.Router;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class ParticleGreenLightningApp implements GreenApp {
    protected final int port;
    protected final String host;
    protected final String scheme;
    protected GreenRuntime runtime;
    protected final Optional<Router> router;

    @Inject
    public ParticleGreenLightningApp(HttpServerConfiguration serverConfiguration, Optional<Router> router) {
        this.router = router;

        int serverPort = serverConfiguration.getPort();
        this.port = serverPort == -1 ? SocketUtils.findAvailableTcpPort() : serverPort;

        this.host = serverConfiguration.getHost().orElse("localhost");
        this.scheme = serverConfiguration.getSsl().isEnabled() ? "https" : "http";
    }

    @Override
    public void declareConfiguration(final Builder builder) {
        builder.useHTTP1xServer(port).setHost(host).useInsecureServer();
    }

    public void declareBehavior(final GreenRuntime runtime) {
        final RestListener adder = new GreenLightningParticleDispatcher(runtime, router);
        runtime.addRestListener(adder).includeAllRoutes();
        this.runtime = runtime;
    }

    public void stop() {
        runtime.shutdownRuntime();
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public String getScheme() { return scheme; }
}
