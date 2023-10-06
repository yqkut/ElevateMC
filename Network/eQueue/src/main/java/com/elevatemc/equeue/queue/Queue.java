package com.elevatemc.equeue.queue;

import com.elevatemc.equeue.util.SortedList;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Queue {
    @Getter @NonNull private String server;
    @Getter
    SortedList<QueueEntry> queueEntries = new SortedList<>(new QueueEntryComparator());
}