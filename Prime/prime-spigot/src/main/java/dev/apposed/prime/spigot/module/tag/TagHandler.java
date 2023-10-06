package dev.apposed.prime.spigot.module.tag;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import dev.apposed.prime.spigot.module.Module;
import dev.apposed.prime.spigot.module.database.mongo.MongoModule;
import dev.apposed.prime.spigot.util.json.JsonHelper;
import lombok.Getter;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

@Getter
public class TagHandler extends Module {

    private Map<String, Tag> tags;
    private MongoCollection<Document> collection;

    @Override
    public void onEnable() {
        super.onEnable();

        this.tags = new HashMap<>();
        this.collection = this.getModuleHandler().getModule(MongoModule.class).getMongoDatabase().getCollection("tags");
        for(Document document : this.collection.find()) {
            final Tag tag = JsonHelper.GSON.fromJson(JsonHelper.GSON.toJson(document), Tag.class);
            tags.put(tag.getId().toUpperCase(), tag);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.tags.values().forEach(tag -> {
            this.collection.replaceOne(
                    Filters.eq("_id", tag.getId()),
                    Document.parse(JsonHelper.GSON.toJson(tag)),
                    JsonHelper.REPLACE_OPTIONS);
        });
    }

    public void create(Tag tag) {
        this.tags.put(tag.getId().toUpperCase(), tag);
        new Thread(() -> {
            this.collection.replaceOne(
                    Filters.eq("_id", tag.getId()),
                    Document.parse(JsonHelper.GSON.toJson(tag)),
                    JsonHelper.REPLACE_OPTIONS);
        }).start();
    }

    public Tag getTag(String id) {
        return tags.get(id);
    }

    public void delete(Tag tag) {
        final Tag removedTag = tags.remove(tag.getId());
        if(removedTag == null) return;
        new Thread(() -> {
            this.collection.deleteOne(Filters.eq("_id", removedTag.getId()));
        }).start();
    }

    public static final String BASE_PERMISSION = "prime.tag.";
}
