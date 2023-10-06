package com.elevatemc.equeue.queue;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
public class QueueEntry implements Comparable<QueueEntry> {
    @Getter private UUID UUID;
    @Getter private int priority;

    @Override
    public int compareTo(QueueEntry o) {
        return this.getPriority() - o.getPriority();
    }
}
