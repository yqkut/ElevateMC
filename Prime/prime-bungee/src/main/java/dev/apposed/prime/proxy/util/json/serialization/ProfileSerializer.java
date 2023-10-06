package dev.apposed.prime.proxy.util.json.serialization;

import com.google.gson.*;
import dev.apposed.prime.proxy.PrimeProxy;
import dev.apposed.prime.proxy.module.profile.Profile;
import dev.apposed.prime.proxy.module.profile.grant.Grant;
import dev.apposed.prime.proxy.module.rank.Rank;
import dev.apposed.prime.proxy.module.rank.RankHandler;

import java.lang.reflect.Type;
import java.util.*;

public class ProfileSerializer implements JsonDeserializer<Profile> {

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
            final RankHandler rankHandler = PrimeProxy.getInstance().getModuleHandler().getModule(RankHandler.class);
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

        Set<String> permissions = new HashSet<>();

        JsonArray permissionsArray = object.get("permissions").getAsJsonArray();
        permissionsArray.forEach(permissionElement -> permissions.add(permissionElement.getAsString()));

        boolean online = object.get("online").getAsBoolean();

        return new Profile(
                uuid,
                username,
                grants,
                permissions,
                online
        );
    }
}
