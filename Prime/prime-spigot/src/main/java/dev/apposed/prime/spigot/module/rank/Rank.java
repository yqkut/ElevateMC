package dev.apposed.prime.spigot.module.rank;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;
import dev.apposed.prime.spigot.module.profile.punishment.type.PunishmentType;
import dev.apposed.prime.spigot.module.rank.meta.RankMeta;
import dev.apposed.prime.spigot.util.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data @AllArgsConstructor
public class Rank {

    @SerializedName("_id")
    private String name;

    private List<RankMeta> meta;
    private List<PunishmentType> proofMeta;
    private List<Rank> inherits;
    private List<String> permissions;

    private String prefix = "";
    private String color = ChatColor.WHITE.toString();
    private int weight;

    public Rank(String name) {
        this.name = name;

        this.meta = new ArrayList<>();
        this.proofMeta = new ArrayList<>();
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

    public boolean requiresProof(PunishmentType type) {
        return this.proofMeta.contains(type);
    }

    public ItemStack getWool() {
        String color = this.color
                .replace(ChatColor.BOLD.toString(), "")
                .replace(ChatColor.ITALIC.toString(), "")
                .replace(ChatColor.UNDERLINE.toString(), "")
                .replace(ChatColor.MAGIC.toString(), "")
                .replace(ChatColor.RESET.toString(), "")
                .replace(ChatColor.STRIKETHROUGH.toString(), "");

        ItemBuilder itemBuilder = new ItemBuilder(Material.WOOL);

        if(color.contains(ChatColor.BLACK.toString())) {
            itemBuilder.dur(15);
        } else if(color.contains(ChatColor.DARK_BLUE.toString())) {
            itemBuilder.dur(11);
        } else if(color.contains(ChatColor.DARK_GREEN.toString())) {
            itemBuilder.dur(13);
        } else if(color.contains(ChatColor.DARK_AQUA.toString())) {
            itemBuilder.dur(9);
        } else if(color.contains(ChatColor.DARK_RED.toString())) {
            itemBuilder.dur(14);
        } else if(color.contains(ChatColor.DARK_PURPLE.toString())) {
            itemBuilder.dur(10);
        } else if(color.contains(ChatColor.GOLD.toString())) {
            itemBuilder.dur(1);
        } else if(color.contains(ChatColor.GRAY.toString())) {
            itemBuilder.dur(8);
        } else if(color.contains(ChatColor.DARK_GRAY.toString())) {
            itemBuilder.dur(7);
        } else if(color.contains(ChatColor.BLUE.toString())) {
            itemBuilder.dur(11);
        } else if(color.contains(ChatColor.GREEN.toString())) {
            itemBuilder.dur(5);
        } else if(color.contains(ChatColor.AQUA.toString())) {
            itemBuilder.dur(3);
        } else if(color.contains(ChatColor.RED.toString())) {
            itemBuilder.dur(14);
        } else if(color.contains(ChatColor.LIGHT_PURPLE.toString())) {
            itemBuilder.dur(2);
        } else if(color.contains(ChatColor.YELLOW.toString())) {
            itemBuilder.dur(4);
        } else if(color.contains(ChatColor.WHITE.toString())) {
            itemBuilder.dur(0);
        }

        return itemBuilder.build();
    }

    public String getColoredDisplay() {
        return this.color + this.name;
    }

    public int addWeight(int weight) {
        return this.weight += weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rank rank = (Rank) o;
        return Objects.equal(name, rank.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
