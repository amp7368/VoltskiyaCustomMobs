package apple.voltskiya.custom_mobs.sql;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.v1_16_R3.MojangsonParser;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class DBItemStack {
    public Material type;
    public int count;
    public final String nbt;

    public DBItemStack(Material type, int count, String nbt) {
        this.type = type;
        this.count = count;
        this.nbt = nbt;
    }

    public ItemStack toItem() {
        try {
            final ItemStack itemStack = CraftItemStack.asBukkitCopy(net.minecraft.server.v1_16_R3.ItemStack.a(MojangsonParser.parse(this.nbt)));
            itemStack.setAmount(count);
            itemStack.setType(type);
            return itemStack;
        } catch (CommandSyntaxException e) {
            return new ItemStack(Material.AIR);
        }
    }

    public boolean exists() {
        return type != Material.AIR && count != 0;
    }

    public NBTTagCompound getEntityNbt() {
        try {
            return net.minecraft.server.v1_16_R3.ItemStack.a(MojangsonParser.parse(this.nbt)).getTag();
        } catch (CommandSyntaxException e) {
            return new NBTTagCompound();
        }
    }

    public EntityType toEntityType() {
        switch (type) {
            case SPECTRAL_ARROW:
                return EntityType.SPECTRAL_ARROW;
            case ARROW:
            case TIPPED_ARROW:
                return EntityType.ARROW;
            case EGG:
                return EntityType.EGG;
            case SNOWBALL:
                return EntityType.SNOWBALL;
            default:
                return null;
        }
    }

    public boolean hasNbt() {
        return !this.nbt.isEmpty();
    }
}
