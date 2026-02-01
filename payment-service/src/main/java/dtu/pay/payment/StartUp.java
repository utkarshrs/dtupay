package dtu.pay.payment;

import dtu.pay.payment.infrastructure.BankServiceAdapter;
import dtu.pay.payment.service.PaymentService;
import messaging.implementations.RabbitMqQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartUp {

    private static final Logger logger =
            LoggerFactory.getLogger(StartUp.class);
    public static void main(String[] args) {
        new StartUp().startUp();
    }

    private void startUp() {
        String rabbitHost = System.getenv().getOrDefault("RABBITMQ_HOST", "localhost");
        logger.info("Connecting to RabbitMQ at: " + rabbitHost);
        var mq = new RabbitMqQueue(rabbitHost);
        var bankAdapter = new BankServiceAdapter();
        var service = new PaymentService(bankAdapter);
        new PaymentServiceMessageHandler(mq, service);
    }
}
