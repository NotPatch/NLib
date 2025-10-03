package com.notpatch.nlib.builder;

import com.notpatch.nlib.manager.WarmupManager;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Builder
@Getter
public class WarmupBuilder {

    @NonNull
    private final Player player;

    @Builder.Default
    private long duration = 3L;

    @Builder.Default
    private TimeUnit timeUnit = TimeUnit.SECONDS;

    private Consumer<Player> onStart;

    @NonNull
    private Runnable onSuccess;

    private Consumer<Player> onCancel;

    public void start() {
        WarmupManager.getInstance().startWarmup(this);
    }
}