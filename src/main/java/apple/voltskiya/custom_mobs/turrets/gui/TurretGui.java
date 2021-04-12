package apple.voltskiya.custom_mobs.turrets.gui;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.turrets.TurretMob;
import apple.voltskiya.custom_mobs.util.MaterialUtils;
import apple.voltskiya.custom_mobs.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;

import static apple.voltskiya.custom_mobs.util.InventoryManagement.makeItem;

public class TurretGui implements InventoryHolder {
    private final TurretMob turret;
    @NotNull Inventory inventory;
    private final Map<Integer, InventoryAction> slotToAction = new HashMap<>() {{
        for (int i : FillInventory.getFiller()) put(i, InventoryAction.DONT_TOUCH);
        for (int i : FillInventory.getBowNoClick()) put(i, InventoryAction.DONT_TOUCH);
        for (int i : FillInventory.getHealth()) put(i, InventoryAction.DONT_TOUCH);
        for (int i : FillInventory.getArrowNoClick()) put(i, InventoryAction.DONT_TOUCH);

        for (int i : FillInventory.getRotateLeftLeftLeft()) put(i, InventoryAction.ROTATE_LEFT_LEFT_LEFT);
        for (int i : FillInventory.getRotateLeftLeft()) put(i, InventoryAction.ROTATE_LEFT_LEFT);
        for (int i : FillInventory.getRotateLeft()) put(i, InventoryAction.ROTATE_LEFT);
        for (int i : FillInventory.getRotateRightRightRight()) put(i, InventoryAction.ROTATE_RIGHT_RIGHT_RIGHT);
        for (int i : FillInventory.getRotateRightRight()) put(i, InventoryAction.ROTATE_RIGHT_RIGHT);
        for (int i : FillInventory.getRotateRight()) put(i, InventoryAction.ROTATE_RIGHT);

        for (int i : FillInventory.getRepairFull()) put(i, InventoryAction.REPAIR_FULL);
        for (int i : FillInventory.getRepair1()) put(i, InventoryAction.REPAIR);

        for (int i : FillInventory.getArrow()) put(i, InventoryAction.ARROW_SLOT);
        for (int i : FillInventory.getBow()) put(i, InventoryAction.BOW_SLOT);
    }};

    public TurretGui(TurretMob turret) {
        this.turret = turret;
        this.inventory = Bukkit.createInventory(this, 54);
        fillInventory(this.inventory);
    }

    public void updateView() {
        this.inventory.clear();
        fillInventory(this.inventory);
    }

