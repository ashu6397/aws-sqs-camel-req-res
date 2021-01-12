package org.home.technology.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LoadTestingRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("seda:loadTesting")
                .setHeader("isLoadTest", constant("true"))
                .loop(header("loadCount"))
                .process((exchange) -> {
                    exchange.getIn().setBody("Load testing" + UUID.randomUUID().toString());
                })
                .to("seda:fromRestEndpoint?waitForTaskToComplete=Never");
//                .end();
    }
}
