package apple.voltskiya.custom_mobs.sql;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class DBItemStack {
    public Material type;
    public int count;
    public String nbt;

    public DBItemStack(Material type, int count, String nbt) {
        this.type = type;
        this.count = count;
        this.nbt = nbt;
    }

    @Nullable
    public ItemStack toItem() {
        try {
            if (type == null) return null;
            final ItemStack itemStack = CraftItemStack.asBukkitCopy(net.minecraft.world.item.ItemStack.a(MojangsonParser.parse(this.nbt)));
            itemStack.setAmount(count);
            itemStack.setType(type);
            return itemStack;
        } catch (CommandSyntaxException e) {
            return null;
        }
    }

    public boolean exists() {
        return type != Material.AIR && count != 0;
    }

    public NBTTagCompound getEntityNbt() {
        if (this.nbt == null) return null;
        try {
            return net.minecraft.world.item.ItemStack.a(MojangsonParser.parse(this.nbt)).getTag();
        } catch (CommandSyntaxException e) {
            return new NBTTagCompound();
        }
    }

    public EntityType toEntityType() {
        return switch (type) {
            case SPECTRAL_ARROW -> EntityType.SPECTRAL_ARROW;
            case ARROW, TIPPED_ARROW -> EntityType.ARROW;
            case EGG -> EntityType.EGG;
            case SNOWBALL -> EntityType.SNOWBALL;
            default -> null;
        };
    }

    public boolean hasNbt() {
        return !this.nbt.isEmpty();
    }
}