    private void fillInventory(Inventory inventory) {
        for (int i : FillInventory.getBowNoClick())
            inventory.setItem(i, makeItem(Material.LIME_TERRACOTTA, 1, "Bow Slot", null));
        for (int i : FillInventory.getRotateLeftLeftLeft())
            inventory.setItem(i, makeItem(Material.RED_DYE, 1, "Rotate 90 Degrees Clockwise", null));
        for (int i : FillInventory.getRotateLeftLeft())
            inventory.setItem(i, makeItem(Material.MAGENTA_DYE, 1, "Rotate 10 Degrees Clockwise", null));
        for (int i : FillInventory.getRotateLeft())
            inventory.setItem(i, makeItem(Material.PINK_DYE, 1, "Rotate 1 Degree Clockwise", null));
        for (int i : FillInventory.getRotateRightRightRight())
            inventory.setItem(i, makeItem(Material.LIGHT_BLUE_DYE, 1, "Rotate 90 Degrees Counter-Clockwise", null));
        for (int i : FillInventory.getRotateRightRight())
            inventory.setItem(i, makeItem(Material.CYAN_DYE, 1, "Rotate 10 Degrees Counter-Clockwise", null));
        for (int i : FillInventory.getRotateRight())
            inventory.setItem(i, makeItem(Material.BLUE_DYE, 1, "Rotate 1 Degree Counter-Clockwise", null));
        for (int i : FillInventory.getFiller())
            inventory.setItem(i, makeItem(Material.BLACK_STAINED_GLASS_PANE, 1, "_", null));
        for (int i : FillInventory.getArrowNoClick())
            inventory.setItem(i, makeItem(Material.LIGHT_GRAY_TERRACOTTA, 1, "Arrows Below", null));
        for (int i : FillInventory.getHealth())
            inventory.setItem(i, makeItem(Material.BLACK_TERRACOTTA, 1, "Health",
                    Arrays.asList(String.valueOf((int) Math.ceil(turret.getHealth())), turret.getRepairCost() + " iron to repair")
            ));
        for (int i : FillInventory.getRepair1())
            inventory.setItem(i, makeItem(Material.IRON_INGOT, 1, "Repair " + TurretMob.HEALTH_PER_REPAIR + " hp",
                    Collections.singletonList("Cost: 1 iron")
            ));
        for (int i : FillInventory.getRepairFull())
            inventory.setItem(i, makeItem(Material.ANVIL, 1, "Repair " + TurretMob.HEALTH_PER_REPAIR + " hp",
                    Collections.singletonList("Cost: " + turret.getRepairCost() + " iron")
            ));
        Iterator<Pair<Material, Integer>> arrows = turret.getArrows().iterator();
        Iterator<Integer> arrowIndex = FillInventory.getArrow().iterator();
        while (arrows.hasNext() && arrowIndex.hasNext()) {
            Pair<Material, Integer> arrow = arrows.next();
            if (arrow != null && !arrow.getKey().isAir()) {
                inventory.setItem(arrowIndex.next(), makeItem(arrow.getKey(), arrow.getValue(), null, null));
            } else arrowIndex.next();
        }
    }


