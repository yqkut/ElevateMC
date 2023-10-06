package dev.apposed.prime.spigot.module.server.filter.command;

import com.elevatemc.elib.command.Command;
import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.module.ModuleHandler;
import dev.apposed.prime.spigot.module.database.redis.JedisModule;
import dev.apposed.prime.spigot.module.server.ServerHandler;
import dev.apposed.prime.spigot.module.server.filter.ChatFilter;
import dev.apposed.prime.spigot.module.server.filter.ChatFilterHandler;
import dev.apposed.prime.spigot.module.server.filter.menu.ChatFilterEditor;
import dev.apposed.prime.spigot.util.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class FilterCommands {

    private static final Prime plugin = Prime.getInstance();
    private static final ModuleHandler moduleHandler = plugin.getModuleHandler();

    private static final ServerHandler serverHandler = moduleHandler.getModule(ServerHandler.class);
    private static final JedisModule jedisModule = moduleHandler.getModule(JedisModule.class);
    private static final ChatFilterHandler filterHandler = moduleHandler.getModule(ChatFilterHandler.class);

    @Command(names = {"prime chat-filter import-defaults"}, description = "Import default chat filter", permission = "op")
    public static void importDefaults(CommandSender sender) {
        if(!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(Color.translate("&cThis command must be executed through console."));
            return;
        }

        for(ChatFilter filter : defaults) {
            filterHandler.trackFilter(filter);
            filterHandler.saveFilter(filter);
        }
    }

    @Command(names = {"prime chat-filter editor"}, description = "Open the chat filter editor", permission = "op")
    public static void editor(Player player) {
        new ChatFilterEditor().openMenu(player);
    }

    private static List<ChatFilter> defaults = Arrays.asList(
            new ChatFilter(
                    "Restricted Phrase \"ip farm\"",
                    "[i1l1|]+p+ ?f[a4]+rm+"),
            new ChatFilter("Restricted Phrase \"dupe\"",
                    "(dupe)|(duplication)"),
            new ChatFilter("Racism \"Nigger\"",
                    "n+[i1l|]+gg+[e3]+r+"),
            new ChatFilter("Racism \"Beaner\"",
                    "b+[e3]+[a4]+n+[e3]+r+"),
            new ChatFilter("Suicide Encouragement",
                    "k+i+l+l+ *y*o*u+r+ *s+e+l+f+"),
            new ChatFilter("Suicide Encouragement",
                    "\\bk+y+s+\\b"),
            new ChatFilter("Offensive \"Faggot\"",
                    "f+[a4]+g+[o0]+t+"),
            new ChatFilter("IP Address",
                    "(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])([.,])){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])"),
            new ChatFilter("Phishing Link \"optifine\"",
                    "optifine\\.(?=\\w+)(?!net)"),
            new ChatFilter("Phishing Link \"gyazo\"",
                    "gyazo\\.(?=\\w+)(?!com)"),
            new ChatFilter("Phishing Link \"prntscr\"",
                    "prntscr\\.(?=\\w+)(?!com)")
    );
}
