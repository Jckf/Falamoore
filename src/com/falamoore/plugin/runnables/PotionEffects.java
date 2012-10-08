package com.falamoore.plugin.runnables;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.falamoore.plugin.Main;

public class PotionEffects implements Runnable {

    public PotionEffects() {
    }

    @Override
    public void run() {
        for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (Main.race.get(p.getName()).equalsIgnoreCase("Elf")) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1400, 2));
                p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1400, 2));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 1400, 1));
            } else if (Main.race.get(p.getName()).equalsIgnoreCase("Dwarf")) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 1400, 2));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1400, 2));
            }
        }
    }

}
