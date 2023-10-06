package dev.apposed.prime.proxy.module.profile.punishment.evidence;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data @AllArgsConstructor
public class PunishmentEvidence {
    private final String link;
    private final UUID addedBy;
    private final long addedAt;
}