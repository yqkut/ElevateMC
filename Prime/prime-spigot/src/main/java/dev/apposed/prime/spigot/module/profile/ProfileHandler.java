package dev.apposed.prime.spigot.module.profile;

import com.elevatemc.elib.util.Callback;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Collation;
import com.mongodb.client.model.CollationStrength;
import com.mongodb.client.model.Filters;
import dev.apposed.prime.packet.ProfileRefreshPacket;
import dev.apposed.prime.packet.type.RefreshType;
import dev.apposed.prime.spigot.PrimeConstants;
import dev.apposed.prime.spigot.module.Module;
import dev.apposed.prime.spigot.module.database.mongo.MongoModule;
import dev.apposed.prime.spigot.module.database.redis.JedisModule;
import dev.apposed.prime.spigot.module.profile.grant.Grant;
import dev.apposed.prime.spigot.module.profile.grant.task.GrantExpiryTask;
import dev.apposed.prime.spigot.module.profile.identity.ProfileIdentity;
import dev.apposed.prime.spigot.module.profile.permission.PermissionHandler;
import dev.apposed.prime.spigot.module.profile.punishment.Punishment;
import dev.apposed.prime.spigot.module.profile.punishment.task.PunishmentCheckerTask;
import dev.apposed.prime.spigot.module.profile.punishment.type.PunishmentType;
import dev.apposed.prime.spigot.module.profile.task.ProfileSaveTask;
import dev.apposed.prime.spigot.module.rank.Rank;
import dev.apposed.prime.spigot.module.rank.RankHandler;
import dev.apposed.prime.spigot.module.server.ServerHandler;
import dev.apposed.prime.spigot.util.Color;
import dev.apposed.prime.spigot.util.json.JsonHelper;
import dev.apposed.prime.spigot.util.mojang.MojangUtils;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter
public class ProfileHandler extends Module {

//    private Map<UUID, Profile> cache;
    private Cache<UUID, Profile> cache;
    private MongoCollection<Document> collection;

    private JedisModule jedisModule;
    private ServerHandler serverHandler;
    private RankHandler rankHandler;
    private PermissionHandler permissionHandler;

    private ExecutorService executorService = Executors.newFixedThreadPool(15);

    @Override
    public void onEnable() {
//        this.cache = new HashMap<>();
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .removalListener((RemovalListener<UUID, Profile>) n -> {
                    if(!n.wasEvicted()) return;
                    if(n.getKey() != null && n.getValue() != null && n.getValue().getPlayer() != null) {
                        cache.put(n.getKey(), n.getValue());
                    } else if(n.getValue() != null){
                        save(n.getValue());
                    }
                })
                .build();
        this.collection = this.getModuleHandler().getModule(MongoModule.class).getMongoDatabase().getCollection("profiles");
        this.jedisModule = this.getModuleHandler().getModule(JedisModule.class);
        this.serverHandler = this.getModuleHandler().getModule(ServerHandler.class);
        this.rankHandler = this.getModuleHandler().getModule(RankHandler.class);
        this.permissionHandler = new PermissionHandler();

        // Once every 15 seconds
        new GrantExpiryTask(this).runTaskTimerAsynchronously(getPlugin(), 0L, 15 * 20L);
        // Once every 3 minutes
        new ProfileSaveTask(this).runTaskTimerAsynchronously(getPlugin(), 0L, 60 * 3 * 20L);
        // Once every 10 seconds
        new PunishmentCheckerTask(this).runTaskTimerAsynchronously(getPlugin(), 0L, 10 * 20L);

//        this.collection.find().iterator().forEachRemaining(document -> add(JsonHelper.GSON.fromJson(JsonHelper.GSON.toJson(document), Profile.class)));
    }

    @Override
    public void onDisable() {
        this.cache.asMap().values().forEach(profile -> this.collection.replaceOne(Filters.eq("_id", profile.getUuid().toString()), Document.parse(JsonHelper.GSON.toJson(profile)), JsonHelper.REPLACE_OPTIONS));
    }

    // TODO: make another method for syncing profiles so i don't have to make so many database calls
    public void save(Profile profile) {
        executorService.execute(() -> {
            this.collection.replaceOne(Filters.eq("_id", profile.getUuid().toString()), Document.parse(JsonHelper.GSON.toJson(profile)), JsonHelper.REPLACE_OPTIONS);
            sendSync(profile);
        });
    }

