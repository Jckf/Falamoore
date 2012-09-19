package com.falamoore.plugin.runnables;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.falamoore.plugin.PermissionManager;

public class PotionEffects implements Runnable {
    final Player player;

    public PotionEffects(Player player) {
        this.player = player;
    }

    public PotionEffects() {
        player = null;
    }

    @Override
    public void run() {
        if (player != null) {
            if (PermissionManager.getRace(player).equalsIgnoreCase("Elf")) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1400, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1400, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 1400, 1));
            } else if (PermissionManager.getRace(player).equalsIgnoreCase("Dwarf")) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 1400, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1400, 2));
            }
            return;
        }
        for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (PermissionManager.getRace(p).equalsIgnoreCase("Elf")) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1400, 2));
                p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1400, 2));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 1400, 1));
            } else if (PermissionManager.getRace(p).equalsIgnoreCase("Dwarf")) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 1400, 2));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1400, 2));
            }
        }
    }

}
