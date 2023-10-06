package dev.apposed.prime.spigot.module.website;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.module.Module;
import dev.apposed.prime.spigot.module.database.mongo.MongoModule;
import dev.apposed.prime.spigot.module.website.announcement.Announcement;
import dev.apposed.prime.spigot.util.json.JsonHelper;
import lombok.Getter;
import org.bson.Document;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

@Getter
public class WebsiteHandler extends Module {

    private final Prime plugin = Prime.getInstance();

    private volatile Set<Announcement> announcements;
    private MongoCollection<Document> collection;

    @Override
    public void onEnable() {
        this.announcements = new HashSet<>();
        this.collection = plugin.getModuleHandler().getModule(MongoModule.class).getMongoDatabase().getCollection("announcements");

        refreshAnnouncements();
    }

    public void refreshAnnouncements() {
        this.announcements.clear();
        new Thread(() -> this.collection.find().iterator().forEachRemaining(document -> announcements.add(JsonHelper.GSON.fromJson(JsonHelper.GSON.toJson(document), Announcement.class)))).start();
    }

    public void saveAnnouncements() {
        new Thread(() -> announcements.forEach(announcement -> collection.replaceOne(Filters.eq("id", announcement.getId()), Document.parse(JsonHelper.GSON.toJson(announcement)), JsonHelper.REPLACE_OPTIONS))).start();
    }

    public Announcement createAnnouncement(String title, String postedBy) {
        final int id = getNextId();
        final Announcement announcement = new Announcement(
                id,
                postedBy,
                title
        );
        this.announcements.add(announcement);
        this.saveAnnouncements();
        return announcement;
    }

    public void deleteAnnouncement(int id) {
        this.announcements.removeIf(announcement -> announcement.getId() == id);
        new Thread(() -> this.collection.deleteOne(Filters.eq("id", id))).start();
    }

    public Announcement getAnnouncement(int id) {
        return this.announcements.stream().filter(announcement -> announcement.getId() == id).findFirst().orElse(null);
    }

    public int getNextId() {
        return announcements.stream()
                .max(Comparator.comparingInt(Announcement::getId))
                .map(announcement -> announcement.getId() + 1).orElse(0);
    }
}