    public void sendSync(Profile profile) {
        executorService.execute(() -> this.jedisModule.sendPacket(
                new ProfileRefreshPacket(profile, RefreshType.UPDATE)
        ));
    }

    public Profile load(Document document) {
        Profile profile = JsonHelper.GSON.fromJson(JsonHelper.GSON.toJson(document), Profile.class);
        if(profile != null) {
            profile.checkGrants();
            this.setupPlayer(profile);
        }
        return profile;
    }

    public void updateProfile(Profile updatedProfile) {
        this.cache.put(updatedProfile.getUuid(), updatedProfile);
    }

    public void setupPlayer(Profile profile) {
        Bukkit.getScheduler().runTask(getPlugin(), () -> {
            Player player = Bukkit.getPlayer(profile.getUuid());
            if (player == null || !player.isOnline()) return;

            Map<String, Boolean> perms = new HashMap<>();

            List<Grant> grants = profile.getActiveGrants();
            Set<String> permissions = profile.getPermissions();
            // TODO: scoped permissions
            grants.forEach(grant -> grant.getRank().getFullPermissions().forEach(permission -> {
                // scoped permission
//                if (permission.contains(":")) {
//                    final String[] permissionData = permission.split(":");
//                    final String scope = permissionData[0];
//                    if (!scope.equalsIgnoreCase(this.serverHandler.getCurrentServer().getGroup().toLowerCase()))
//                        return;
//                    permission = permissionData[1];
//                }

                if (permission.startsWith("-")) {
                    perms.put(permission.replaceFirst("-", ""), false);
                } else {
                    perms.put(permission, true);
                }
            }));

            permissions.forEach(permission -> {
//                if (permission.contains(":")) {
//                    final String[] permissionData = permission.split(":");
//                    final String scope = permissionData[0];
//                    if (!scope.equalsIgnoreCase(this.serverHandler.getCurrentServer().getGroup().toLowerCase()))
//                        return;
//                    permission = permissionData[1];
//                }

                if (permission.startsWith("-")) {
                    perms.put(permission.replaceFirst("-", ""), false);
                } else {
                    perms.put(permission, true);
                }
            });

            permissionHandler.update(player, perms);

            if (getPlugin().getConfig().getBoolean("nametag")) {
                player.setPlayerListName(profile.getColoredName(getModuleHandler().getModule(ServerHandler.class).getCurrentScope().getId()));
            }
        });
    }

