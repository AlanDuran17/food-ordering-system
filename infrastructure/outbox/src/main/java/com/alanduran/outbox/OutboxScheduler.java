package com.alanduran.outbox;

public interface OutboxScheduler {

    void processOutboxMessage();
}
