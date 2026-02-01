package dtu.pay.account;

import dtu.pay.account.service.AccountService;
import messaging.implementations.RabbitMqQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartUp {

    private static final Logger logger =
            LoggerFactory.getLogger(StartUp.class);

    public static void main(String[] args) throws Exception {
        new StartUp().startUp();
    }

    private void startUp() {
        logger.info("Account-service startup");

        String rabbitHost = System.getenv().getOrDefault("RABBITMQ_HOST", "localhost");
        logger.info("Connecting to RabbitMQ at: {}", rabbitHost);

        var mq = new RabbitMqQueue(rabbitHost);
        var service = new AccountService();
        new AccountServiceMessageHandler(mq, service);

        logger.info("Account-service successfully started");
    }
}
