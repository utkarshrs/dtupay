package dtu.pay.facade;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import messaging.Event;
import messaging.MessageQueue;

public class FacadeTestContext {

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
        
        public void clearLastPublishedEvent() {
            lastPublishedEvent = null;
        }
    }

    private static InMemoryMessageQueue queue;

    static {
        reset();
    }

    public static void reset() {
        queue = new InMemoryMessageQueue();
    }

    public static InMemoryMessageQueue getQueue() {
        return queue;
    }
}
