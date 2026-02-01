package dtu.pay.token;

import dtu.pay.token.service.TokenService;
import messaging.Event;
import messaging.MessageQueue;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class TokenTestContext {

    public static class InMemoryMessageQueue implements MessageQueue {

        private final Map<String, Consumer<Event>> handlers = new HashMap<>();
        private Event lastPublishedEvent;

        @Override
        public void publish(Event event) {
            lastPublishedEvent = event;
            Consumer<Event> handler = handlers.get(event.getType());
            if (handler != null) {
                handler.accept(event);
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
    private static TokenServiceMessageHandler handler;

    static {
        reset();
    }

    public static void reset() {
        queue = new InMemoryMessageQueue();
        handler = new TokenServiceMessageHandler(queue, new TokenService());
    }

    public static InMemoryMessageQueue getQueue() {
        return queue;
    }

    public static TokenServiceMessageHandler getHandler() {
        return handler;
    }
}

