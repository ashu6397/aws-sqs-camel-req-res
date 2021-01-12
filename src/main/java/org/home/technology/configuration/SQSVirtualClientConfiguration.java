package org.home.technology.configuration;

import com.amazonaws.services.sqs.AmazonSQSRequester;
import com.amazonaws.services.sqs.AmazonSQSRequesterClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSResponder;
import com.amazonaws.services.sqs.AmazonSQSResponderClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SQSVirtualClientConfiguration {

    @Autowired
    private SQSClientConfiguration sqsClientConfiguration;

    @Bean
    public AmazonSQSResponder amazonSQSResponder() {

        return AmazonSQSResponderClientBuilder
                .standard()
                .withAmazonSQS(sqsClientConfiguration.getAmazonSQSClient())
                .build();

    }

    @Bean
    public AmazonSQSRequester amazonSQSRequester() {
        return AmazonSQSRequesterClientBuilder
                .standard()
                .withAmazonSQS(sqsClientConfiguration.getAmazonSQSClient())
                .build();
    }
}
