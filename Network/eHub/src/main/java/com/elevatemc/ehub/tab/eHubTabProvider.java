package com.elevatemc.ehub.tab;

import cc.fyre.universe.Universe;
import com.elevatemc.ehub.eHub;
import com.elevatemc.elib.tab.provider.TabProvider;
import com.elevatemc.elib.util.Pair;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.mojang.authlib.properties.Property;
import dev.apposed.prime.spigot.module.profile.Profile;
import dev.apposed.prime.spigot.module.profile.ProfileHandler;
import dev.apposed.prime.spigot.module.server.scoreboard.PrimeScoreboardStyle;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Optional;

public class eHubTabProvider implements TabProvider {
    private static final ProfileHandler profileHandler = eHub.getInstance().getPrime().getModuleHandler().getModule(ProfileHandler.class);

    @Override
    public Table<Integer, Integer, String> provide(Player player) {
        Table<Integer, Integer, String> layout = HashBasedTable.create();
        Profile profile = profileHandler.getCache().getIfPresent(player.getUniqueId());

        final Pair<ChatColor, ChatColor> style = PrimeScoreboardStyle.getStyle(player);

        layout.put(0, 1, style.getKey().toString() + ChatColor.BOLD + "ElevateMC");
        layout.put(1, 1, style.getValue() + "Online: " + eHub.getInstance().getGlobalPlayerCount());

        layout.put(2, 0, style.getKey().toString() + ChatColor.BOLD + "Website");
        layout.put(3, 0, style.getValue() + "elevatemc.com");

        layout.put(5, 0, style.getKey().toString() + ChatColor.BOLD + "Telegram");
        layout.put(6, 0, style.getValue() + "t.me/elevatemc");

        // Too big for 1.7
//        layout.put(8, 0, style.getKey().toString() + ChatColor.BOLD + "Twitter");
//        layout.put(9, 0, style.getValue() + "twitter.com/elevatemc");

        layout.put(2, 2, style.getKey().toString() + ChatColor.BOLD + "Teamspeak");
        layout.put(3, 2, style.getValue() + "ts.elevatemc.com");

        layout.put(5, 2, style.getKey().toString() + ChatColor.BOLD + "Store");
        layout.put(6, 2, style.getValue() + "store.elevatemc.com");

//        layout.put(8, 2, style.getKey().toString() + ChatColor.BOLD + "NameMC");
//        layout.put(9, 2, style.getValue() + "/namemc");

        layout.put(3, 1, style.getKey().toString() + ChatColor.BOLD + "Your Rank:");

        if (profile != null) {
            layout.put(4, 1, style.getValue() + profile.getHighestActiveNonHiddenGrant().getRank().getColoredDisplay());
        } else {
            layout.put(4, 1, style.getValue() + "Loading...");
        }

        layout.put(6, 1, style.getKey().toString() + ChatColor.BOLD + "Server Info:");
        layout.put(7, 1, style.getKey() + "Practice: " + style.getValue() + eHub.getInstance().getServerPlayerCount("Elevate-Practice") + "/" + eHub.getInstance().getMaxPlayerCount("Elevate-Practice"));
        return layout;
    }


    @Override
    public String getHeader(Player player) {
        final Pair<ChatColor, ChatColor> style = PrimeScoreboardStyle.getStyle(player);
        return
                "\n&8▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒\n" +
                "&8▒" + style.getKey() + "▟██&8▒" + style.getKey() + "█&8▒▒▒" + style.getKey() + "▟██&8▒" + style.getKey() + "█&8▒" + style.getKey() + "█&8▒" + style.getKey() + "▟█▙&8▒" + style.getKey() + "███&8▒" + style.getKey() + "▟██&8▒\n" +
                "&8▒" + style.getKey() + "█&8▒▒▒" + style.getKey() + "█&8▒▒▒" + style.getKey() + "█&8▒▒▒" + style.getKey() + "█&8▒" + style.getKey() + "█&8▒" + style.getKey() + "█&8▒" + style.getKey() + "█&8▒▒" + style.getKey() + "█&8▒&8▒" + style.getKey() + "█&8▒▒▒\n" +
                "&8▒" + style.getKey() + "█▀&8▒▒" + style.getKey() + "█&8▒▒▒" + style.getKey() + "█▀&8▒▒" + style.getKey() + "█▟▛&8▒" + style.getKey() + "█▀█&8▒&8▒" + style.getKey() + "█&8▒&8▒" + style.getKey() + "█▀&8▒▒\n" +
                "&8▒" + style.getKey() + "▜██&8▒" + style.getKey() + "▜██&8▒" + style.getKey() + "▜██&8▒" + style.getKey() + "▜▛&8▒&8▒" + style.getKey() + "█&8▒" + style.getKey() + "█&8▒&8▒" + style.getKey() + "█&8▒&8▒" + style.getKey() + "▜██&8▒\n" +
                "&8▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒\n" +
                "\n" +
                style.getKey() + "Welcome to the &lElevate Network" + style.getKey() + "!" +
                "\n";
    }

