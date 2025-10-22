package com.notpatch.nlib.economy.impl;

import com.notpatch.nlib.NLib;
import com.notpatch.nlib.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultEconomy implements Economy {

    private Economy economy = null;

    public VaultEconomy(){
        setupEconomy();
    }

    private void setupEconomy() {
        if (NLib.getInstance().getPlugin().getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        RegisteredServiceProvider<Economy> rsp = NLib.getInstance().getPlugin().getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        economy = rsp.getProvider();
    }

    public double withdraw(OfflinePlayer player, double price) {
        if (economy != null) {
            economy.withdraw(player, price);
            return price;
        }
        return 0.0;
    }

    public double deposit(OfflinePlayer player, double price) {
        if (economy != null) {
            economy.deposit(player, price);
            return price;
        }
        return 0.0;
    }

    public double getBalance(OfflinePlayer player) {
        return economy != null ? economy.getBalance(player) : 0.0;
    }
}
