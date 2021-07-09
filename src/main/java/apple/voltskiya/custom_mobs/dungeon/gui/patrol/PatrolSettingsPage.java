package apple.voltskiya.custom_mobs.dungeon.gui.patrol;

import apple.voltskiya.custom_mobs.dungeon.patrols.Patrol;
import apple.voltskiya.custom_mobs.dungeon.patrols.PatrolStep;
import apple.voltskiya.custom_mobs.dungeon.patrols.PatrolWand;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import voltskiya.apple.utilities.util.gui.InventoryGui;
import voltskiya.apple.utilities.util.gui.InventoryGuiPageSimple;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotGeneric;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

import java.util.Arrays;
import java.util.Collections;

public class PatrolSettingsPage extends InventoryGuiPageSimple {
    private final PatrolGui patrolGui;
    private final Patrol patrol;

    public PatrolSettingsPage(PatrolGui patrolGui) {
        super(patrolGui);
        this.patrolGui = patrolGui;
        this.patrol = patrolGui.getPatrol();
        setSlot(new GiveWand(), 0);
        setSlot(new StepLocation(), 1);
        setSlot(new InventoryGuiSlotGeneric((e) -> {
            patrol.currenStepIndexAdd(-1);
            update();
        }, InventoryUtils.makeItem(Material.ORANGE_TERRACOTTA, 1, "Back a step", null)), 21);
        setSlot(new InventoryGuiSlotGeneric((e) -> {
            patrol.currenStepIndexAdd(1);
            update();
        }, InventoryUtils.makeItem(Material.LIME_TERRACOTTA, 1, "Forward a step", null)), 23);
    }

    @Override
    public String getName() {
        return "Patrol Settings";
    }

    @Override
    public int size() {
        return 54;
    }

    private class GiveWand implements InventoryGui.InventoryGuiSlot {
        @Override
        public void dealWithClick(InventoryClickEvent event) {
            final ItemStack wand = new ItemStack(Material.STICK);
            final ItemMeta itemMeta = wand.getItemMeta();
            itemMeta.getPersistentDataContainer().set(PatrolWand.PATROL_WAND_NAMESPACE, PersistentDataType.STRING, patrolGui.getPatrol().getUID());
            itemMeta.setDisplayName(patrol.getName());
            wand.setItemMeta(itemMeta);
            patrolGui.getPlayer().getInventory().addItem(wand);
        }

        @Override
        public ItemStack getItem() {
            return InventoryUtils.makeItem(Material.STICK, 1, "Get wand", Arrays.asList("Name: " + patrol.getName(), "Rename the wand to change the name of the patrol"));
        }
    }

    private class StepLocation implements InventoryGui.InventoryGuiSlot {
        @Override
        public void dealWithClick(InventoryClickEvent event) {
        }

        @Override
        public ItemStack getItem() {
            final PatrolStep currentStep = patrol.getCurrentStep();
            final Location stepLocation = currentStep == null ? null : currentStep.getLocation();
            return InventoryUtils.makeItem(Material.BLACK_TERRACOTTA, 1, "Current step location",
                    Collections.singletonList(
                            stepLocation == null ?
                                    "No steps in place" :
                                    String.format("[%d, %d, %d]", stepLocation.getBlockX(), stepLocation.getBlockY(), stepLocation.getBlockZ())
                    )
            );
        }
    }
}
