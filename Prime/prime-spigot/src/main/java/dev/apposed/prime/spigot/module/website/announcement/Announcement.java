package dev.apposed.prime.spigot.module.website.announcement;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data @RequiredArgsConstructor @AllArgsConstructor
public class Announcement {
    private final int id;
    private final long postedAt;
    private final String postedBy;
    private String title;
    private String content;

    public Announcement(int id, String postedBy, String title) {
        this.id = id;
        this.postedAt = System.currentTimeMillis();
        this.postedBy = postedBy;
        this.title = title;
        this.content = "No content provided";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Announcement that = (Announcement) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
