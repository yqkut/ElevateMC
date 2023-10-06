package com.elevatemc.equeue.queue;

import com.elevatemc.equeue.eQueue;
import dev.apposed.prime.proxy.module.profile.Profile;

import java.util.Comparator;
import java.util.Optional;

public class QueueEntryComparator implements Comparator<QueueEntry> {
    @Override
    public int compare(QueueEntry entry1, QueueEntry entry2) {
        Optional<Profile> profile1 = eQueue.getInstance().getProfileHandler().getProfile(entry1.getUUID());
        Optional<Profile> profile2 = eQueue.getInstance().getProfileHandler().getProfile(entry2.getUUID());
        int firstEntryWeight = 0;
        int secondEntryWeight = 0;
        if (profile1.isPresent()) {
            firstEntryWeight = profile1.get().getHighestActiveGrant().getRank().getWeight();
        }
        if (profile2.isPresent()) {
            secondEntryWeight = profile2.get().getHighestActiveGrant().getRank().getWeight();
        }
        return secondEntryWeight - firstEntryWeight;
    }
}
