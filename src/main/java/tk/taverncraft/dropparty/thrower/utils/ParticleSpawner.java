package tk.taverncraft.dropparty.thrower.utils;

import java.util.Random;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

/**
 * Handles spawning of fireworks and particles.
 */
public class ParticleSpawner {
    private static Random random = new Random();

    /**
     * Sets off fireworks at given location.
     *
     * @param location location to set fireworks at
     */
    public void displayFireworkEffect(Location location) {
        Firework firework = location.getWorld().spawn(location, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        Color randomColor = Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        Color randomFade = Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        meta.addEffect(FireworkEffect.builder()
            .withColor(randomColor)
            .withFade(randomFade)
            .with(FireworkEffect.Type.BURST)
            .flicker(false)
            .trail(true)
            .build());
        meta.setPower(0);
        firework.setFireworkMeta(meta);
    }

    /**
     * Spawns particle at given location.
     *
     * @param particle particle to spawn
     * @param location location to spawn at
     * @param effectCount number of particles to spawn
     */
    public void displayParticleEffect(Particle particle, Location location, int effectCount) {
        location.getWorld().spawnParticle(particle, location, effectCount, 0.1, 0.1, 0.1, 0.0);
    }
}
