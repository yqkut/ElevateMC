package com.elevatemc.potpvp.kit;

import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.gamemode.kit.GameModeKit;
import com.google.common.collect.ImmutableList;
import com.mongodb.client.MongoCollection;
import com.mongodb.util.JSON;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.kit.listener.KitEditorListener;
import com.elevatemc.potpvp.kit.listener.KitItemListener;
import com.elevatemc.potpvp.kit.listener.KitLoadListener;
import com.elevatemc.potpvp.util.MongoUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import com.google.gson.reflect.TypeToken;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class KitHandler {

    public static final String MONGO_COLLECTION_NAME = "playerKits";
    public static final int KITS_PER_TYPE = 4;

    private final Map<UUID, List<Kit>> kitData = new ConcurrentHashMap<>();

    public KitHandler() {
        Bukkit.getPluginManager().registerEvents(new KitEditorListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new KitItemListener(), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new KitLoadListener(), PotPvPSI.getInstance());
    }

    public List<Kit> getKits(Player player, GameModeKit gameMode) {
        List<Kit> kits = new ArrayList<>();

        for (Kit kit : kitData.getOrDefault(player.getUniqueId(), ImmutableList.of())) {
            if (kit.getType() == gameMode) {
                kits.add(kit);
            }
        }

        return kits;
    }

    public Optional<Kit> getKit(Player player, GameModeKit gameMode, int slot) {
        return kitData.getOrDefault(player.getUniqueId(), ImmutableList.of())
            .stream()
            .filter(k -> k.getType() == gameMode && k.getSlot() == slot)
            .findFirst();
    }

    public Kit saveDefaultKit(Player player, GameModeKit gameModeKit, int slot) {
        Kit created = Kit.ofDefaultKit(gameModeKit, "Kit " + slot, slot);

        kitData.computeIfAbsent(player.getUniqueId(), i -> new ArrayList<>()).add(created);
        saveKitsAsync(player);

        return created;
    }

    public void removeKit(Player player, GameModeKit gameMode, int slot) {
        boolean removed = kitData.computeIfAbsent(player.getUniqueId(), i -> new ArrayList<>())
                .removeIf(k -> k.getType() == gameMode && k.getSlot() == slot);

        if (removed) {
            saveKitsAsync(player);
        }
    }

    public void saveKitsAsync(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(PotPvPSI.getInstance(), () -> {
            MongoCollection<Document> collection = MongoUtils.getCollection(MONGO_COLLECTION_NAME);
            List kitJson = (List) JSON.parse(PotPvPSI.getGson().toJson(kitData.getOrDefault(player.getUniqueId(), ImmutableList.of())));

            Document query = new Document("_id", player.getUniqueId().toString());
            Document kitUpdate = new Document("$set", new Document("kits", kitJson));

            collection.updateOne(query, kitUpdate, MongoUtils.UPSERT_OPTIONS);
        });
    }

    public void wipeKitsForPlayer(UUID target) {
        kitData.remove(target);

        MongoCollection<Document> collection = MongoUtils.getCollection(MONGO_COLLECTION_NAME);
        collection.deleteOne(new Document("_id", target.toString()));
    }

    public int wipeKitsWithType(GameModeKit gameMode) {
        // remove kits for online players
        for (List<Kit> playerKits : kitData.values()) {
            playerKits.removeIf(k -> k.getType() == gameMode);
        }

        // then the DB query
        // ref: https://docs.mongodb.com/manual/reference/operator/update/pull/
        MongoCollection<Document> collection = MongoUtils.getCollection(MONGO_COLLECTION_NAME);
        Document typeQuery = new Document("type", gameMode.getId());

        collection.updateMany(
            new Document("kits", new Document("$elemMatch", typeQuery)),
            new Document("$pull", new Document("kits", typeQuery))
        );

        return -1;
    }

    public void loadKits(UUID playerUuid) {
        MongoCollection<Document> collection = MongoUtils.getCollection(MONGO_COLLECTION_NAME);
        Document playerKits = collection.find(new Document("_id", playerUuid.toString())).first();

        if (playerKits != null) {
            List kits = playerKits.get("kits", List.class);
            Type listKit = new TypeToken<List<Kit>>() {}.getType();

            kitData.put(playerUuid, PotPvPSI.getGson().fromJson(JSON.serialize(kits), listKit));
        }
    }

    public void unloadKits(Player player) {
        kitData.remove(player.getUniqueId());
    }

}