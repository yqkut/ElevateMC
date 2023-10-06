package dev.apposed.prime.proxy.module.profile.grant;

import com.google.gson.annotations.SerializedName;
import dev.apposed.prime.proxy.module.rank.Rank;
import dev.apposed.prime.proxy.util.time.DurationUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data @AllArgsConstructor
public class Grant {

    @SerializedName("_id")
    private UUID id;

    private final Rank rank;

    private final UUID addedBy;
    private final long addedAt;
    private final String addedReason;
    private final long duration;
    private final List<String> scopes;

    private UUID removedBy;
    private long removedAt;
    private String removedReason;

    private boolean removed;

    public Grant(Rank rank, UUID addedBy, long addedAt, String addedReason, long duration, List<String> scopes) {
        this.id = UUID.randomUUID();
        this.rank = rank;

        this.addedBy = addedBy;
        this.addedAt = addedAt;
        this.addedReason = addedReason;
        this.duration = duration;
        this.scopes = scopes;
    }

    public boolean isActive() {
        if(!removed) {
            if(this.duration == Long.MAX_VALUE) return true;
            return System.currentTimeMillis() <= (this.addedAt + this.duration);
        }

        return false;
    }

    public long getRemaining() {
        if(removed) return 0L;
        if(duration == Long.MAX_VALUE) return Long.MAX_VALUE;
        if(!isActive()) return 0L;

        return (addedAt + duration) - System.currentTimeMillis();
    }

    public String formatDuration() {
        if(this.duration == Long.MAX_VALUE) return "Permanent";
        return DurationUtils.toString(this.addedAt + this.duration);
    }
}
