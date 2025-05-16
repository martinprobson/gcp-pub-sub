package net.martinprobson.example;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Publish extends GcpConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(Publish.class);

    public static void main(String... args) throws Exception {
        var topicId = "MyTestTopic";

        publisherExample(loadCredentials(), topicId);
    }

    public static void publisherExample(ServiceAccountCredentials serviceAccountCredentials, String topicId)
            throws IOException, InterruptedException {
        Publisher publisher = null;
        try {
            // Create a publisher instance with default settings bound to the topic
            publisher = getPublisher(TopicName.of(serviceAccountCredentials.getProjectId(), topicId));

            var message = "Hello World!";
            var data = ByteString.copyFromUtf8(message);
            var pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

            // Once published, returns a server-assigned message id (unique within the topic)
            List<ApiFuture<String>> apiFutures = new ArrayList<>();
            for (var i = 0; i < 10; i++) {
                apiFutures.add(publisher.publish(pubsubMessage));
            }
            apiFutures.forEach(s -> {
                String messageId;
                try {
                    messageId = s.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
                logger.info("Published message ID: {}", messageId);
            });

        } finally {
            if (publisher != null) {
                // When finished with the publisher, shutdown to free up resources.
                publisher.shutdown();
                publisher.awaitTermination(1, TimeUnit.MINUTES);
            }
        }
    }

    private static Publisher getPublisher(TopicName topicName) throws IOException {
        return Publisher.newBuilder(topicName)
                .setCredentialsProvider(GcpConfiguration::loadCredentials)
                .build();
    }
}
