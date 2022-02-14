package ru.civwars.chat.requests;

import ru.civwars.entity.player.KPlayer;
import ru.lib27.annotation.NotNull;
import ru.lib27.annotation.Nullable;

public class ChatRequest {

    private final long id;
    private final KPlayer sender;
    private final KPlayer receiver;
    private final int seconds;
    private long expireTime;

    private boolean isCompleted = false;

    protected ChatRequest(@NotNull KPlayer sender, @NotNull KPlayer receiver, int seconds) {
        this.id = ChatRequestManager.instance.getNextId();
        this.sender = sender;
        this.receiver = receiver;
        this.seconds = seconds >= 0 ? seconds : 0;
        this.expireTime = System.currentTimeMillis() + (this.seconds * 1000);
    }

    /**
     * @return идентификатор запроса.
     */
    @NotNull
    public final long getId() {
        return this.id;
    }

    /**
     * @return отправитель.
     */
    @NotNull
    public final KPlayer getSender() {
        return this.sender;
    }

    /**
     * @return получатель.
     */
    @NotNull
    public final KPlayer getReceiver() {
        return this.receiver;
    }

    /**
     * @return {@code true}, если время на ответ вышло. Иначе {@code false}.
     */
    public boolean isExpired() {
        return this.expireTime < System.currentTimeMillis();
    }

    /**
     * Является ли запрос завершенным.
     *
     * @return {@code true}, если запрос завершен. Иначе {@code false}.
     */
    public boolean isCompleted() {
        return this.isCompleted;
    }

    public boolean process(@NotNull KPlayer receiver, boolean flag, @NotNull String[] args) {
        if (this.receiver != receiver) {
            return false;
        } else if (this.isCompleted) {
            return true;
        }

        if (flag) {
            this.cancel(receiver);
            this.isCompleted = true;
            return true;
        } else {
            if (this.accept(receiver, args)) {
                this.isCompleted = true;
            }
            return this.isCompleted;
        }
    }

    protected boolean accept(@NotNull KPlayer receiver, @NotNull String[] args) {
        return true;
    }

    protected void cancel(@Nullable KPlayer receiver) {
    }

}
