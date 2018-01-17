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
package org.particleframework.http.server.netty.binding

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.particleframework.context.ApplicationContext
import org.particleframework.core.io.socket.SocketUtils
import org.particleframework.http.HttpHeaders
import org.particleframework.http.HttpStatus
import org.particleframework.runtime.ParticleApplication
import org.particleframework.runtime.server.EmbeddedServer
import org.particleframework.http.annotation.Controller
import org.particleframework.http.annotation.Get
import org.particleframework.http.annotation.Put
import spock.lang.Specification

/**
 * @author Graeme Rocher
 * @since 1.0
 */
class NettyHttpServerSpec extends Specification {


    void "test Particle server running"() {
        when:
        ApplicationContext applicationContext = ParticleApplication.run()
        int port = applicationContext.getBean(EmbeddedServer).getPort()

        OkHttpClient client = new OkHttpClient()
        def request = new Request.Builder()
                .url("http://localhost:$port/person/Fred")

        def response = client.newCall(request.build()).execute()
        then:
        response.body().string() == "Person Named Fred"

        cleanup:
        applicationContext?.stop()
    }

    void "test Particle server running again"() {
        when:
        ApplicationContext applicationContext = ParticleApplication.run()
        int port = applicationContext.getBean(EmbeddedServer).getPort()
        OkHttpClient client = new OkHttpClient()
        def request = new Request.Builder()
                .url("http://localhost:$port/person/Fred")

        def response = client.newCall(request.build()).execute()

        then:
        response.body().string() == "Person Named Fred"

        cleanup:
        applicationContext?.stop()
    }

    void "test Particle server on different port"() {
        when:
        int newPort = SocketUtils.findAvailableTcpPort()
        ApplicationContext applicationContext = ParticleApplication.run('-port',newPort.toString())

        OkHttpClient client = new OkHttpClient()
        def request = new Request.Builder()
                .url("http://localhost:$newPort/person/Fred")

        def response = client.newCall(request.build()).execute()

        then:
        response.body().string() == "Person Named Fred"

        cleanup:
        applicationContext?.stop()
    }

    void "test bind method argument from request parameter"() {
        when:
        int newPort = SocketUtils.findAvailableTcpPort()
        ApplicationContext applicationContext = ParticleApplication.run('-port',newPort.toString())

        OkHttpClient client = new OkHttpClient()
        def request = new Request.Builder()
                .url("http://localhost:$newPort/person/another/job?id=10")

        def response = client.newCall(request.build()).execute()

        then:
        response.body().string() == "JOB ID 10"

        cleanup:
        applicationContext?.stop()
    }

    void "test bind method argument from request parameter when parameter missing"() {
        when:"A required request parameter is missing"
        int newPort = SocketUtils.findAvailableTcpPort()
        ApplicationContext applicationContext = ParticleApplication.run('-port',newPort.toString())

        OkHttpClient client = new OkHttpClient()
        def request = new Request.Builder()
                .url("http://localhost:$newPort/person/another/job")

        def response = client.newCall(request.build()).execute()


        then:"A 400 is returned"
        response.code() == HttpStatus.BAD_REQUEST.code

        cleanup:
        applicationContext?.stop()
    }

    void "test allowed methods handling"() {
        when:"A request is sent to the server for the wrong HTTP method"
        int newPort = SocketUtils.findAvailableTcpPort()
        ApplicationContext applicationContext = ParticleApplication.run('-port',newPort.toString())

        def request = new Request.Builder()
                .url("http://localhost:$newPort/person/job/test")
                .header("Content-Length", "4")
                .post(RequestBody.create(MediaType.parse("text/plain"), "test"))
        OkHttpClient client = new OkHttpClient()
        def response = client.newCall(
                request.build()
        ).execute()


        then:
        response.code() == HttpStatus.METHOD_NOT_ALLOWED.code
        response.header(HttpHeaders.ALLOW) == 'PUT'
    }


    @Controller
    static class PersonController {

        @Get('/{name}')
        String name(String name) {
            "Person Named $name"
        }

        @Put('/job/{name}')
        void doWork(String name) {
            println 'doing work'
        }

        @Get('/another/job')
        String doMoreWork(int id) {
            "JOB ID $id"
        }
    }
}
