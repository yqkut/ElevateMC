package dev.apposed.prime.proxy.module.rank;

import com.google.gson.annotations.SerializedName;
import dev.apposed.prime.proxy.module.rank.meta.RankMeta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data @AllArgsConstructor @Getter
public class Rank {

    @SerializedName("_id")
    private String name;

    private List<RankMeta> meta;
    private List<Rank> inherits;
    private List<String> permissions;

    private String prefix = "";
    private String color = NamedTextColor.WHITE.toString();
    private int weight;

    public Rank(String name) {
        this.name = name;

        this.meta = new ArrayList<>();
        this.inherits = new ArrayList<>();
        this.permissions = new ArrayList<>();
    }

    public List<String> getFullPermissions() {
        final List<String> permissions = new ArrayList<>();

        permissions.addAll(this.permissions);

        for(Rank rank : inherits) {
            permissions.addAll(rank.getPermissions());
        }

        return permissions;
    }

    public boolean hasPermission(String permission) {
        return this.getFullPermissions().contains(permission);
    }

    public boolean inherits(Rank rank) {
        return this.inherits.contains(rank);
    }

    public boolean hasMeta(RankMeta meta, boolean individual) {
        if(individual)
            return this.meta.contains(meta);

        Optional<Rank> rankedMeta = this.inherits.stream().filter(rank -> rank.hasMeta(meta, true)).findAny();
        return rankedMeta.isPresent() || this.meta.contains(meta);
    }

    public String getColoredDisplay() {
        return this.color + this.name;
    }

    public int addWeight(int weight) {
        return this.weight += weight;
    }
}
