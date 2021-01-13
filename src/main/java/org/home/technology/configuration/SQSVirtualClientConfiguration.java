package org.home.technology.configuration;

import com.amazonaws.services.sqs.AmazonSQSRequester;
import com.amazonaws.services.sqs.AmazonSQSRequesterClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSResponder;
import com.amazonaws.services.sqs.AmazonSQSResponderClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

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
                .withIdleQueueRetentionPeriodSeconds(1)
                .withIdleQueueSweepingPeriod(1, TimeUnit.SECONDS)
                .withQueueHeartbeatInterval(1)
                .withAmazonSQS(sqsClientConfiguration.getAmazonSQSClient())
                .build();
    }
}
