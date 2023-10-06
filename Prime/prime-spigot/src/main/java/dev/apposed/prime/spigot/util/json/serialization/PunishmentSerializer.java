package dev.apposed.prime.spigot.util.json.serialization;

import com.google.gson.*;
import dev.apposed.prime.spigot.module.profile.punishment.Punishment;
import dev.apposed.prime.spigot.module.profile.punishment.evidence.PunishmentEvidence;
import dev.apposed.prime.spigot.module.profile.punishment.type.PunishmentType;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PunishmentSerializer implements JsonSerializer<Punishment>, JsonDeserializer<Punishment> {

    @Override
    public JsonElement serialize(Punishment punishment, Type t, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        object.addProperty("type", punishment.getType().toString());

        object.addProperty("addedBy", punishment.getAddedBy().toString());
        object.addProperty("addedAt", punishment.getAddedAt());
        object.addProperty("addedReason", punishment.getAddedReason());
        object.addProperty("duration", punishment.getDuration());

        JsonArray evidenceArr = new JsonArray();

        punishment.getEvidence().forEach(evidence -> {
            JsonObject evidenceObj = new JsonObject();

            evidenceObj.addProperty("link", evidence.getLink());
            evidenceObj.addProperty("addedBy", evidence.getAddedBy().toString());
            evidenceObj.addProperty("addedAt", evidence.getAddedAt());

            evidenceArr.add(evidenceObj);
        });

        object.add("evidence", evidenceArr);

        if(punishment.isRemoved()) {
            object.addProperty("removedBy", punishment.getRemovedBy().toString());
            object.addProperty("removedAt", punishment.getRemovedAt());
            object.addProperty("removedReason", punishment.getRemovedReason());
        }

        object.addProperty("removed", punishment.isRemoved());

        return object;
    }

    @Override
    public Punishment deserialize(JsonElement element, Type t, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = element.getAsJsonObject();

        PunishmentType type = PunishmentType.valueOf(object.get("type").getAsString());

        UUID addedBy = UUID.fromString(object.get("addedBy").getAsString());
        long addedAt = object.get("addedAt").getAsLong();
        String addedReason = object.get("addedReason").getAsString();
        long duration = object.get("duration").getAsLong();

        Set<PunishmentEvidence> evidence = new HashSet<>();

        JsonArray evidenceArr = object.get("evidence").getAsJsonArray();
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

        boolean removed = object.get("removed").getAsBoolean();

        if(removed) {
            UUID removedBy = UUID.fromString(object.get("removedBy").getAsString());
            long removedAt = object.get("removedAt").getAsLong();
            String removedReason = object.get("removedReason").getAsString();

            return new Punishment(
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
        } else {
            return new Punishment(
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
        }
    }
}
