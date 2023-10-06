package dev.apposed.prime.spigot.util.json.serialization;

import com.google.gson.*;
import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.module.tag.Tag;
import dev.apposed.prime.spigot.module.profile.Profile;
import dev.apposed.prime.spigot.module.profile.grant.Grant;
import dev.apposed.prime.spigot.module.profile.identity.ProfileIdentity;
import dev.apposed.prime.spigot.module.profile.punishment.Punishment;
import dev.apposed.prime.spigot.module.profile.punishment.evidence.PunishmentEvidence;
import dev.apposed.prime.spigot.module.profile.punishment.type.PunishmentType;
import dev.apposed.prime.spigot.module.rank.Rank;
import dev.apposed.prime.spigot.module.rank.RankHandler;
import dev.apposed.prime.spigot.util.json.JsonHelper;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Type;
import java.util.*;

public class ProfileSerializer implements JsonSerializer<Profile>, JsonDeserializer<Profile> {

    @Override
    public JsonElement serialize(Profile profile, Type t, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        object.addProperty("_id", profile.getUuid().toString());
        object.addProperty("username", profile.getUsername());

        JsonArray grants = new JsonArray();
        profile.getGrants().forEach(grant -> {
            JsonObject grantObject = new JsonObject();

            grantObject.addProperty("_id", grant.getId().toString());
            grantObject.addProperty("rank", grant.getRank().getName());
            grantObject.addProperty("addedBy", grant.getAddedBy().toString());
            grantObject.addProperty("addedAt", grant.getAddedAt());
            grantObject.addProperty("addedReason", grant.getAddedReason());
            grantObject.addProperty("duration", grant.getDuration());

            grantObject.add("scopes", JsonHelper.GSON.toJsonTree(grant.getScopes()).getAsJsonArray());

            if(grant.isRemoved()) {
                grantObject.addProperty("removedBy", grant.getRemovedBy().toString());
                grantObject.addProperty("removedAt", grant.getRemovedAt());
                grantObject.addProperty("removedReason", grant.getRemovedReason());
            }

            grantObject.addProperty("removed", grant.isRemoved());

            grants.add(grantObject);
        });

        object.add("grants", grants);

        JsonArray punishments = new JsonArray();
        profile.getPunishments().forEach(punishment -> {
            JsonObject punishmentObj = new JsonObject();

            punishmentObj.addProperty("type", punishment.getType().toString());

            punishmentObj.addProperty("addedBy", punishment.getAddedBy().toString());
            punishmentObj.addProperty("addedAt", punishment.getAddedAt());
            punishmentObj.addProperty("addedReason", punishment.getAddedReason());
            punishmentObj.addProperty("duration", punishment.getDuration());

            JsonArray evidenceArr = new JsonArray();

            punishment.getEvidence().forEach(evidence -> {
                JsonObject evidenceObj = new JsonObject();

                evidenceObj.addProperty("link", evidence.getLink());
                evidenceObj.addProperty("addedBy", evidence.getAddedBy().toString());
                evidenceObj.addProperty("addedAt", evidence.getAddedAt());

                evidenceArr.add(evidenceObj);
            });

            punishmentObj.add("evidence", evidenceArr);

            if(punishment.isRemoved()) {
                punishmentObj.addProperty("removedBy", punishment.getRemovedBy().toString());
                punishmentObj.addProperty("removedAt", punishment.getRemovedAt());
                punishmentObj.addProperty("removedReason", punishment.getRemovedReason());
            }

            punishmentObj.addProperty("removed", punishment.isRemoved());
            if(punishment.hasIp()) punishmentObj.addProperty("ip", punishment.getIp());

            punishments.add(punishmentObj);
        });
        object.add("punishments", punishments);

        JsonArray permissions = new JsonArray();
        profile.getPermissions().forEach(permission -> {
            permissions.add(new JsonPrimitive(permission));
        });
        object.add("permissions", permissions);

        JsonArray identities = new JsonArray();
        profile.getIdentities().forEach(identity -> identities.add(new JsonPrimitive(identity.getIp())));
        object.add("identities", identities);

        object.addProperty("online", profile.isOnline());
        object.addProperty("messagesToggled", profile.isMessagesToggled());
        object.addProperty("lastServer", (profile.getLastServer() == null ? "Unknown" : profile.getLastServer()));
        object.addProperty("lastOnline", profile.getLastOnline());

        if(profile.getLastIdentity() != null) {
            object.addProperty("lastIdentity", profile.getLastIdentity().getIp());
        }

        if(profile.getSyncCode() != 0) object.addProperty("syncCode", profile.getSyncCode());
        if(profile.getPassword() != null) object.addProperty("password", profile.getPassword());
        if(profile.hasActiveTag()) object.addProperty("tag", profile.getTag());
        if(profile.getChatColor() != null) object.addProperty("chatColor", profile.getChatColor().name().toUpperCase());
        if(profile.hasNickname()) object.addProperty("nickname", profile.getNickname());
        if(profile.hasStyle()) object.addProperty("style", profile.getStyle());
        if(profile.getFirstJoin() != 0) object.addProperty("firstJoin", profile.getFirstJoin());
        object.addProperty("playtime", profile.getPlaytime());

        return object;
    }

