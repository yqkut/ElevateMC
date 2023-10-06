package dev.apposed.prime.spigot.module.server.filter;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

@Data
public class ChatFilter {

    private final UUID id;
    private final String description;
    private String regex;

    public ChatFilter(String description, String regex) {
        this.id = UUID.randomUUID();
        this.description = description;
        this.regex = regex;
    }

    public ChatFilter(Map<String, String> map) {
        this.id = UUID.fromString(map.get("id"));
        this.description = map.get("description");
        this.regex = map.get("pattern");
    }

    public Map<String, String> toMap() {
        final Map<String, String> map = new HashMap<>();
        map.put("id", id.toString());
        map.put("description", description);
        map.put("pattern", getPattern().toString());
        return map;
    }

    public Pattern getPattern() {
        return Pattern.compile(regex);
    }
}
