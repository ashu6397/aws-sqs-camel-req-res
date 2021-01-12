package org.home.technology.controller;

import org.apache.camel.*;
import org.apache.camel.impl.DefaultExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class MessageController {

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private CamelContext camelContext;

    @PostMapping(value = "/sendMessage")
    public String sendMessage(@RequestBody String requestBody) throws Exception {
        producerTemplate.sendBody("seda:fromRestEndpoint", requestBody);
        Endpoint endpoint =
                camelContext.getEndpoint("seda:collectSQSResponse");
        PollingConsumer consumer = endpoint.createPollingConsumer();
        Exchange exchange = consumer.receive();
        return Objects.requireNonNull(exchange.getIn().getBody(String.class), "Didn't get response back in time");
    }

    @PostMapping(value = "/loadTesting/{count}")
    public void loadTesting(@PathVariable Integer count) throws Exception {
        DefaultExchange defaultExchange = new DefaultExchange(camelContext);
        defaultExchange.getIn().setHeader("loadCount", count);
        producerTemplate.send("seda:loadTesting", defaultExchange);
    }
}
