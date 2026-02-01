package dtu.pay.facade.config;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import messaging.MessageQueue;
import messaging.implementations.RabbitMqQueue;

@ApplicationScoped
public class MessageQueueProducer {

    @ConfigProperty(name = "rabbitmq.host", defaultValue = "localhost")
    String rabbitMqHost;

    @Produces
    @ApplicationScoped
    public MessageQueue createMessageQueue() {
        return new RabbitMqQueue(rabbitMqHost);
    }
}
