package com.elevatemc.potpvp.gamemode.kit;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.util.MongoUtils;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import com.google.gson.annotations.SerializedName;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.HashSet;
import java.util.Set;

public class GameModeKit {
    private static final String MONGO_COLLECTION_NAME = "defaultKits";

    @Getter
    private static final Set<GameModeKit> allKits = new HashSet<>();

    public static GameModeKit fetchById(String id) {
        Document document = MongoUtils.getCollection(MONGO_COLLECTION_NAME).find(Filters.eq("_id", id)).first();
        if (document != null) {
            return PotPvPSI.getPLAIN_GSON().fromJson(document.toJson(), GameModeKit.class);
        } else {
            return null;
        }
    }

    public static GameModeKit createFromGameMode(GameMode gameMode) {
        return new GameModeKit(gameMode.getName().toUpperCase().replaceAll(" ", "_"), gameMode.getName(), gameMode.getIcon());
    }

    public static GameModeKit byId(String id) {
        for (GameModeKit kit : allKits) {
            if (kit.getId().equalsIgnoreCase(id)) {
                return kit;
            }
        }

        return null;
    }

    @SerializedName("_id") private String id;

    @Getter private final String displayName;

    @Getter private final MaterialData icon;

    @Getter @Setter
    private ItemStack[] defaultArmor = new ItemStack[0];

    @Getter @Setter
    private ItemStack[] defaultInventory = new ItemStack[0];

    @Getter @Setter private ItemStack[] editorItems = new ItemStack[0];

    public GameModeKit(String id, String displayName, MaterialData icon) {
        GameModeKit saved = fetchById(id);
        this.id = id;
        this.displayName = displayName;
        this.icon = icon;
        if (saved != null) {
            this.defaultArmor = saved.defaultArmor;
            this.defaultInventory = saved.defaultInventory;
            this.editorItems = saved.editorItems;
        } else {
            this.saveAsync();
        }
        allKits.add(this);
    }

    public String getId() {
        return id.toUpperCase();
    }

    public void saveAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(PotPvPSI.getInstance(), () -> {
            MongoCollection<Document> collection = MongoUtils.getCollection(MONGO_COLLECTION_NAME);
            Document gameModeDoc = Document.parse(PotPvPSI.getPLAIN_GSON().toJson(this));
            gameModeDoc.remove("_id"); // upserts with an _id field is weird.

            Document query = new Document("_id", id);
            Document kitUpdate = new Document("$set", gameModeDoc);

            collection.updateOne(query, kitUpdate, MongoUtils.UPSERT_OPTIONS);
        });
    }
}
