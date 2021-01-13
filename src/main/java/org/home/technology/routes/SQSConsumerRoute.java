package org.home.technology.routes;

import com.amazonaws.services.sqs.AmazonSQSResponder;
import com.amazonaws.services.sqs.MessageContent;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import org.apache.camel.builder.RouteBuilder;
import org.home.technology.configuration.SQSVirtualClientConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SQSConsumerRoute extends RouteBuilder {

    @Autowired
    private SQSVirtualClientConfiguration sqsVirtualClientConfiguration;

    @Override
    public void configure() throws Exception {
        from("aws-sqs://{{aws-sqs-queue-name}}?amazonSQSClient=#amazonSQSClient&messageAttributeNames=*&concurrentConsumers=10")
                .log("${body}")
                .process((exchange) -> {
                    final Message requestMessage = convertToAWSMessage(exchange.getIn().getBody(String.class), exchange.getIn().getHeaders());
                    AmazonSQSResponder amazonSQSResponder = sqsVirtualClientConfiguration
                            .amazonSQSResponder();
                    MessageContent messageContent = MessageContent.fromMessage(requestMessage);
                    if (amazonSQSResponder.isResponseMessageRequested(messageContent))
                        amazonSQSResponder.sendResponseMessage(messageContent,
                                new MessageContent("System response for message: " + requestMessage.getMessageAttributes().get("CamelAwsSqsMessageId")));
                }).onException(Exception.class)
                .handled(true)
                .log("${body}");
    }

    private Message convertToAWSMessage(String body, Map<String, Object> headers) {
        Message awsMessage = new Message();
        awsMessage.setBody(body);
        Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
        headers.forEach((key, value) -> messageAttributes.put(key, new MessageAttributeValue().withStringValue((value.toString()))));
        awsMessage.setMessageAttributes(messageAttributes);
        return awsMessage;
    }
}
