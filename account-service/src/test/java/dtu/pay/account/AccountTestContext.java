package dtu.pay.account;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import dtu.pay.account.service.AccountService;
import messaging.Event;
import messaging.MessageQueue;

public class AccountTestContext {

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
        
        public void clearLastPublishedEvent() {
            lastPublishedEvent = null;
        }
    }

    private static InMemoryMessageQueue queue;
    private static AccountServiceMessageHandler handler;
    private static AccountService service;

    static {
        reset();
    }

    public static void reset() {
        queue = new InMemoryMessageQueue();
        service = new AccountService();
        handler = new AccountServiceMessageHandler(queue, service);
    }

    public static InMemoryMessageQueue getQueue() {
        return queue;
    }

    public static AccountServiceMessageHandler getHandler() {
        return handler;
    }

    public static AccountService getService() {
        return service;
    }
}
