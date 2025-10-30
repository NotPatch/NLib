package com.notpatch.nlib.economy.impl;

import com.notpatch.nlib.NLib;
import com.notpatch.nlib.util.NLogger;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultEconomy implements com.notpatch.nlib.economy.Economy {

    private net.milkbowl.vault.economy.Economy economy = null;

    public VaultEconomy() {
        setupEconomy();
    }

    private void setupEconomy() {
        if (NLib.getInstance().getPlugin().getServer().getPluginManager().getPlugin("Vault") == null) {
            NLogger.error("Vault plugin not found!");
            return;
        }

        try {
            RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp =
                    NLib.getInstance().getPlugin().getServer().getServicesManager()
                            .getRegistration(net.milkbowl.vault.economy.Economy.class);

            if (rsp == null) {
                NLogger.error("No economy provider found!");
                return;
            }

            economy = rsp.getProvider();
            NLogger.info("Economy service initialized successfully!");
        } catch (Exception e) {
            NLogger.error("Failed to setup economy: " + e.getMessage());
        }
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        if (economy == null) {
            NLogger.error("Economy service is null!");
            return 0.0;
        }
        return economy.getBalance(player);
    }

    @Override
    public double withdraw(OfflinePlayer player, double price) {
        if (economy == null) {
            NLogger.error("Economy service is null!");
            return 0.0;
        }
        if (economy.withdrawPlayer(player, price).transactionSuccess()) {
            return price;
        }
        return 0.0;
    }

    @Override
    public double deposit(OfflinePlayer player, double price) {
        if (economy == null) {
            NLogger.error("Economy service is null!");
            return 0.0;
        }
        if (economy.depositPlayer(player, price).transactionSuccess()) {
            return price;
        }
        return 0.0;
    }
}
