package com.elevatemc.ehub.queue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class QueuePosition {
    @Getter private String server;
    @Getter private int position;
    @Getter private int total;
}
