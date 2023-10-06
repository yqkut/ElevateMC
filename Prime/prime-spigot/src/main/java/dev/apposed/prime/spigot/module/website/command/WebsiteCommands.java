package dev.apposed.prime.spigot.module.website.command;

import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.Param;
import com.elevatemc.elib.util.TimeUtils;
import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.module.website.WebsiteHandler;
import dev.apposed.prime.spigot.module.website.announcement.Announcement;
import dev.apposed.prime.spigot.util.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WebsiteCommands {

    private static final Prime plugin = Prime.getInstance();
    private static final WebsiteHandler websiteHandler = plugin.getModuleHandler().getModule(WebsiteHandler.class);

    @Command(names = {"website announcement create", "announcement create"}, permission = "op")
    public static void create(Player player, @Param(name = "title", wildcard = true) String title) {
        websiteHandler.createAnnouncement(title, player.getName());
        player.sendMessage(Color.translate("&aSuccessfully created announcement."));
    }

    @Command(names = {"website announcement list", "announcement list"}, permission = "op")
    public static void list(Player player) {
        websiteHandler.getAnnouncements().forEach(announcement -> player.sendMessage(Color.translate("&c" + announcement.getId() + " - " + announcement.getTitle())));
    }

    @Command(names = {"website announcement info", "announcement info"}, permission = "op")
    public static void info(Player player, @Param(name = "id") int id) {
        final Announcement announcement = websiteHandler.getAnnouncement(id);
        if(announcement == null) {
            player.sendMessage(Color.translate("&cCould not find announcement."));
            return;
        }

        player.sendMessage(Color.translate("&aID: &f" + announcement.getId()));
        player.sendMessage(Color.translate("&aPosted At: &f" + TimeUtils.formatIntoDetailedString((int)(System.currentTimeMillis() - announcement.getPostedAt())/1000) + " ago"));
        player.sendMessage(Color.translate("&aPosted By: &f" + announcement.getPostedBy()));
        player.sendMessage(Color.translate("&aTitle: &f" + announcement.getTitle()));
        player.sendMessage(Color.translate("&aContent: &f" + announcement.getContent()));
    }

    @Command(names = {"website announcement set-title", "announcement set-title"}, permission = "op")
    public static void setTitle(Player player, @Param(name = "id") int id, @Param(name = "title", wildcard = true) String title) {
        if(websiteHandler.getAnnouncement(id) == null) {
            player.sendMessage(Color.translate("&cCould not find announcement."));
            return;
        }

        player.sendMessage(Color.translate("&aSet the title to " + title));
        websiteHandler.getAnnouncement(id).setTitle(title);
        websiteHandler.saveAnnouncements();
    }

    @Command(names = {"website announcement set-content", "announcement set-content"}, permission = "op")
    public static void setContent(Player player, @Param(name = "id") int id, @Param(name = "content", wildcard = true) String content) {
        if(websiteHandler.getAnnouncement(id) == null) {
            player.sendMessage(Color.translate("&cCould not find announcement."));
            return;
        }

        player.sendMessage(Color.translate("&c&lNOTE: &7It is recommended to edit the announcement's content via the database."));
        player.sendMessage(Color.translate("&aSet the content to " + content));
        websiteHandler.getAnnouncement(id).setContent(content);
        websiteHandler.saveAnnouncements();
    }

    @Command(names = {"website announcement delete", "website announcement remove", "announcement remove", "announcement delete"}, permission = "op")
    public static void delete(Player player, @Param(name = "id") int id) {
        websiteHandler.deleteAnnouncement(id);
        player.sendMessage(Color.translate(String.format("&aDeleted announcement %s.", id)));
    }

    @Command(names = {"website refresh", "website announcement recache", "announcement recache"}, permission = "op")
    public static void refresh(CommandSender sender) {
        sender.sendMessage(Color.translate("&aRecaching announcements..."));
        websiteHandler.refreshAnnouncements();
    }

    @Command(names = {"website announcement save", "announcement save"}, permission = "op")
    public static void save(CommandSender sender) {
        sender.sendMessage(Color.translate("&aSaving announcements..."));
        websiteHandler.saveAnnouncements();
    }
}
