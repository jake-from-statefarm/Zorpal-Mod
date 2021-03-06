package com.scaun.zorpal.blocks;

import com.scaun.zorpal.cap.IMachine;
import com.scaun.zorpal.cap.MachineCapability;
import com.scaun.zorpal.setup.Registration;
import com.scaun.zorpal.tools.CustomDisabledSlot;
import com.scaun.zorpal.tools.CustomEnergyStorage;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.Tags;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class ZorpContainer extends AbstractContainerMenu {
    private final BlockEntity blockEntity;
    private final Player playerEntity;
    private final IItemHandler playerInventory;


    private BlockPos pos;

    public ZorpContainer(int windowId, BlockPos pos, Inventory playerInventory, Player player) {
        super(Registration.ZORP_TRANS_CONTAINER.get(), windowId);
        blockEntity = player.getCommandSenderWorld().getBlockEntity(pos);
        this.playerEntity = player;
        this.playerInventory = new InvWrapper(playerInventory);
        this.pos = pos;

        if (blockEntity != null) {
            blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
                addSlot(new SlotItemHandler(h, 0, 66, 12));
                addSlot(new SlotItemHandler(h, 1, 86, 12));
                addSlot(new CustomDisabledSlot(h, 2, 77, 41));
            });
        }
        layoutPlayerInventorySlots(11, 71);
        trackPower();
    }

    // Setup syncing of power from server to client
    public void trackPower() {
        // Split our 32 bit int into two 16 bit ints
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return getEnergy() & 0xffff;
            }
            @Override
            public void set(int value) {
                    blockEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(h -> {
                        int energyStored = h.getEnergyStored() & 0xffff0000;
                        ((MachineCapability)h).setEnergy(energyStored + (value & 0xffff));
                    });
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return (getEnergy() >> 16) & 0xffff;
            }
            @Override
            public void set(int value) {
                blockEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(h -> {
                    int energyStored = h.getEnergyStored() & 0x0000ffff;
                    ((MachineCapability)h).setEnergy(energyStored | (value << 16));
                });
            }
        });
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();
            if (index < 3) {
                if (!this.moveItemStackTo(stack, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, itemstack);
            } else {
                if (stack.getItem() == Registration.ZORP_INGOT.get() && index > 2) {
                    if (!this.moveItemStackTo(stack, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if ((stack.is(Tags.Items.INGOTS) || stack.is(Tags.Items.GEMS)) && index > 2){
                    if (!this.moveItemStackTo(stack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }

                } else if (index < 28) {
                    if (!this.moveItemStackTo(stack, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 37) {
                    if (!this.moveItemStackTo(stack, 3, 30, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, stack);
        }

        return itemstack;
    }

    public int getEnergy() {
        return blockEntity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
    }

    public int getMaxEnergy() {
        return blockEntity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0);
    }

    public int getEnergyUsage() {
        float usage = (float)blockEntity.getCapability(MachineCapability.MACHINE).map(IMachine::getUsage).orElse(0);
        float speed = blockEntity.getCapability(MachineCapability.MACHINE).map(IMachine::getSpeed).orElse(1.0f);

        System.out.println("USAGE: " + usage + " SPEED:" + speed);
        return (int)(usage * speed);
    }

    public float getCounter() {
        return blockEntity.getCapability(MachineCapability.MACHINE).map(IMachine::getProgress).orElse(0.0f);
    }

    public int getCounterMax() {
        return ((ZorpBE)blockEntity).getCounterMax();
    }

    public int[] getSides() {
        return blockEntity.getCapability(MachineCapability.MACHINE).map(IMachine::getSides).orElse(null);
    }

    public void setInSides(int index, int value) {
        IMachine cap = blockEntity.getCapability(MachineCapability.MACHINE).orElse(null);
        cap.setInSides(index, value);
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()), playerEntity, Registration.ZORP_TRANS.get());
    }

    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }
}
