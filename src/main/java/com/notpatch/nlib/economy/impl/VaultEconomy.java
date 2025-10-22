package com.notpatch.nlib.economy.impl;

import com.notpatch.nlib.NLib;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultEconomy implements com.notpatch.nlib.economy.Economy {

    private Economy economy = null;

    public VaultEconomy(){
        setupEconomy();
    }

    private void setupEconomy() {
        if (NLib.getInstance().getPlugin().getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp = NLib.getInstance().getPlugin().getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (rsp == null) {
            return;
        }
        economy = rsp.getProvider();
    }

    @Override
    public double withdraw(OfflinePlayer player, double price) {
        if (economy != null) {
            economy.withdrawPlayer(player, price);
            return price;
        }
        return 0.0;
    }

    @Override
    public double deposit(OfflinePlayer player, double price) {
        if (economy != null) {
            economy.depositPlayer(player, price);
            return price;
        }
        return 0.0;
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return economy != null ? economy.getBalance(player) : 0.0;
    }
}
