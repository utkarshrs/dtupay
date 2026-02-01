package dtu.pay.report;

import dtu.pay.report.service.ReportService;
import messaging.Event;
import messaging.MessageQueue;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ReportTestContext {

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
    private static ReportServiceMessageHandler handler;

    static {
        reset();
    }

    public static void reset() {
        queue = new InMemoryMessageQueue();
        handler = new ReportServiceMessageHandler(queue, new ReportService());
    }

    public static InMemoryMessageQueue getQueue() {
        return queue;
    }

    public static ReportServiceMessageHandler getHandler() {
        return handler;
    }
}
