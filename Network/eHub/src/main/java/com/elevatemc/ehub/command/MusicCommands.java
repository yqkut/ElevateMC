package com.elevatemc.ehub.command;

import com.elevatemc.ehub.eHub;
import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class MusicCommands {
    @Command(names = {"music skip"}, permission = "music.skip")
    public static void skip(Player sender) {
        eHub.getInstance().getRadioSongPlayer().playNextSong();
        sender.sendMessage(ChatColor.GREEN + "Skipped this song!");
    }

    @Command(names = {"music list"}, permission = "music.list")
    public static void list(Player sender) {
        eHub.getInstance().getRadioSongPlayer().getPlaylist().getSongList().forEach(song -> {
            sender.sendMessage(ChatColor.YELLOW + song.getTitle());
        });

    }

    @Command(names = {"music play"}, permission = "music.play")
    public static void play(Player sender, @Parameter(name="song-name", wildcard = true) String name) {
        Song song = eHub.getInstance().getRadioSongPlayer().getPlaylist().getSongList().stream().filter(s -> s.getTitle().equalsIgnoreCase(name)).findFirst().orElse(null);
        if (song == null) {
            sender.sendMessage(ChatColor.RED + "Could not find that song!");
            return;
        }
        eHub.getInstance().getRadioSongPlayer().playSong(eHub.getInstance().getRadioSongPlayer().getPlaylist().getIndex(song));
    }
}