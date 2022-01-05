package apple.voltskiya.custom_mobs.turret.infinite;

import apple.voltskiya.custom_mobs.turret.parent.TurretArrowStack;
import apple.voltskiya.custom_mobs.turret.parent.TurretBow;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TurretBowInfinite extends TurretBow {
    private static final TurretArrowStack ARROW;

    static {
        ARROW = new TurretArrowStack(0);
    }

    public TurretBowInfinite() {
    }

    @Override
    public Material getHeadMaterial() {
        return Material.REDSTONE_TORCH;
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public int ticksPerShoot() {
        return 5;
    }

    @Override
    public @Nullable TurretArrowStack removeArrow() {
        return ARROW;
    }

    @Override
    public void tickBowDurability() {
        super.tickBowDurability();
    }

    @Override
    public double getBreakingChance() {
        return super.getBreakingChance();
    }

    @Override
    public boolean isCrossBow() {
        return super.isCrossBow();
    }

    @Override
    public boolean isArrowsEmpty() {
        return super.isArrowsEmpty();
    }

    @Override
    public List<ItemStack> getArrows() {
        return super.getArrows();
    }

    @Override
    public @Nullable ItemStack removeArrowSlot(int slot) {
        return super.removeArrowSlot(slot);
    }

    @Override
    public @Nullable ItemStack getBowItem() {
        return super.getBowItem();
    }

    @Override
    public @Nullable ItemStack removeBowItem() {
        return super.removeBowItem();
    }

    @Override
    public @Nullable ItemStack addArrow(ItemStack item) {
        return super.addArrow(item);
    }

    @Override
    public ItemStack addBow(ItemStack bow) {
        return super.addBow(bow);
    }
}