    @Override
    public String getFooter(Player player) {
        final Pair<ChatColor, ChatColor> style = PrimeScoreboardStyle.getStyle(player);
        return "\n" + style.getKey() + "store.elevatemc.com\n";
    }

    private static final Property STORE = new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTY1NjQyOTYxMjc3MSwKICAicHJvZmlsZUlkIiA6ICI0M2NmNWJkNjUyMDM0YzU5ODVjMDIwYWI3NDE0OGQxYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJrYW1pbDQ0NSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9iZDQ2NjJjODFhZjE2OGQ5MjZkODExYTAyZTdhZTYxOWViODM0OGNkZDU4OTRiNDE0MDI1ZDhiZThjZTA0MzUyIgogICAgfQogIH0KfQ==", "aMXPfoEJ57sqgnWz93KnlD7n+LEmgbc+tjhQZl4v6G7zW0Ad+jwirO5KJokZJtxNLrqL/3a+LmJBijsd08zHUWkpZWbZc6DUjxqV7sAHohpKhLIgDGG9ZXzkd34AtoUEhpWiDr5nthljhOcZxDbBgpGqJUJxAQxlI3/KBZQSGyXw2jrTep99YlrIzsfk9hEYGYbnjNa3BCXN7ua/gq6uq40rh7wMIhES7jBSAbwMgDuqoc0f3FkXbjuhBQl+rI7N+vj96GSAhQp4t8ijNvqHvtvM+OlgO7PmvGsq55U51nWWSTVw9ddsWLwj6edjAI48tQsO511BTk/6o5RJavCcx3CV3AtujG1Ovd8xKlGiZg06u02A21C3ViO/HaLMdXQqvD2Pl30R84fs3fUuWrKeD2ZUhBJU3VrBMB9Pu97KtuA8eoIgP32dhYHpSLEwYNl0DnCOmN7QDHIeo6cdc5ARdRZ6kaj2K+L8oflYRRV2QasgJRvspWzyLlqz8hnXiczptns1pyGe1aElCZA7iLIZpWfki/ljchu4+0bV0mOAIWtFqPj40YJE3Srp2eZqjmHIHh3xXs+DfU5IJ5NXEtyrJEp/8WDgQUZgiEc65U2Of+9xTZFgddlMaJwV243nvm/+H1qU1QgK2L27PcnZcF960sAhP/ZAd31D0nYPKcpRuwI=");
    private static final Property PLAIN_COLOR_STORE = new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTY1NjUzMDEwMzkyNiwKICAicHJvZmlsZUlkIiA6ICJmZTYxY2RiMjUyMTA0ODYzYTljY2E2ODAwZDRiMzgzZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNeVNoYWRvd3MiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2YzOTE5ZjUxNzg4NTlkZGMwOWQyZWU1YTlmN2RmZjVjODEyZTVhMjk2MjIwNjQ1N2E2Yjc0NmIwMmU0NzMwIgogICAgfQogIH0KfQ==", "o5+MVlyvH17ihr0/bL4QTO34ezmR5+zpWRsJkv8dNG/9rJpRnjifD6lMdI7+pBpvmesREc8/5i0RkvZme+/ASBfzOeGGA5ePUYEPaaXcK765GDo3DOctsbZ/rjPNrr0IlNZFopFzm3i9QILEZvqNISiw+yX2IVPjIa04RY4JJ3D2dCqo79MWqWkT/pj9V9XwApL8RTQhE6ZbofG/lVDtRBnhe78dYNfF4xIYVd8BxA2e7zi2Umoha5Mpzn2SPQv1mt+7j5WGoUllbkYKMqpFuSm29muxLsGwe4iI5Jr2nIrz5dofDlKw1zXQay1anGfqsYXKFwiCaFLQp4rQE8o1ScLNPwI5lGbSueprVd3wHXk+zeeg3XtkaJ+5qPujIWpUAsNtuT7bkCgK1TPBb7GeUKphFiyYRaqErMmmnMGiXdP9ikXwV+XLtdutgF+1fBlE5CgC6Xq3d+Ro7mcq6/qhYCIxhjWqwBxZJu5jS6IORH6EoPYu9XQIBgLtbtZMGn51anr6tu6Qp+9wYPUeY1fgzIZOsX9O6/RONjrVlbXn1sSBUFwOZ4iIJRxsnqbZUskiOs3iVA5sbbMN0nBLSMlMQdXroihzTsjQqERIxr4oEFmUkVeFOP18kPLjMHCMCo0VYhHs8AZw8BDlc8wKBlZlgo5FY1BpjVbn5K27HPQTI40=");
    private static final Property WEBSITE = new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTY1NjQyOTY3Mjk0OSwKICAicHJvZmlsZUlkIiA6ICJhMWU4OThjNWEyNmY0MTYyOTQyYmNmNmM1MzRiMTE5ZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJ0b21keGlpaSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9hNGQxY2RhODQ4Y2EzZGEwZjg4NTNhYzA4ZmFiYzIxMWE4ZGFhZjEzNmNmZTllMTdiZDI2MTAwODE3ODdkOWM5IgogICAgfQogIH0KfQ==", "fHDjx3C6Ls7FvIqRKk0uW0YoSgE1kc/yUUARTESPovCFnuVcq7QAI8CM+ayuplDW7RyokwxYLTajh6dPDuAnlpSnvUPyZDN8boI7jGP48lIte60fu4m4isLHuGRsZynb+neRunw4Edn833tXKhoPbMqtv+hQddaMrKEk5Cu6npD52k3/AaZJ0Q8JwHCmvSZjyJP7DqmHYuTRQl2PWTFZagx+VXDiq4H3HLROnlWQWn3Ef5gNezmxj/5UuOsvN/DP5aRpexxyCYFk5giU6qgJxxmY1LwCP5tiOC62+y8CxKBZA3Hq5o7HViwo/4agNaJFgJyHFgvh3FUkGc7NNsKA3bIbB/ksW3s6G/ArW9N98K1fUEyaRZJHC2OjSM4BBK2oQjJU1Q1yLcBeIU5SVgx2/rhhxLJaFIhvWhHPhDkGWU0MZ7YF1/ibxOgu83FhlKsOvykSqoFYcZkImjFTQUTJo5eA3tq5BvMeBdZMqGyr/v3qMoFCQ5N+xcD+EIZdjv1HeUs8u3LsjsipxCeWcapc9CI1HJ5z2mQCTowU3p3YqTC44y6OZacswL/BmIrzPkEhf7+UJjZke1eFpg+DAXlVx58m9JDHHH/s8iCUpF8yQ2++y+ddOuvPT7SynBaNdp/wr1Oqp7c46c6rVAQyREi/Q0LGMD4QzD6+0tX4hhqUzr4=");
    private static final Property PLAIN_COLOR_WEBSITE = new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTY1NjUzMDI3MzgxMywKICAicHJvZmlsZUlkIiA6ICIzOTVkZTJlYjVjNjU0ZmRkOWQ2NDAwY2JhNmNmNjFhNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJzcGFyZXN0ZXZlIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzkxYzIxZWFmOWYzYjVhMTg2OWM3ZDY0NTM4NWQwZDVhYTRjZmUyNGE4NzRmMzViOThkOGFhMDBkNDBlODc4ZTAiCiAgICB9CiAgfQp9", "pdORfhGepV7FkwDpY6/zEpemALAU2pmXoNgaQ6P1hfVoyfjbDeZSHsWKRDvJbqNVg4yuoLuydksWewv2M9EIqqx7v2/X3UDJdN4uqTMjZ7ZENILTg8m9x34WVfyEORaPnPsnUn2sJYT4FukUvaoMQKk+hJU4uoAoufbhOmRfp3IMI84Uc7ZNxHrEYSyrOl6VxLD+HypQecLVc0uiCo0W9oI/Ryu/jo0AJV5+lYHPq4Tvmy0yQpDRqHFMBoqlZhwia8sYAWtMlRPAAauFsKY4m9bPM+F5wyIQvvkMQiOcs5XypU3Tssdk/AM8X9o/0yv85mXq9vbpFUEOBCRUyRVhBMSrTSyrZt/MVSi4LNsYMZ+FYHolU8h8Dyc5XtkoKILARNKHKA4lg8k3kPjeRzHmFjy8TklPRgfhNgVo+3MQ+vLkqVPOVslkKeGZT9QDMpqOj69fXBp6O5diwMuOyU3g/XZdErdnKEvWxXgZZxPh0ej+nZtDDOdFCgvjJFeT92Qrv2Mua7yvI98MyxKHDgRzl1aDCVEh14aM3CQ0QTFH8uswiKzM8mP6O2a0sSUh+xWvuqi6AKsig64W+wDHmhAuJcFwftf71g2wmOgtW0roaZoxH74TISn8Vzw43nNA2OWQ5RNpBVv9EnQFLjzTxwvM/X/AAAQ2NTo//V4w6/mAXoQ=");
    private static final Property TEAMSPEAK = new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTY1NjQyOTgyMDExOSwKICAicHJvZmlsZUlkIiA6ICIwYzE1OTI3Yjc4OTY0MTA3OTA5MWQyMjkxN2U0NmIyYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJQYXkyV2F5IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2RlNGI2MzQ5OGU3MTM0NmYzMGQwY2NkODcwMDliMDk2YWVkZTgxM2U2ODMwZjFmMjY4MjQ1OWE5OTU0MWIyZmYiCiAgICB9CiAgfQp9", "fY9msPrtguvXdzYkpBhEkYKm8hDq4ODdxLtWFeSoKi9Mj8Of0Z+eDRfw1tg4PvdPpg6QsNUEH86o+2vvF/ys8n9SfpYBHlM562KuSjDJmoDsxGja6Yq0BPRLC8bd0l936bhL0++FdPmFxKc1OYHpAKjmUJsS9oXFir+WwBKUXi/khqgdPbMzzxbCzUezDLsxdBZQ5dsjhAApEfd/Ajss4UwFdjqHG+cftbFMzqe+1IsuUwqHsfl6vRrjRbFYl8BMEfPLNkn/jIusYAqmYbPyyPeIZ2CjMjSnfDsxGHxI3zsaE07ee9lbZGaTysCV/vDmUz3t3jMxJkMS8pC23HikOAJE5ERJwBbA2NRYANMPAjR6RxFLimW+yaaKu87kUrIlTSrSKtbA4cAoXMSWB1lisz/tob3V0HCPQXpazMjTu4ICMTendw7LhJ42KHPGnqxjVzY1ipZVIXOv0W2WsvJLbVG3BZwTJNDUkdC4etSmvDMUUZmpUQdwBRsnJCYe2Ep4x14qb9j9khdmp5KKcOs1JyNsIrmgCLRKCuH+OSc1thrqk4sDwjpHGma7buHbEtmCGeAFjZxDHoPEHf3yhRWIgoKPe6K1h3ztozNmlcbimPUpgUdh5bVeMLkycGBRz9ydS4PHjPJ1y/AjRkpnbvNzH3lCtK6hoF/vz+PSWajbTwM=");
    private static final Property PLAIN_COLOR_TEAMSPEAK = new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTY1NjUzMDMzNDUxOCwKICAicHJvZmlsZUlkIiA6ICJmMjc0YzRkNjI1MDQ0ZTQxOGVmYmYwNmM3NWIyMDIxMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJIeXBpZ3NlbCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jZjk5YWRmMDM3YWNkNDAwMzQ0ZjhhZmRkZmE1ZTQ5ZDM5YmMzNmViZGIzYWQ4ZWM4MzkxOTA5YzU2OTM5ODYwIgogICAgfQogIH0KfQ==", "dCJqNYmwS7zLm9fl+G8riCuXLeznTT3elmzlhXmiv6iwzpZ3oa/tY4fv/hotYTiaXCY0KlPPVmCQC+88T3Gs80nPxBbe+nFfjsYGXG3JU1dGbF66cqpnZuhb0P38FcSdizdZMynYcVaUtGn7JUY9YrdMfuhGOemRmJtlEb7pShyuNQ34AVUCp85pDxjyj+JtAYVQUWWD+P/PU/C+ga3SMtIdNl9fCze4rDShMYO/RmHQgCo8oMlHdzUhgKDMxNgJXWv9SgHmn0VqZOxVs8cvkd3xpC9m2/aowgeuCcsUlVZ8Klmymf5IXsePgE92gJNEUP9l2txSwnfOjnHae5AgdmX2/h8l3PYkHQ7Afi1ceNMIm/E1YR+kuMkD7gAYtvFpLkLocX8n29dArLqw1s5TeogXpe6GB+PwvSWGK3Yu980ZCoMu6sAbyAPyYOxVnSr2gpCZ66QZwEOpRWIwCVEDQ72Cr2Xr99ITAU1ffDTP8M2crPd7VvR57rxyda6mH77Pml+KwcOrosFUdMx3ZyVX91n41MgFl3bIT/guoToXHgg52dQLrWOaTdWMqp/SprtUcocI953m0vQvo1krfC9SraKaOyiSxoiE3agA2FL4iu5kj8T9dFCkro/dSgjorgw9eg9+7Vk+GJmgFaf7KLAgornlxlNtl6KQagfQ2r3ApRk=");
    private static final Property TELEGRAM = new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTY1NjUyMjAyMDM5OCwKICAicHJvZmlsZUlkIiA6ICIxNzU1N2FjNTEzMWE0YTUzODAwODg3Y2E4ZTQ4YWQyNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJQZW50YXRpbCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS85YTk1ZDZlNDA3ODI4YTM0MDI0NzA4M2E5YTNjOWY1MDQyMDBkNzk3ZTNmYmI0NjFmOGIxNzNkN2NkZjIzZWZmIgogICAgfQogIH0KfQ==", "ETIO4ghhnBoL88HsoFOS+SDSrIlwRZ2CSUJ+Ki+ULtW4PKqBNcTs9Tkug9eYLph6Zsd9XHzvRZIOCka0d9N9RYtTNL262lMJj1S6mKJGhFC3+n14rn2uvlZliN5nYdZH/U0NaszvZ926jnE9GH6M9VBGYnN8Ax5uLkm6QQUbBZABce43KmEi5q1cvJZKAHS+yub2fmv2qfv/S7NLAfcATQquVLTmwVj9IJc8QQJXvbvB28ar1F1IUKIvxGjcFgr5/Oq4YjSgBv7Ge6oJYxvI7fYnOwwOsyYcimXV5NookI8UiAunSpjtYDByN85eSPjCDjUu/gO6F97fWDFJh52b2FAmsFU87pGDDlEiaca6Ku9LILPRVf9923f6duMXZGATGeTPswigaIjiSmXDtyEjLv+COP9G+zul9yvK4M0fFJH7iEig0xKj7ZNGQb4QN25v0LEwbygmYm/CXoWJOhPf69vlx01nlGNweAR4U3tZ5C31mrUIIsIC6xGBCoy0QHoS0rAhEz58mSqthqibjU2n8UbTxL3Cytz0kraY9ZsdVHEPFRJx7/lfB2+/7mB1JjMYwzxNwTKYsjze2bjr3rAmm+tViAsDMbhRROKrm9rQfFnsS4vW9n32OTvfXkGyQ1TbPamz7T/eesWz/1FbmSI8l6/MCf7SwKTgqHaTJOPyPyU=");
    private static final Property PLAIN_COLOR_TELEGRAM = new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTY1NjUyOTk3MDE5MiwKICAicHJvZmlsZUlkIiA6ICI4NTRhYTNiNDVkZDk0OTNhYmZiZThkNWU4MTBjNmMyYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJERERhdmlkYSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jZDRiZmNjY2Q4OTExYzFjOWQxMDI3ODA2YmY4OWMyZTJiYTQ1ZDAyM2Y3OTJlZDE0MzZkMTFjM2Y1NzYyM2MxIgogICAgfQogIH0KfQ==", "OHARxHfRAffpuL6qojoK56h5Hb9zTKTaYDtRaexQ7Zxd3HGp8DsF4zmdGLTdARtu7eDeY6zYrP7uZBgTPxldz0Dy6a/FdJtnt76hMVJpZFExC21B2F4e6l50Zpb8NNDkfIr/7jzHDiwSG4lG/hWNLbZU+cl1mF4yRapUG7IDCSkO1Zqsv/PcxjWIobNZNaGnJiW5/Q5RthtyoLyEyL8V1P9V+WYDHBHBdjSQDniyBWLbuTblILuh/YzPfCb99f3D95qcIoI/U/D4nrZVGqRZe9V7iSAf2lc2y9/Hd0tcYZFQ7M7D6iXbT+ewflhlO26dAe/jLWN0sIzxlrftl5OiKvMX78ssajGzOzz4QmXHB+CYCSYZtwCgo8Yv7oxKP3U6NUcAjXW/IMcIS5QvcXGNzERuyRlfcYrU4gvWv2NCXHCkMM8cQ5fQmolrljFTLt99lGqHLUDfDAPTtmHBJ6SG+lt5Ux6RhkiLyx4bBf/7rA7MtRF8YiNCIx3rmSXYBoW8eNJh32O8F3SWXX39Ir+MqM9wdtA4BlWJc6CNY/C4SNKo4agtY5556+blIs9Kvu3CfmHEKZtVn++bdGceCFWnbG1S9TjuyK/o6gx2V8SJlg2gvYmfPywgVbEfthV3jUasmYLdwxkjQCxJlAO2Nei0bWXL1cvK5+96rodJHLgCySU=");
    private static final Property ELEVATE = new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTY1NjUzMTQxNjY5NSwKICAicHJvZmlsZUlkIiA6ICJiMjdjMjlkZWZiNWU0OTEyYjFlYmQ5NDVkMmI2NzE0YSIsCiAgInByb2ZpbGVOYW1lIiA6ICJIRUtUMCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zZTBkMDdhZmM4YjlmZGIyODVmOTA2NDcwNjgyZDQzMWE2ZmFmMDYxYmQwYTlhYTJlZGViMzIzNzZhMTE4YzYyIgogICAgfQogIH0KfQ==", "FWcUxMtAQih7OE4CC0A5jtrxPkwWmtr32kloEj7L1lQ7RDgEliCeMHsb/tUWWHpWKwczllEnZJ1sNAqJ8XcW3A2+INELThapQAu/stPP+hOEcsZf4/DrJeTgIWlkkUyr1uT45bgFCllBsl0Jx4YI+36ZplC5B0Ci34fjJrQvOHidswYoEf/yYUOLI2b3dngofmi4t69Ec87g9xOHoEeKhMIPLup+wl7an5RrLxb+G7+G3daufZ792a1o3kju+mBYV32fAA4D1/LXweanGaoFJGN2PN8tnydlGWXXcth38EjFR3Vh7Xq11JaU8/JWonpkl+R6RU16aWdXu/inKtMhfiS8wqUkkQPCDHgLx2zdkRcz7aD4medM9cl3sPSrsNDwL4sRHZY4Emu7mlfSyG9QlOhWhdYkQdHk3usaHvGMQem1f5JYRLUL4PhGhqOiKLZ5oOCPUcbTSSyXnCOkPReaByiOOweAZp0gM/oqhGRpRz90i+m44P8JnT8VYvs8vWF+CxRuPXMESCKD0ebtFZcPKNjMUYzx9DuaoOg+UxBPLKGAjob9e6m09ayde4d08zAXUXYQdE/NGP2xPWtcl8N1ZJWJMXMKeC5fjhzV70EqJIcS73L++aaY8gNZrdXBZiR0oKcxjrOC4OV06dnD5+inGF0RkxfG9omYLvtHl9Vxoyk=");
    private static final Property RANK = new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTY1NjUzMTMyNzUwNiwKICAicHJvZmlsZUlkIiA6ICIwNTkyNTIxZGNjZWE0NzRkYjE0M2NmMDg2MDA1Y2FkNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJwdXIyNCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lYzM4NzA5YThmYWNkOTI2M2EzNDMzOGZhYTQ2ZDk2YWUwYmExZmM4MTQyNTllMTNjYTc4YTY4ODkyOTkwMGZkIgogICAgfQogIH0KfQ==", "thV8esXRHwzH4C/2RoiPahhSNlpssRFpWZI38S3QWiJWBdeuPvKxYj9eb+Znh0nRPF4WS4e/kfL0Zq6B3XzRbgBMgv/Uoy/5oDmrEWOIAloBrTCsdmoXvg9roPj2J5TaasExpuZE5xWKNenA0f2DyGdcHdC5uJausgId6iGR4N+6XmkV+BChlI6OTMsNwdeslgWTU/d7XVclHqE3Ji/oyZnv1JX1sIzzvzuqxF3tCtnOYJybsLlR+1H7kAfbRm7v29FXafHlNHOtRHo1oSZH/PUz2ZFClBlUuIyV0eF4URoYaTrb4+a04NMdohiwb9/1Ot4Fd2MgD4Q40V+WP7foa6hTOE4WMMKrxLWqZ7KK+QF7sdvD8XTGW+ZFscSrobUhW7bCdDjNrf/s43/iL2Wp50m+DpQHnVN5Wz2gm696q9rkbPNNSCS82NVLCRdHZMEs/KdJBJLSYQB/OmYCT985PutdgSqsEeUE6YUemVNbC/bnsvIMgV0DOCCTfrLdGnSRZ+epCUTXU3CjL11lTwKjSemY5xjaxJX1pVTqekgiwjPb+Eqk5+lIQOC30nnGQR6qMQPNLda5AZzi2qtpMnCHqRj2CXGtE8L2fWuWbess+Ip9D1F5kDzD7DwkJQbM2lbigbrE7vDfMo35I7pUdsZd0KJHPAbpb0D+nyl/WxQNFyg=");
    private static final Property SERVER_INFO = new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTY1NjUzMDU2MTYxOCwKICAicHJvZmlsZUlkIiA6ICJkOGNkMTNjZGRmNGU0Y2IzODJmYWZiYWIwOGIyNzQ4OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJaYWNoeVphY2giLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzUwMDcwMGY2Y2ViNDQ4YmY5ODc4NmQxYzVkZDNkZTQ1MWNjMWNhZjA2MzBiMjE5N2I2ODk0ODMzZTE0MGQ2IgogICAgfQogIH0KfQ==", "k2uc3xXRsktxkKc3oxnsw2i53Cxa3l0eNu9dey0sG1DStJIjQALsT5cpSnPclbxN/KbS/me7zL15MG3Zn9lv/fEN0npW9M+DECEG6C7CbActEZoUb1/3ulc2MskQkPIlmSDu9eK+MCQ9w25qPlYHcYpWuYYeiXUTpCbnBzMoh414jYa2xjlTm4ATsCGf9uJkmFR1AW+nbVEyYTLfQdNTBUzjEmUJNsyG4UgBP/wPoagUHJapS6hqP8/hh2dhrRCeSm+YSHdEiusJ4uv5sB3lGQtjiDuWhgIa2Sc51m0YegCRPvo+wFtpN9KrM9nsD4mHfs0ZVWHc5Poxpw/A9Q2MByzXrmJA4mCtuayrOiDdszYyWg9K4CfHQV/RGQ/V2iRNqs/WjSjwGfqhxnn2BdCgxoQvGNw2Yul6Km76VYsgKjMTRJl8xk0XQkcq4bjiqraMRc/yyZUL9LOEm6icJ7/UDWyCyYZfYTN/sHg4OHbDtgW16O0XmgNj3N2Hly1IzwlLMHz748ogagycXIFpEKWoFalW3QFh4tEk/RvoZWCKSl9I2GPQkPkaXWDvv1mEL0wml9268R0AqZT6VRwkHnLwbtJU3CpwVFX+c8VN4OL0Je9K2uYs6yHb/DpMOpTf/5JYAGQUOkVnpbsUca4MdYZjSkU4CtcCC3ushSNtRXeOB0Q=");
    private static final Property INFO_PLAIN = new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTY1NjUzMDY0NDI5MSwKICAicHJvZmlsZUlkIiA6ICI1NTZjNDNmOWJmZjU0MjI1OTI0NDU5M2EwN2QyYzE1MSIsCiAgInByb2ZpbGVOYW1lIiA6ICJHaW50b2tsIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzkxMDFlZWMyNjI5NzNhM2UxMjk4NTc1MDY5NjgyODUwMTk0ODBjODhjOTgxODRkYzc0NTg4NTU0MzlkZGNjYjAiCiAgICB9CiAgfQp9", "kSE1tZTqtjPudXxGzbpDPrapuf0hDb6aP1gXoItg3Kki8SFCPoK1UGgcpoFlkDHrdB5K0r/JR9DBzCr2OfmuL5A8W/fKl/XjUa+hQZs1cak68tkyCAc5VaaU8wkfABO7S4yVooPFpB3rJ+O4i6bk0zNNj/ig0sDoXAWzF0Cf6Q0EDN7cvaiGGJT5zhZCPyh+VVuM4t6poEmaCsoOJdUIhJAvEqS91q2ar8x847uLuA+Rz0qux6w6UY3QpzX9DtCluVS4DUXv69N1tCvzBGJQfqRR3PAaSBTM/rwNBvej+yr84FHOvUHbn14P4pZefLYVXvXZcYUqwTk2jXqF+nt5LNQaHhYWPolNn7lRrk5nMHNUVFt8PLfURqGMAgZeA1iNdegK/517d6ALYJC+969ZaZOJ7bcsSLJwKwZHsPo/5zWIMaNMBYUoPHzSdD7ZWZy4YWbohbmLE9a5Vf1wF5CfZORm6pyQQnRJUw8f3yEQcslQe1yvzVrdxW7d1dBTuScGv2I8C7n/rSuklWv+r+42cvilY7VvaITlybnJyNNF9ro2oNtPfhSc5LwNpBbPq/P9f0K8bJiMPV0cPSN56XXZVQuNCZ1NV04C3WwZIo3Zsxj5wu4NCVrWKXlOQwXEgw/xCAgvzgiM/gnWWKyHmenZhYGAa7vJWxTxHMeR/w2j/H4=");

    @Override
    public Table<Integer, Integer, Property> getHeads(Player player) {
        Table<Integer, Integer, Property> heads = HashBasedTable.create();

        // Website
        heads.put(2, 0, WEBSITE);
        heads.put(3, 0, PLAIN_COLOR_WEBSITE);

        heads.put(5, 0, TELEGRAM);
        heads.put(6, 0, PLAIN_COLOR_TELEGRAM);

        heads.put(2, 2, TEAMSPEAK);
        heads.put(3, 2, PLAIN_COLOR_TEAMSPEAK);

        heads.put(5, 2, STORE);
        heads.put(6, 2, PLAIN_COLOR_STORE);

        heads.put(0, 1, ELEVATE);
        heads.put(1, 1, INFO_PLAIN);

        heads.put(3, 1, RANK);
        heads.put(4, 1, INFO_PLAIN);

        heads.put(6, 1, SERVER_INFO);
        heads.put(7, 1, INFO_PLAIN);

        return heads;
    }
}
