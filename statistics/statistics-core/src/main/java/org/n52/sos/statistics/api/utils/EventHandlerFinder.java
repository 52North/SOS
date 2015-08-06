package org.n52.sos.statistics.api.utils;

import java.util.Map;
import java.util.Objects;

import org.n52.sos.statistics.api.interfaces.IServiceEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventHandlerFinder {

    private static final Logger logger = LoggerFactory.getLogger(EventHandlerFinder.class);

    @SuppressWarnings("unchecked")
    public static <T> IServiceEventHandler<T> findHandler(Object object, Map<String, IServiceEventHandler<?>> handlers) {
        String key = object.getClass().getSimpleName();
        logger.debug("Searching handler for object by key {}", key);
        IServiceEventHandler<T> handler = (IServiceEventHandler<T>) handlers.get(key);

        if (handler == null) {
            logger.debug("Searching default handler for object by key default");
            handler = (IServiceEventHandler<T>) handlers.get("default");
        }

        Objects.requireNonNull(handler, "Can not find handler for object: " + key);
        return handler;
    }

    private EventHandlerFinder() {
    }

}
