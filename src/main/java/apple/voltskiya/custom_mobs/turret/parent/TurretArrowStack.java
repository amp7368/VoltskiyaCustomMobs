package apple.voltskiya.custom_mobs.turret.parent;

import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;
import voltskiya.apple.utilities.util.minecraft.NbtUtils;

public class TurretArrowStack {
    private final int stackSize;
    @Nullable
    private Material material;
    private NBTTagCompound entityTag;
    private int count;
    @Nullable
    private EntityType entityType;

    public TurretArrowStack(int stackSize) {
        this.stackSize = stackSize;
    }

    public EntityType toEntityType() {
        return entityType;
    }

    public NBTTagCompound getEntityNbt() {
        return null;
    }


    @Nullable
    public ItemStack toItem() {
        if (exists())
            return InventoryUtils.makeItem(material, count, (String) null, null);
        return null;
    }

    public boolean exists() {
        return count != 0 && material != null;
    }

    public ItemStack removeAll() {
        ItemStack item = toItem();
        this.material = null;
        this.entityTag = null;
        this.count = 0;
        return item;
    }

    @Nullable
    public ItemStack merge(ItemStack item) {
        int amountAdding;
        if (exists()) {
            if (badMerge(item)) return item;
            amountAdding = Math.min(this.stackSize - count, item.getAmount());
        } else {
            amountAdding = Math.min(this.stackSize, item.getAmount());
            this.entityTag = getEntityTag(item);
            this.material = item.getType();
            this.entityType = switch (material) {
                case ARROW, TIPPED_ARROW -> EntityType.ARROW;
                case SPECTRAL_ARROW -> EntityType.SPECTRAL_ARROW;
                case EGG -> EntityType.EGG;
                case SNOWBALL -> EntityType.SNOWBALL;
                default -> EntityType.valueOf(material.name());
            };
        }
        this.count += amountAdding;
        int newAmountLeft = item.getAmount() - amountAdding;
        if (newAmountLeft == 0) return null;
        item.setAmount(newAmountLeft);
        return item;
    }

    public boolean badMerge(ItemStack item) {
        boolean badType = item.getType() != material;
        if (badType) return true;
        NBTTagCompound nbt = getEntityTag(item);
        boolean badNbt = !nbt.equals(this.entityTag);
        if (badNbt) return true;
        return count >= stackSize;
    }

    @NotNull
    public NBTTagCompound getEntityTag(ItemStack item) {
        NBTTagCompound nbt = CraftItemStack.asNMSCopy(item).save(new NBTTagCompound());
        return NbtUtils.getItemEntity(nbt);
    }

    public void remove() {
        this.count--;
    }
}
