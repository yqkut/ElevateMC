package dev.apposed.prime.spigot.module.rank;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import dev.apposed.prime.packet.RankRefreshPacket;
import dev.apposed.prime.packet.type.RefreshType;
import dev.apposed.prime.spigot.module.Module;
import dev.apposed.prime.spigot.module.database.mongo.MongoModule;
import dev.apposed.prime.spigot.module.database.redis.JedisModule;
import dev.apposed.prime.spigot.module.profile.ProfileHandler;
import dev.apposed.prime.spigot.module.rank.meta.RankMeta;
import dev.apposed.prime.spigot.util.Color;
import dev.apposed.prime.spigot.util.json.JsonHelper;
import lombok.Getter;
import org.bson.Document;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Getter
public class RankHandler extends Module {

    private Set<Rank> cache;
    private MongoCollection<Document> collection;

    private JedisModule jedisModule;

    @Override
    public void onEnable() {
        this.cache = new HashSet<>();
        this.collection = this.getModuleHandler().getModule(MongoModule.class).getMongoDatabase().getCollection("ranks");
        this.jedisModule = this.getModuleHandler().getModule(JedisModule.class);

        this.collection.find().iterator().forEachRemaining(document -> add(JsonHelper.GSON.fromJson(JsonHelper.GSON.toJson(document), Rank.class)));

        if(this.getDefaultRank() == null) {
            Rank defaultRank = new Rank("Default");
            defaultRank.getMeta().add(RankMeta.DEFAULT);
            defaultRank.setColor(Color.translate("&7"));
            defaultRank.setWeight(1);

            this.create(defaultRank);
        }

        System.out.println("Loaded " + this.cache.size() + " ranks.");
    }

    @Override
    public void onDisable() {
        this.cache.forEach(rank -> this.collection.replaceOne(Filters.eq("_id", rank.getName()), Document.parse(JsonHelper.GSON.toJson(rank)), JsonHelper.REPLACE_OPTIONS));
    }

    public void save(Rank rank) {
        new Thread(() -> {
            this.collection.replaceOne(Filters.eq("_id", rank.getName()), Document.parse(JsonHelper.GSON.toJson(rank)), JsonHelper.REPLACE_OPTIONS);
            this.jedisModule.sendPacket(
                    new RankRefreshPacket(rank, RefreshType.UPDATE)
            );
        }, "rank-save-" + rank.getName()).start();
    }

    public void updateRank(Rank updatedRank) {
        Rank rank = this.getRank(updatedRank.getName()).orElse(null);
        if(rank == null) {
            rank = updatedRank;
            this.cache.add(rank);
        }

        rank.setPrefix(updatedRank.getPrefix());
        rank.setColor(updatedRank.getColor());
        rank.setWeight(updatedRank.getWeight());
        rank.setPrefix(updatedRank.getPrefix());
        rank.setInherits(updatedRank.getInherits());
        rank.setMeta(updatedRank.getMeta());
        rank.setPermissions(updatedRank.getPermissions());
    }

    public Rank create(Rank rank) {
        save(rank);
        add(rank);
        return rank;
    }

    // need to make sure to remove all grants with this rank from profiles
    public void delete(Rank rank) {
        new Thread(() -> {
            this.collection.deleteOne(Filters.eq("_id", rank.getName()));
            this.cache.remove(rank);
            this.jedisModule.sendPacket(new RankRefreshPacket(rank, RefreshType.REMOVE));
        }, "rank-delete-" + rank.getName()).start();
    }

    public Rank getDefaultRank() {
        return this.cache.stream().filter(rank -> rank.hasMeta(RankMeta.DEFAULT, true)).findFirst().orElse(null);
    }

    public Rank add(Rank rank) {
        this.cache.add(rank);
        return rank;
    }

    public Optional<Rank> getRank(String name) {
        return this.cache.stream().filter(rank -> rank.getName().equalsIgnoreCase(name)).findFirst();
    }

}
