package net.martinprobson.example;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Subscribe extends GcpConfiguration{


    private static final Logger logger = LoggerFactory.getLogger(Subscribe.class);

    public static void main(String... args) throws Exception {
        var subscriptionId = "MyTestSub";

        subscribeAsyncExample(loadCredentials(), subscriptionId);
    }

    public static void subscribeAsyncExample(ServiceAccountCredentials serviceAccountCredentials, String subscriptionId) {
        var subscriptionName =
                ProjectSubscriptionName.of(serviceAccountCredentials.getProjectId(), subscriptionId);

        // Instantiate an asynchronous message receiver.
        MessageReceiver receiver =
                (PubsubMessage message, AckReplyConsumer consumer) -> {
                    // Handle incoming message, then ack the received message.
                    logger.info("Id: {} Data:  {}", message.getMessageId(), message.getData().toStringUtf8());
                    consumer.ack();
                };

        Subscriber subscriber = null;
        try {
            subscriber = getSubscriber(receiver, subscriptionName);
            // Start the subscriber.
            subscriber.startAsync().awaitRunning();
            logger.info("Listening for messages on {}", subscriptionName);
            // Allow the subscriber to run for 30s unless an unrecoverable error occurs.
            subscriber.awaitTerminated(300, TimeUnit.SECONDS);
        } catch (TimeoutException timeoutException) {
            // Shut down the subscriber after 30s. Stop receiving messages.
            subscriber.stopAsync();
        }
    }

    private static Subscriber getSubscriber(MessageReceiver receiver, ProjectSubscriptionName subscriptionName) {
        return Subscriber.newBuilder(subscriptionName, receiver)
                .setCredentialsProvider(GcpConfiguration::loadCredentials)
                .build();
    }

}