    @Override
    public Profile deserialize(JsonElement element, Type t, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = element.getAsJsonObject();

        UUID uuid = UUID.fromString(object.get("_id").getAsString());
        String username = object.get("username").getAsString();

        Set<Grant> grants = new HashSet<>();

        JsonArray grantsArray = object.get("grants").getAsJsonArray();
        grantsArray.forEach(grantElement -> {
            JsonObject grant = grantElement.getAsJsonObject();

            UUID id = UUID.fromString(grant.get("_id").getAsString());
            final RankHandler rankHandler = JavaPlugin.getPlugin(Prime.class).getModuleHandler().getModule(RankHandler.class);
            Optional<Rank> rank = rankHandler.getRank(grant.get("rank").getAsString());
            Rank grantRank = rank.orElseGet(rankHandler::getDefaultRank);

            UUID addedBy = UUID.fromString(grant.get("addedBy").getAsString());
            long addedAt = grant.get("addedAt").getAsLong();
            String addedReason = grant.get("addedReason").getAsString();
            long duration = grant.get("duration").getAsLong();
            boolean removed = grant.get("removed").getAsBoolean();

            List<String> scopes = new ArrayList<>();
            JsonArray scopesArr = grant.get("scopes").getAsJsonArray();
            scopesArr.forEach(scopeElement -> scopes.add(scopeElement.getAsString()));

            if(removed) {
                UUID removedBy = UUID.fromString(grant.get("removedBy").getAsString());
                long removedAt = grant.get("removedAt").getAsLong();
                String removedReason = grant.get("removedReason").getAsString();

                grants.add(
                        new Grant(id, grantRank, addedBy, addedAt, addedReason, duration, scopes, removedBy, removedAt, removedReason, true)
                );
            } else {
                grants.add(
                        new Grant(id, grantRank, addedBy, addedAt, addedReason, duration, scopes, null, 0L, null, false)
                );
            }
        });

        Set<Punishment> punishments = new HashSet<>();

        if(object.get("punishments") != null) {
            JsonArray punishmentsArray = object.get("punishments").getAsJsonArray();
            punishmentsArray.forEach(punishmentElement -> {
                JsonObject punishment = punishmentElement.getAsJsonObject();

                PunishmentType type = PunishmentType.valueOf(punishment.get("type").getAsString());

                UUID addedBy = UUID.fromString(punishment.get("addedBy").getAsString());
                long addedAt = punishment.get("addedAt").getAsLong();
                String addedReason = punishment.get("addedReason").getAsString();
                long duration = punishment.get("duration").getAsLong();

                Set<PunishmentEvidence> evidence = new HashSet<>();

                JsonArray evidenceArr = punishment.get("evidence").getAsJsonArray();
                evidenceArr.forEach(evidenceElement -> {
                    JsonObject evidenceObj = evidenceElement.getAsJsonObject();

                    String link = evidenceObj.get("link").getAsString();
                    UUID evidenceAddedBy = UUID.fromString(evidenceObj.get("addedBy").getAsString());
                    long evidenceAddedAt = evidenceObj.get("addedAt").getAsLong();

                    evidence.add(new PunishmentEvidence(
                            link,
                            evidenceAddedBy,
                            evidenceAddedAt
                    ));
                });

                boolean removed = punishment.get("removed").getAsBoolean();
                String ip = punishment.has("ip") ? punishment.get("ip").getAsString() : "";

                if (removed) {
                    UUID removedBy = UUID.fromString(punishment.get("removedBy").getAsString());
                    long removedAt = punishment.get("removedAt").getAsLong();
                    String removedReason = punishment.get("removedReason").getAsString();

                    final Punishment punish = new Punishment(
                            type,
                            addedBy,
                            addedAt,
                            addedReason,
                            duration,
                            evidence,
                            removedBy,
                            removedAt,
                            removedReason,
                            true
                    );
                    punish.setIp(ip);
                    punishments.add(punish);
                } else {
                    final Punishment punish = new Punishment(
                            type,
                            addedBy,
                            addedAt,
                            addedReason,
                            duration,
                            evidence,
                            null,
                            0L,
                            null,
                            false
                    );
                    punish.setIp(ip);
                    punishments.add(punish);
                }
            });
        }

        Set<String> permissions = new HashSet<>();

        JsonArray permissionsArray = object.get("permissions").getAsJsonArray();
        permissionsArray.forEach(permissionElement -> permissions.add(permissionElement.getAsString()));

        Set<Tag> tags = new HashSet<>();

        Set<ProfileIdentity> identities = new HashSet<>();
        if(object.has("identities")) {
            JsonArray identitiesArray = object.get("identities").getAsJsonArray();
            identitiesArray.forEach(identityElement -> identities.add(new ProfileIdentity(identityElement.getAsString())));
        }

        boolean online = object.get("online").getAsBoolean();
        boolean messagesToggled = false;
        if(object.has("messagesToggled"))
            messagesToggled = object.get("messagesToggled").getAsBoolean();

        String lastServer = object.get("lastServer").getAsString();
        long lastOnline = object.get("lastOnline").getAsLong();

        ProfileIdentity lastIdentity = new ProfileIdentity("");
        if(object.get("lastIdentity") != null) {
            lastIdentity.setIp(object.get("lastIdentity").getAsString());
        }

        String tag = null;
        if(object.has("tag")) {
            tag = object.get("tag").getAsString();
        }

        final Profile profile = new Profile(
                uuid,
                username,
                "",
                grants,
                punishments,
                permissions,
                tag,
                identities,
                online,
                messagesToggled,
                lastServer,
                lastOnline,
                lastIdentity,
                0L
        );

        if(object.has("syncCode")) profile.setSyncCode(object.get("syncCode").getAsInt());
        if(object.has("password")) profile.setPassword(object.get("password").getAsString());
        if(object.has("chatColor")) profile.setChatColor(ChatColor.valueOf(object.get("chatColor").getAsString().toUpperCase()));
        if(object.has("nickname")) profile.setNickname(object.get("nickname").getAsString());
        if(object.has("style")) profile.setStyle(object.get("style").getAsString());
        if(object.has("firstJoin")) profile.setFirstJoin(object.get("firstJoin").getAsLong());
        if(object.has("playtime")) profile.setPlaytime(object.get("playtime").getAsLong());

        return profile;
    }
}
