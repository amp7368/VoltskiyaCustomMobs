package apple.voltskiya.custom_mobs.gui;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class InventoryGui implements InventoryHolder {
    protected List<InventoryGuiPage> pageMap = new ArrayList<>();

    protected int page = 0;

    protected void addPage(InventoryGuiPage... pageGuis) {
        for (InventoryGuiPage pageGui : pageGuis) {
            pageGui.fillInventory();
            pageMap.add(pageGui);
        }
    }

    public void nextPage(int count) {
        List<HumanEntity> viewers = getInventory().getViewers();

        page += count;
        page = Math.max(0, page);
        page = Math.min(pageMap.size() - 1, page);
        update(viewers);
    }

    private void update(@Nullable List<HumanEntity> viewers) {
        for (HumanEntity viewer : new ArrayList<>(viewers == null ? getInventory().getViewers() : viewers)) {
            final InventoryGuiPage inventoryGuiPage = pageMap.get(page);
            inventoryGuiPage.update();
            viewer.openInventory(inventoryGuiPage.getInventory());
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

        default void update() {
            fillInventory();
        }

        void dealWithClick(InventoryClickEvent event);

        int size();
    }

    public interface InventoryGuiSlot {
        void dealWithClick(InventoryClickEvent event);

        ItemStack getItem();
    }

}
