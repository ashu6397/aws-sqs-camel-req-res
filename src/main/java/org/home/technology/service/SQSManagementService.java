package org.home.technology.service;

import org.home.technology.configuration.SQSClientConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SQSManagementService {

    @Autowired
    private SQSClientConfiguration sqsClientConfiguration;

    public void bulkDeleteQueue(String queuePrefix) {
        sqsClientConfiguration.getAmazonSQSClient().listQueues(queuePrefix)
                .getQueueUrls().forEach((queueUrl -> sqsClientConfiguration.getAmazonSQSClient().deleteQueue(queueUrl)));
    }

}
