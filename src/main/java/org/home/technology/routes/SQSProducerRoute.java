package org.home.technology.routes;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import org.apache.camel.builder.RouteBuilder;
import org.home.technology.configuration.SQSVirtualClientConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class SQSProducerRoute extends RouteBuilder {

    @Value(value = "${aws-sqs-queue-name}")
    private String sqsQueueName;

    @Autowired
    private SQSVirtualClientConfiguration sqsVirtualClientConfiguration;

    @Override
    public void configure() throws Exception {
        from("seda:fromRestEndpoint?concurrentConsumers=10")
                .process((exchange) -> {
                    SendMessageRequest request = new SendMessageRequest()
                            .withMessageBody(exchange.getIn().getBody(String.class))
                            .withQueueUrl(sqsQueueName)
                            .withMessageGroupId("foo")
                            .withMessageDeduplicationId(UUID.randomUUID().toString());

                    // If no response is received, in 20 seconds,
                    // trigger the TimeoutException.
                    Message reply = sqsVirtualClientConfiguration
                            .amazonSQSRequester()
                            .sendMessageAndGetResponse(request,
                                    20, TimeUnit.SECONDS);
                    exchange.getIn().setBody(reply.getBody());
                })
                .log("${body}")
                .choice().when(simple("${header.isLoadTest} == null || ${header.isLoadTest} == 'false'"))
                .to("seda:collectSQSResponse");
    }
}
