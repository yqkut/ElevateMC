package dev.apposed.prime.proxy.module.rank.meta;

import lombok.Getter;

@Getter
public enum RankMeta {
    DEFAULT("Default Rank"),
    STAFF("Staff Rank"),
    SERVER("Bungee /server"),
    DONATOR("Donator Rank"),
    VPN_BYPASS("VPN Bypass"),
    IP_BYPASS("IP Bypass"),
    HIDDEN("Hidden Rank");

    private String display;

    RankMeta(String display) {
        this.display = display;
    }

}