    public void dealWithClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        InventoryAction action = slotToAction.get(slot);
        if (action == null) return;
        action.dealWithClick.accept(event, this);
    }

    private void bowStartChange(InventoryClickEvent event) {
        final ItemStack cursor = event.getCursor();
        if (cursor == null || cursor.getType().isAir() || MaterialUtils.isBowLike(cursor.getType()))
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this::bowChange, 0);
        else event.setCancelled(true);
    }

    private void bowChange() {

    }

    private void arrowStartChange(InventoryClickEvent event) {
        final ItemStack cursor = event.getCursor();
        if (cursor == null || cursor.getType().isAir() || MaterialUtils.isArrow(cursor.getType()))
            Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this::arrowChange, 0);
        else event.setCancelled(true);
    }

    private void arrowChange() {
        List<Pair<Material, Integer>> arrows = new ArrayList<>();
        for (int index : FillInventory.getArrow()) {
            final ItemStack item = inventory.getItem(index);
            if (item == null || !MaterialUtils.isArrow(item.getType()) || item.getAmount() == 0) {
                arrows.add(new Pair<>(Material.AIR, 0));
            } else {
                arrows.add(new Pair<>(item.getType(), item.getAmount()));
            }
        }
        turret.setArrows(arrows);
    }

    private void rotate(double degrees) {
        degrees = Math.toRadians(degrees);
        turret.rotateCenter(degrees);
    }

    public Long getUniqueId() {
        return turret.getUniqueId();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }


    private enum InventoryAction {
        DONT_TOUCH((click, turretGui) -> {
            click.setCancelled(true);
        }),
        ROTATE_LEFT_LEFT_LEFT((click, turretGui) -> {
            turretGui.rotate(-90);
            click.setCancelled(true);
        }),
        ROTATE_LEFT_LEFT((click, turretGui) -> {
            turretGui.rotate(-10);
            click.setCancelled(true);
        }),
        ROTATE_LEFT((click, turretGui) -> {
            turretGui.rotate(-1);
            click.setCancelled(true);
        }),
        ROTATE_RIGHT_RIGHT_RIGHT((click, turretGui) -> {
            turretGui.rotate(90);
            click.setCancelled(true);
        }),
        ROTATE_RIGHT_RIGHT((click, turretGui) -> {
            turretGui.rotate(10);
            click.setCancelled(true);
        }),
        ROTATE_RIGHT((click, turretGui) -> {
            turretGui.rotate(1);
            click.setCancelled(true);
        }),
        REPAIR_FULL((click, turretGui) -> {
            turretGui.repair(turretGui.turret.getRepairCost(), click.getWhoClicked());
            click.setCancelled(true);
        }),
        REPAIR((click, turretGui) -> {
            turretGui.repair(1, click.getWhoClicked());
            click.setCancelled(true);
        }),
        ARROW_SLOT((click, turretGui) -> {
            turretGui.arrowStartChange(click);
        }),
        BOW_SLOT((click, turretGui) -> {
            turretGui.bowStartChange(click);
        });

        private final BiConsumer<InventoryClickEvent, TurretGui> dealWithClick;

        InventoryAction(BiConsumer<InventoryClickEvent, TurretGui> dealWithClick) {
            this.dealWithClick = dealWithClick;
        }
    }

    private void repair(int repairAmount, HumanEntity player) {
        repairAmount = Math.min(repairAmount, turret.getRepairCost());
        if (repairAmount == 0) {
            player.sendMessage(ChatColor.RED + "The turret is already fully repaired");
            return;
        }
        Inventory inventory = player.getInventory();
        int repaired = 0;
        for (ItemStack item : inventory.getStorageContents()) {
            if (item != null && item.getType() == Material.IRON_INGOT) {
                int amountThere = item.getAmount();
                if (amountThere < repairAmount) {
                    repairAmount -= amountThere;
                    repaired += amountThere;
                    item.setAmount(0);
                } else {
                    item.setAmount(amountThere - repairAmount);
                    repaired += repairAmount;
                    break;
                }
            }
        }
        turret.repair(repaired);
    }

    private static class FillInventory {
        private static Collection<Integer> getBowNoClick() {
            return Arrays.asList(0, 1, 2, 9, 11, 18, 19, 20);
        }

        private static Collection<Integer> getRotateLeftLeftLeft() {
            return Collections.singletonList(21);
        }

        private static Collection<Integer> getRotateLeftLeft() {
            return Collections.singletonList(22);
        }

        private static Collection<Integer> getRotateLeft() {
            return Collections.singletonList(23);
        }

        private static Collection<Integer> getRotateRight() {
            return Collections.singletonList(24);
        }

        private static Collection<Integer> getRotateRightRight() {
            return Collections.singletonList(25);
        }

        private static Collection<Integer> getRotateRightRightRight() {
            return Collections.singletonList(26);
        }

        public static Collection<Integer> getFiller() {
            List<Integer> filler = new ArrayList<>();
            for (int i = 3; i < 8; i++) filler.add(i);
            for (int i = 12; i < 16; i++) filler.add(i);
            for (int i = 30; i < 36; i++) filler.add(i);
            for (int i = 39; i < 45; i++) filler.add(i);
            for (int i = 48; i < 54; i++) filler.add(i);
            return filler;
        }

        public static Collection<Integer> getArrowNoClick() {
            List<Integer> filler = new ArrayList<>();
            for (int i = 27; i < 30; i++) filler.add(i);
            return filler;
        }

        public static Collection<Integer> getHealth() {
            return Collections.singletonList(8);
        }

        public static Collection<Integer> getRepair1() {
            return Collections.singletonList(16);
        }

        public static Collection<Integer> getRepairFull() {
            return Collections.singletonList(17);
        }

        public static Collection<Integer> getArrow() {
            return Arrays.asList(36, 37, 38, 45, 46, 47);
        }

        public static Collection<Integer> getBow() {
            return Collections.singletonList(10);
        }
    }
}