    public CompletableFuture<Profile> load(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Profile> profile = getProfile(uuid);

            if(profile.isPresent()) {
                profile.get().checkGrants();
                this.setupPlayer(profile.get());
                return profile.get();
            }

            profile = this.fetchFromDatabase(uuid);
            if(profile.isPresent()) {
                profile.get().checkGrants();
                add(profile.get());
                this.setupPlayer(profile.get());
                return profile.get();
            }

            Player player = Bukkit.getPlayer(uuid);
            if(player == null || !player.isOnline()) return null;
            return create(new Profile(player));
        });
    }

    public Profile create(Profile profile) {
        profile.getGrants().add(
                new Grant(
                        this.getModuleHandler().getModule(RankHandler.class).getDefaultRank(),
                        PrimeConstants.CONSOLE_UUID,
                        System.currentTimeMillis(),
                        "Default Rank",
                        Long.MAX_VALUE,
                        Arrays.asList("Global")
                )
        );
        profile.setFirstJoin(System.currentTimeMillis());

        save(profile);
        add(profile);
        return profile;
    }

    // need to make sure to remove all grants with this rank from profiles
    public void delete(Profile profile) {
        executorService.execute(() -> {
            this.collection.deleteOne(Filters.eq("_id", profile.getUuid().toString()));
            this.jedisModule.sendPacket(new ProfileRefreshPacket(profile, RefreshType.REMOVE));
        });
    }

    public boolean isCached(UUID uuid) {
        return cache.asMap().containsKey(uuid);
    }

    public Collection<Profile> getProfiles() {
        return this.cache.asMap().values();
    }

    public List<Profile> getStaffProfiles() {
        return this.getProfiles().stream().filter(Profile::isStaff).collect(Collectors.toList());
    }

    public Profile add(Profile profile) {
        this.cache.put(profile.getUuid(), profile);
        return profile;
    }

    public Optional<Profile> fetchFromDatabase(UUID uuid) {
        if(Bukkit.isPrimaryThread()) {
            Bukkit.getOnlinePlayers()
                    .stream()
                    .filter(player -> player.hasPermission("prime.staff"))
                    .forEach(player -> {
                        player.sendMessage(Color.translate("&c&l<!> Database uuid query running on main thread. Contact a developer immediately."));
                    });
        }
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
        if(Bukkit.isPrimaryThread()) {
            Bukkit.getOnlinePlayers()
                    .stream()
                    .filter(player -> player.hasPermission("prime.staff"))
                    .forEach(player -> {
                        player.sendMessage(Color.translate("&c&l<!> Database username query running on main thread. Contact a developer immediately."));
                    });
        }
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
        return profileOptional.orElseGet(() -> create(new Profile(uuidRef.get(), username)));

    }

    public Optional<Profile> getProfile(UUID uuid) {
        return Optional.ofNullable(cache.getIfPresent(uuid));
//        return this.getProfiles().stream().filter(p -> p.getUuid().equals(uuid)).findFirst();
    }

    public CompletableFuture<Profile> getProfile(String name) {
        return CompletableFuture.supplyAsync(() -> {
            final Optional<Profile> profile = getProfiles().stream().filter(p -> p.getUsername().equalsIgnoreCase(name)).findFirst();
            return profile.orElseGet(() -> fetchFromDatabase(name));
        });
    }

    public Profile getProfile(String name, UUID uuid) {
        final Optional<Profile> profile = this.getProfile(uuid);
        return profile.orElseGet(() -> this.create(new Profile(uuid, name)
        ));

    }

    public List<Punishment> getPunishments(UUID uuid) {
        final List<Punishment> punishments = new ArrayList<>();
        this.getProfiles().forEach(profile -> punishments.addAll(profile.getPunishments().stream().filter(punishment -> punishment.getAddedBy().equals(uuid)).collect(Collectors.toList())));
        return punishments;
    }

    public List<Punishment> getPunishmentsByStaff(UUID staffUUID) {
        try {
            final CompletableFuture<List<Punishment>> futurePunishments = CompletableFuture.supplyAsync(() -> {
                final List<Punishment> punishments = new ArrayList<>();

                // db.profiles.find({"grants": {$elemMatch: {"addedBy": ""}}})
//                final MongoCursor<Document> profiles = this.collection.find(Filters.expr(Document.parse("{\"punishments\": {$elemMatch: {\"addedBy\": \"" + staffUUID.toString() + "\"}}}"))).cursor();
                final MongoCursor<Document> profiles = this.collection.find().cursor();

                while(profiles.hasNext()) {
                    final Document document = profiles.next();
                    if(!document.containsKey("punishments")) continue;

                    final UUID uuid = UUID.fromString(document.getString("_id"));
                    final List<Document> documents = document.getList("punishments", Document.class);
                    if(documents.size() == 0) continue;

                    documents.forEach(punishment -> {
                        if(!UUID.fromString(punishment.getString("addedBy")).equals(staffUUID)) return;
                        PunishmentType type = PunishmentType.valueOf(punishment.getString("type"));
                        UUID addedBy = UUID.fromString(punishment.getString("addedBy"));
                        long addedAt = punishment.getLong("addedAt");
                        String addedReason = punishment.getString("addedReason");
                        long duration = ((Number) punishment.get("duration")).longValue();
                        boolean removed = punishment.getBoolean("removed");

                        Punishment newPunishment = new Punishment(type, addedBy, addedAt, addedReason, duration);
                        newPunishment.setRemoved(removed);

                        if(removed) {
                            UUID removedBy = UUID.fromString(punishment.getString("removedBy"));
                            long removedAt = punishment.getLong("removedAt");
                            String removedReason = punishment.getString("removedReason");

                            newPunishment.setRemovedBy(removedBy);
                            newPunishment.setRemovedAt(removedAt);
                            newPunishment.setRemovedReason(removedReason);
                        }

                        newPunishment.setPlayer(uuid);
                        punishments.add(newPunishment);
                    });
                }

                return punishments;
            });

            return futurePunishments.get();
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<Punishment> getActivePunishments(UUID uuid) {
        return this.getPunishments(uuid).stream().filter(Punishment::isActive).collect(Collectors.toList());
    }

    public List<Punishment> punishmentsWithoutProof(Profile profile) {
        return this.getPunishments(profile.getUuid()).stream().filter(punishment -> profile.requiresProof(punishment.getType())).filter(punishment -> punishment.getEvidence().size() == 0).collect(Collectors.toList());
    }

    public List<Grant> getGrantsByStaff(UUID staffUUID) {
        try {
            final CompletableFuture<List<Grant>> futureGrants = CompletableFuture.supplyAsync(() -> {
                final List<Grant> grants = new ArrayList<>();

//                final MongoCursor<Document> profiles = this.collection.find(Filters.expr(Document.parse("{\"grants\": {$elemMatch: {\"addedBy\": \"" + staffUUID.toString() + "\"}}}"))).cursor();
                final MongoCursor<Document> profiles = this.collection.find().cursor();

                while(profiles.hasNext()) {
                    final Document document = profiles.next();
                    if(!document.containsKey("grants")) continue;

                    final UUID uuid = UUID.fromString(document.getString("_id"));
                    final List<Document> documents = document.getList("grants", Document.class);
                    if(documents.size() == 0) continue;

                    documents.forEach(grant -> {
                        if(!UUID.fromString(grant.getString("addedBy")).equals(staffUUID)) return;
                        UUID id = UUID.fromString(grant.getString("_id"));
                        Optional<Rank> rank = rankHandler.getRank(grant.getString("rank"));
                        Rank grantRank = rank.orElseGet(rankHandler::getDefaultRank);
                        UUID addedBy = UUID.fromString(grant.getString("addedBy"));
                        long addedAt = grant.getLong("addedAt");
                        String addedReason = grant.getString("addedReason");
                        long duration = ((Number) grant.get("duration")).longValue();
                        boolean removed = grant.getBoolean("removed");
                        List<String> scopes = grant.getList("scopes", String.class);

                        Grant newGrant = new Grant(id, grantRank, addedBy, addedAt, addedReason, duration, scopes, null, 0L, null, false);
                        newGrant.setRemoved(removed);

                        if(removed) {
                            UUID removedBy = UUID.fromString(grant.getString("removedBy"));
                            long removedAt = grant.getLong("removedAt");
                            String removedReason = grant.getString("removedReason");

                            newGrant.setRemovedBy(removedBy);
                            newGrant.setRemovedAt(removedAt);
                            newGrant.setRemovedReason(removedReason);
                        }

                        newGrant.setPlayer(uuid);
                        grants.add(newGrant);
                    });
                }

                return grants;
            });

            return futureGrants.get();
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Optional<Profile> identityIsPunished(ProfileIdentity identity) {
        return this.fetchByIdentity(identity.getIp()).stream().filter(profile -> profile.hasActivePunishment(PunishmentType.BAN) || profile.hasActivePunishment(PunishmentType.BLACKLIST)).findAny();
    }

    public List<Profile> fetchByIdentity(String address) {
        final List<Profile> profiles = new ArrayList<>();

        for(Profile profile : this.getProfiles()) {
            for(ProfileIdentity profileIdentity : profile.getIdentities()) {
                if(profileIdentity.getIp().equalsIgnoreCase(address)) {
                    profiles.add(profile);
                }
            }
        }

        executorService.execute(() -> {
            final MongoCursor<Document> profile = this.collection.find(Filters.eq("lastIdentity", address)).iterator();
            if(profile.hasNext()) {
                Profile loadedProfile = load(profile.next());
                if(loadedProfile != null && profiles.stream().noneMatch(p -> p.getUuid().equals(loadedProfile.getUuid()))) {
                    profiles.add(loadedProfile);
                }
            }
        });

        return profiles;
    }

    public Profile getProfileFromNickname(String nickname) {
        return this.cache.asMap().values().stream().filter(profile -> profile.getNickname().equalsIgnoreCase(nickname)).findFirst().orElse(null);
    }

    public boolean canChangeNickname(String nickname) {
        return getProfileFromNickname(nickname) == null && this.cache.asMap().values().stream().noneMatch(profile -> profile.getUsername().equalsIgnoreCase(nickname));
    }
}
