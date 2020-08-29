package org.affluentproductions.jdabase.thread;

public class AffluentThread extends Thread {

    private static long gid;
    private final long id;
    private volatile Thread thread;

    public AffluentThread(Runnable runnable) {
        this.thread = new Thread(runnable);
        thread.setDaemon(true);
        this.id = nextId();
        thread.setName("Affluent-Thread-" + id);
        thread.start();
    }

    public void cancel() {
        this.thread = null;
    }

    private long nextId() {
        return ++gid;
    }

    @Override
    public long getId() {
        return id;
    }

    public Thread getThread() {
        return thread;
    }
}