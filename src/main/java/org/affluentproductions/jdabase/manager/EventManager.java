package org.affluentproductions.jdabase.manager;

import org.affluentproductions.jdabase.JDABase;
import org.affluentproductions.jdabase.api.AffluentListener;
import org.affluentproductions.jdabase.event.AffluentEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class EventManager {

    private final JDABase jdaBase;
    private final ExecutorService executorService;
    private final List<AffluentListener> listeners = new ArrayList<>();

    public EventManager(JDABase jdaBase, ExecutorService executorService) {
        this.executorService = executorService;
        this.jdaBase = jdaBase;
    }

    public void addListener(AffluentListener affluentListener) {
        listeners.add(affluentListener);
    }

    public void removeListener(AffluentListener affluentListener) {
        listeners.remove(affluentListener);
    }

    public void fireEvent(AffluentEvent affluentEvent) {
        try {
            if (executorService != null && !executorService.isShutdown()) {
                executorService.execute(() -> handle(affluentEvent));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handle(AffluentEvent affluentEvent) {
        for (AffluentListener affluentListener : listeners) {
            affluentEvent.setJDABase(jdaBase);
            affluentListener.onEvent(affluentEvent);
        }
    }
}
