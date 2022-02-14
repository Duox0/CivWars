package ru.civwars.thread.request;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import ru.lib27.annotation.NotNull;

public abstract class AsyncRequest {

    private final Condition condition;
    private Boolean finished = false;

    public AsyncRequest(@NotNull ReentrantLock lock) {
        this.condition = lock.newCondition();
    }
    
    @NotNull
    public Condition getCondition() {
        return this.condition;
    }

    public void finished() {
        this.finished = true;
    }
    
    public boolean isFinished() {
        return this.finished;
    }
}
