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

    @Override
    public double withdraw(OfflinePlayer player, double price) {
        if(this.economy == null){
            return price;
        }
        return 0;
    }

    @Override
    public double deposit(OfflinePlayer player, double price) {
        return 0;
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return 0;
    }
}
