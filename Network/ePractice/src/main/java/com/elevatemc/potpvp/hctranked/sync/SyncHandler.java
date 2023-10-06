package com.elevatemc.potpvp.hctranked.sync;

import com.elevatemc.potpvp.util.MongoUtils;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import java.util.UUID;

public class SyncHandler {
    private static final String SYNCS_MONGO_COLLECTION_NAME = "syncs";
    private static final String RANKED_GAMES_MONGO_COLLECTION_NAME = "rankedgames";

    public boolean isSynced(UUID uuid) {
        Document document = MongoUtils.getCollection(SYNCS_MONGO_COLLECTION_NAME).find(Filters.eq("minecraft", uuid.toString())).first();
        return document != null;
    }
}
