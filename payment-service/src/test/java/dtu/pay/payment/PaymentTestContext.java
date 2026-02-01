package dtu.pay.payment;

import dtu.pay.payment.infrastructure.StubBankTransferAdapter;
import dtu.pay.payment.service.PaymentService;
import messaging.Event;
import messaging.MessageQueue;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class PaymentTestContext {

    public static class InMemoryMessageQueue implements MessageQueue {

        private final Map<String, Consumer<Event>> handlers = new HashMap<>();
        private Event lastPublishedEvent;

        @Override
        public void publish(Event event) {
            Consumer<Event> handler = handlers.get(event.getType());
            if (handler != null) {
                handler.accept(event);
            } else {
                lastPublishedEvent = event;
            }
        }

        @Override
        public void addHandler(String topic, Consumer<Event> handler) {
            handlers.put(topic, handler);
        }

        public Event getLastPublishedEvent() {
            return lastPublishedEvent;
        }
    }

    private static InMemoryMessageQueue queue;
    private static PaymentServiceMessageHandler handler;
    private static StubBankTransferAdapter bankAdapter;

    static {
        reset();
    }

    public static void reset() {
        queue = new InMemoryMessageQueue();
        bankAdapter = new StubBankTransferAdapter();
        var service = new PaymentService(bankAdapter);
        handler = new PaymentServiceMessageHandler(queue, service);
    }

    public static InMemoryMessageQueue getQueue() {
        return queue;
    }

    public static PaymentServiceMessageHandler getHandler() {
        return handler;
    }

    public static StubBankTransferAdapter getBankAdapter() {
        return bankAdapter;
    }
}
