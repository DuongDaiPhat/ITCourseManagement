package utils;

import java.util.ArrayList;
import java.util.List;

public class SimpleEventBus {
    private static final SimpleEventBus instance = new SimpleEventBus();
    private final List<Object> subscribers = new ArrayList<>();

    public static SimpleEventBus getInstance() {
        return instance;
    }

    public void register(Object subscriber) {
        if (!subscribers.contains(subscriber)) {
            subscribers.add(subscriber);
        }
    }

    public void post(Object event) {
        for (Object subscriber : subscribers) {
            try {
                for (java.lang.reflect.Method method : subscriber.getClass().getDeclaredMethods()) {
                    if (method.isAnnotationPresent(Subscribe.class) && method.getParameterCount() == 1 && method.getParameterTypes()[0].isInstance(event)) {
                        method.setAccessible(true);
                        method.invoke(subscriber, event);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error posting event to subscriber: " + e.getMessage());
            }
        }
    }
}