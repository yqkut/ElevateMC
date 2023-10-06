package dev.apposed.prime.proxy.module.rank;

import com.mongodb.client.MongoCollection;
import dev.apposed.prime.proxy.module.Module;
import dev.apposed.prime.proxy.module.database.mongo.MongoModule;
import dev.apposed.prime.proxy.module.profile.ProfileHandler;
import dev.apposed.prime.proxy.module.rank.meta.RankMeta;
import dev.apposed.prime.proxy.util.json.JsonHelper;
import lombok.Getter;
import org.bson.Document;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Getter
public class RankHandler extends Module {

    private final ProfileHandler profileHandler = this.getPlugin().getModuleHandler().getModule(ProfileHandler.class);

    private Set<Rank> cache;
    private MongoCollection<Document> collection;

    @Override
    public void onEnable() {
        this.cache = new HashSet<>();
        this.collection = this.getModuleHandler().getModule(MongoModule.class).getMongoDatabase().getCollection("ranks");
        this.collection.find().iterator().forEachRemaining(document -> add(JsonHelper.GSON.fromJson(JsonHelper.GSON.toJson(document), Rank.class)));
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

        //todo: npe
//        this.profileHandler.getProfiles().stream().filter(Objects::nonNull).filter(profile -> profile.hasRank(updatedRank)).forEach(this.profileHandler::setupPlayer);
    }

}
