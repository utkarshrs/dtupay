package dtu.pay.report;

import dtu.pay.report.service.ReportService;
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
        String rabbitHost = System.getenv().getOrDefault("RABBITMQ_HOST", "localhost");
        logger.info("Connecting to RabbitMQ at: " + rabbitHost);
        var mq = new RabbitMqQueue(rabbitHost);
        var service = new ReportService();
        new ReportServiceMessageHandler(mq, service);
    }
}
