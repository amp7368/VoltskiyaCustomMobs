package apple.voltskiya.custom_mobs.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public abstract class InventoryGui implements InventoryHolder {
    protected List<InventoryGuiPage> pageMap = new ArrayList<>();

    protected int page = 0;

    protected void addPage(InventoryGuiPage... pageGuis) {
        for (InventoryGuiPage pageGui : pageGuis) {
            pageGui.fillInventory();
            pageMap.add(pageGui);
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.pageMap.get(page).getInventory();
    }

    public void onGuiInventory(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory != null)
            if (clickedInventory.getHolder() instanceof InventoryGui) {
                InventoryGuiPage p = this.pageMap.get(page);
                if (p != null) p.dealWithClick(event);
            }
        event.setCancelled(true);
    }

    public void onPlayerInventory(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    public interface InventoryGuiPage {
        String getName();

        Inventory getInventory();

        void fillInventory();

        void setSlot(InventoryGuiSlot item, Collection<Integer> slot);

        void setSlot(InventoryGuiSlot item, int... slot);

        void dealWithClick(InventoryClickEvent event);

        int size();
    }

    public interface InventoryGuiSlot {
        void dealWithClick(InventoryClickEvent event);

        ItemStack getItem();
    }

    public abstract static class InventoryGuiPageSimple implements InventoryGuiPage {
        private final Inventory inventory;
        private final InventoryGuiSlot[] clicking = new InventoryGuiSlot[size()];

        public InventoryGuiPageSimple(InventoryHolder holder) {
            this.inventory = Bukkit.createInventory(holder, size(), getName());
            Arrays.fill(clicking, InventoryGuiSlotDoNothing.get());
        }


        @Override
        public Inventory getInventory() {
            return inventory;
        }

        @Override
        public void fillInventory() {
            for (int i = 0; i < clicking.length; i++) {
                this.getInventory().setItem(i, clicking[i].getItem());
            }
        }

        @Override
        public void setSlot(InventoryGuiSlot item, Collection<Integer> slot) {
            for (Integer i : slot) {
                clicking[i] = item;
            }
        }

        @Override
        public void setSlot(InventoryGuiSlot item, int... slot) {
            for (Integer i : slot) {
                clicking[i] = item;
            }
        }

        @Override
        public void dealWithClick(InventoryClickEvent event) {
            int slot = event.getSlot();
            if (slot < 0 || slot >= clicking.length) return;
            clicking[slot].dealWithClick(event);
        }
    }

    public static class InventoryGuiSlotGeneric implements InventoryGuiSlot {
        private final Consumer<InventoryClickEvent> dealWithEvent;
        private final ItemStack item;

        public InventoryGuiSlotGeneric(Consumer<InventoryClickEvent> dealWithEvent, ItemStack item) {
            this.dealWithEvent = dealWithEvent;
            this.item = item;
        }

        @Override
        public void dealWithClick(InventoryClickEvent event) {
            dealWithEvent.accept(event);
        }

        @Override
        public ItemStack getItem() {
            return item;
        }
    }

    private static class InventoryGuiSlotDoNothing implements InventoryGuiSlot {
        private static InventoryGuiSlotDoNothing instance = null;

        public static InventoryGuiSlotDoNothing get() {
            if (instance == null) instance = new InventoryGuiSlotDoNothing();
            return instance;
        }

        @Override
        public void dealWithClick(InventoryClickEvent event) {
        }

        @Override
        public ItemStack getItem() {
            return new ItemStack(Material.AIR);
        }
    }
}
