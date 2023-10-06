package dev.apposed.prime.proxy.module.profile;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Collation;
import com.mongodb.client.model.CollationStrength;
import com.mongodb.client.model.Filters;
import com.velocitypowered.api.proxy.Player;
import dev.apposed.prime.proxy.module.Module;
import dev.apposed.prime.proxy.module.database.mongo.MongoModule;
import dev.apposed.prime.proxy.util.json.JsonHelper;
import dev.apposed.prime.proxy.util.mojang.MojangUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bson.Document;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

@Getter
public class ProfileHandler extends Module {

    private Cache<UUID, Profile> cache;
    private MongoCollection<Document> collection;

    @Override
    public void onEnable() {
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .removalListener((RemovalListener<UUID, Profile>) n -> {
                    if(!n.wasEvicted()) return;
                    if(n.getKey() != null && n.getValue() != null && n.getValue().getPlayer() != null) {
                        cache.put(n.getKey(), n.getValue());
                    }
                })
                .build();
        this.collection = this.getModuleHandler().getModule(MongoModule.class).getMongoDatabase().getCollection("profiles");
    }

    @SneakyThrows
    public Profile load(Document document) {
        return JsonHelper.GSON.fromJson(JsonHelper.GSON.toJson(document), Profile.class);
    }

    public CompletableFuture<Profile> load(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Profile> profile = getProfile(uuid);

            if (profile.isPresent()) return profile.get();

            // In database
            profile = this.fetchFromDatabase(uuid);
            if (profile.isPresent()) {
                add(profile.get());
                return profile.get();
            }

            Player player = getPlugin().getServer().getPlayer(uuid).orElse(null);
            if (player == null || !player.isActive()) return null;
            return add(new Profile(player));
        });
    }

    public void updateProfile(Profile updatedProfile) {
        Profile profile = this.getProfile(updatedProfile.getUuid()).orElse(null);
        if(profile == null) {
            profile = updatedProfile;
            add(profile);
        }

        profile.setOnline(updatedProfile.isOnline());
        profile.setGrants(updatedProfile.getGrants());
        profile.setPermissions(updatedProfile.getPermissions());
        profile.setUsername(updatedProfile.getUsername());
        profile.setUuid(updatedProfile.getUuid());
    }

    public Profile add(Profile profile) {
        this.cache.put(profile.getUuid(), profile);
        return profile;
    }

    public Optional<Profile> fetchFromDatabase(UUID uuid) {
        final MongoCursor<Document> profile = this.collection.find(Filters.eq("_id", uuid.toString())).iterator();
        if(profile.hasNext()) {
            Profile loadedProfile = load(profile.next());
            if(loadedProfile != null) {
                return Optional.of(loadedProfile);
            }
        }

        return Optional.empty();
    }

    public Profile fetchFromDatabase(String username) {
        String patternString = "(?i)^" + username + "$";
        Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
        final MongoCursor<Document> profile = this.collection.find(Filters.regex("username", pattern)).collation(Collation.builder().locale("en").collationStrength(CollationStrength.SECONDARY).build()).iterator();
        if(profile.hasNext()) {
            Profile loadedProfile = load(profile.next());
            if(loadedProfile != null) {
                return loadedProfile;
            }
        }

        // create new profile | fetch uuid of player

        final AtomicReference<UUID> uuidRef = new AtomicReference<>();
        MojangUtils.getUUIDForPlayerName(username, uuidString-> {;
            if(uuidString == null) return;
            uuidRef.set(UUID.fromString(uuidString));
        });

        final Optional<Profile> profileOptional = getProfile(uuidRef.get());
        return profileOptional.orElseGet(() -> add(new Profile(uuidRef.get(), username)));

    }

    public Optional<Profile> getProfile(UUID uuid) {
        return Optional.ofNullable(cache.getIfPresent(uuid));
    }


    public CompletableFuture<Profile> getProfile(String name) {
        return CompletableFuture.supplyAsync(() -> {
            final Optional<Profile> profile = getProfiles().stream().filter(p -> p.getUsername().equalsIgnoreCase(name)).findFirst();
            return profile.orElseGet(() -> fetchFromDatabase(name));
        });
    }

    public Profile getProfile(String name, UUID uuid) {
        final Optional<Profile> profile = this.getProfile(uuid);
        return profile.orElseGet(() -> this.add(new Profile(uuid, name)
        ));

    }

    public Collection<Profile> getProfiles() {
        return cache.asMap().values();
    }

}
