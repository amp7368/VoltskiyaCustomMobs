package apple.voltskiya.custom_mobs.turret.parent;

import apple.utilities.util.ObjectUtilsFormatting;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.utilities.util.minecraft.EnchantmentUtils;
import voltskiya.apple.utilities.util.minecraft.ItemSerializable;
import voltskiya.apple.utilities.util.serializing.EnchantmentSerializeable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TurretBow {
    private final transient Random random = new Random();
    private final List<TurretArrowStack> arrows = new ArrayList<>();
    @Nullable
    private ItemSerializable bow = null;
    private int durabilityLeft = 0;
    private EnchantmentSerializeable enchantments = EnchantmentSerializeable.empty();

    public TurretBow() {
    }

    public TurretBow(int stackSize, int slots) {
        for (int i = 0; i < slots; i++) {
            arrows.add(new TurretArrowStack(stackSize));
        }
    }

    public Material getHeadMaterial() {
        return bow == null ? Material.AIR : bow.getMaterial();
    }

    public boolean exists() {
        return bow != null;
    }

    /**
     * @return how many skipped ticks before shooting
     */
    public int ticksPerShoot() {
        if (exists()) return 1;
        final int quickCharge = this.getQuickCharge();
        return switch (quickCharge) {
            case 0 -> 4;
            case 1 -> 3;
            case 2 -> 2;
            default -> 1;
        };
    }


    @Nullable
    public TurretArrowStack removeArrow() {
        for (TurretArrowStack arrow : arrows) {
            if (arrow.exists()) {
                arrow.remove();
                return arrow;
            }
        }
        return null;
    }

    public void tickBowDurability() {
        if (this.bow == null) return;
        if (random.nextDouble() < getBreakingChance()) {
            if (--durabilityLeft <= 0) {
                this.bow = null;
            }
        }
    }

    public double getBreakingChance() {
        return EnchantmentUtils.randomBreakUnbreaking(getEnchantmentLevel(Enchantment.DURABILITY)) ? 1 : 0;
    }

    private int getQuickCharge() {
        return getEnchantmentLevel(Enchantment.QUICK_CHARGE);
    }

    public boolean isCrossBow() {
        return bow != null && bow.getMaterial() == Material.CROSSBOW;
    }

    public int getEnchantmentLevel(Enchantment e) {
        return enchantments.getEnchantment(e);
    }

    public boolean isArrowsEmpty() {
        for (TurretArrowStack arrow : arrows) {
            if (arrow.exists()) return false;
        }
        return true;
    }

    public List<ItemStack> getArrows() {
        List<ItemStack> arrowItems = new ArrayList<>();
        for (TurretArrowStack arrow : arrows) {
            arrowItems.add(arrow.toItem());
        }
        return arrowItems;
    }

    @Nullable
    public ItemStack removeArrowSlot(int slot) {
        return arrows.size() <= slot ? null : arrows.get(slot).removeAll();
    }

    @Nullable
    public ItemStack getBowItem() {
        return ObjectUtilsFormatting.failToNull(bow, ItemSerializable::getItem, this::applyBowDamage);
    }

    private ItemStack applyBowDamage(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta instanceof Damageable damageable) {
            damageable.setDamage(item.getType().getMaxDurability() - durabilityLeft);
            item.setItemMeta(damageable);
        }
        return item;
    }

    public @Nullable ItemStack removeBowItem() {
        ItemStack bowItem = getBowItem();
        this.enchantments.applyEnchantments(bowItem);
        this.enchantments = EnchantmentSerializeable.empty();
        this.bow = null;
        return bowItem;
    }

    @Nullable
    public ItemStack addArrow(ItemStack item) {
        for (TurretArrowStack arrowSlot : arrows) {
            item = arrowSlot.merge(item);
            if (item == null) return null;
        }
        return item;
    }

    public ItemStack addBow(ItemStack bow) {
        if (this.bow == null) {
            setBow(bow);
            return null;
        }
        return bow;
    }

    private void setBow(ItemStack bow) {
        this.bow = new ItemSerializable(bow, true);
        this.enchantments = new EnchantmentSerializeable(bow);
        if (bow.getItemMeta() instanceof Damageable damageable) {
            this.durabilityLeft = bow.getType().getMaxDurability() - damageable.getDamage();
        } else {
            this.durabilityLeft = 0;
        }
    }
}
