package com.notpatch.nlib.builder;

import com.notpatch.nlib.NLib;
import com.notpatch.nlib.manager.CooldownManager;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Builder
@Getter
@Accessors(chain = true)
public class CooldownBuilder {

    @NonNull
    private final String name;

    @Builder.Default
    private long duration = 30L;

    @Builder.Default
    private TimeUnit timeUnit = TimeUnit.SECONDS;

    @Builder.Default
    private String onCooldownMessage = "&cBu komutu tekrar kullanmak için {time} saniye beklemelisin!";

    @Builder.Default
    private String cooldownFinishedMessage = "&aCooldown süresi doldu! Artık tekrar kullanabilirsin.";

    @Builder.Default
    private boolean notifyOnFinish = false;

    @Builder.Default
    private boolean persistent = false;

    private Consumer<Player> onCooldownExpired;

    private Consumer<Player> onCooldownStarted;

    public CooldownBuilder build() {
        CooldownManager.getInstance().registerCooldown(this, NLib.getInstance().getPlugin());
        return this;
    }
}