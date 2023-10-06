package dev.apposed.prime.spigot.module.tag;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@Data @AllArgsConstructor
public class Tag {

    private final String id;
    private String displayName;
    private String display;

    public Tag(String id) {
        this.id = id;
        this.displayName = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(id, tag.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
