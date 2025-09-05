package com.notpatch.nlib.economy;

import org.bukkit.OfflinePlayer;

public interface Economy {

    double withdraw(OfflinePlayer player, double price);

    double deposit(OfflinePlayer player, double price);

    double getBalance(OfflinePlayer player);
}