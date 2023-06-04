package apple.voltskiya.custom_mobs.abilities.common.grenade.player;

import apple.mc.utilities.inventory.item.InventoryUtils;
import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.abilities.common.grenade.bomb.ThrowBomb;
import apple.voltskiya.custom_mobs.abilities.common.grenade.flashbang.ThrowFlashbang;
import apple.voltskiya.custom_mobs.sound.PlaySound;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

public class PlayerThrowGrenade implements Listener {

    public static final NamespacedKey GRENADE_VELOCITY_KEY = VoltskiyaPlugin.get().namespacedKey("grenade_velocity");
    public static final TextComponent FILLED_SQUARE = Component.text("\u25A0", TextColor.color(0x75ff93));
    public static final TextComponent EMPTY_SQUARE = Component.text("\u25A1", TextColor.color(0xffffff));
    public static final PlaySound GRENADE_INCREMENT_SOUND = new PlaySound(SoundCategory.PLAYERS, Sound.UI_STONECUTTER_SELECT_RECIPE,
        1f, 1f);
    public static final int MAX_FUSE_DURATION = 90;
    public static final int MIN_FUSE_DURATION = 30;
    private static final int MAX_VELOCITY_INCREMENT = 5;
    private static final float MAX_VELOCITY = 1.7f;
    private static final float MIN_VELOCITY = 0.3f;

    public PlayerThrowGrenade() {
        VoltskiyaPlugin.get().registerEvents(this);
    }

    private static void incrementVelocity(Player player, ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        int velocity = getVelocityMultiplier(container);
        velocity++;
        velocity %= MAX_VELOCITY_INCREMENT;
        container.set(GRENADE_VELOCITY_KEY, PersistentDataType.INTEGER, velocity);
        item.setItemMeta(meta);
        incrementVelocityDisplay(player, velocity);
    }

    private static int getVelocityMultiplier(PersistentDataContainer container) {
        return container.getOrDefault(GRENADE_VELOCITY_KEY, PersistentDataType.INTEGER, 3);
    }

    private static void incrementVelocityDisplay(Player player, int velocity) {
        velocity++;
        List<Component> messageParts = new ArrayList<>(MAX_VELOCITY_INCREMENT);
        for (int i = 0; i < velocity; i++)
            messageParts.add(FILLED_SQUARE);
        for (int i = 0; i < MAX_VELOCITY_INCREMENT - velocity; i++)
            messageParts.add(EMPTY_SQUARE);

        Component message = Component.join(JoinConfiguration.separator(Component.space()), messageParts);
        Times titleTimes = Times.times(Duration.ZERO, Duration.ofMillis(750), Duration.ofMillis(325));
        player.showTitle(Title.title(Component.empty(), message, titleTimes));
        GRENADE_INCREMENT_SOUND.play(player);
    }

    @EventHandler
    public void onThrowItem(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || item.getType().isAir()) return;
        List<String> itemFlags = Arrays.asList(InventoryUtils.get().getItemFlags(item));

        if (itemFlags.stream().noneMatch(GrenadeRecipes.ALL_GRENADES::contains)) return;
        event.setCancelled(true);
        if (event.getAction().isLeftClick()) {
            incrementVelocity(event.getPlayer(), item);
            return;
        }
        Location startLocation = event.getPlayer().getEyeLocation();
        Vector velocity = startLocation.getDirection();
        startLocation.add(velocity);
        float velocityIncrement =
            getVelocityMultiplier(item.getItemMeta().getPersistentDataContainer()) / (MAX_VELOCITY_INCREMENT - 1f);
        float velocityMultiplier = (MAX_VELOCITY - MIN_VELOCITY) * velocityIncrement + MIN_VELOCITY;
        int fuseDuration = (int) ((MAX_FUSE_DURATION - MIN_FUSE_DURATION) * velocityIncrement + MIN_FUSE_DURATION);
        velocity.multiply(velocityMultiplier);
        item.setAmount(item.getAmount() - 1);
        if (itemFlags.contains(GrenadeRecipes.GRENADE_FLASHBANG)) {
            new ThrowFlashbang(PlayerGrenadeConfig.get().flashbang).start(startLocation, velocity, fuseDuration);
        } else if (itemFlags.contains(GrenadeRecipes.GRENADE_BOMB)) {
            new ThrowBomb(PlayerGrenadeConfig.get().bomb).start(startLocation, velocity, fuseDuration);
        }
    }

}
