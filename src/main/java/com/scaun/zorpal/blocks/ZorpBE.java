package com.scaun.zorpal.blocks;

import java.sql.Time;

import javax.annotation.Nonnull;

import com.mojang.math.Matrix4f;
import com.scaun.zorpal.cap.IMachine;
import com.scaun.zorpal.cap.MachineCapability;
import com.scaun.zorpal.setup.Registration;
import com.scaun.zorpal.tools.CustomEnergyStorage;

import cpw.mods.util.Lazy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class ZorpBE extends BlockEntity {

    public static final int CAPACITY = 200000;
    public static final int RECEIVE = 2000;
    public static final int USAGE = 120; 
    public static final float SPEED = 1.0f;
    public static final float MAXSPEED = 1.0f;
    public static final int TPS = 20;
    public static final float TIME = 8.0f;

    private final TagKey[] inputLeftTags = {Tags.Items.INGOTS, Tags.Items.GEMS}; // not type safe but im not sure what to do because i can't make a TagKey<Item>[]
    private final Item[] inputRightItems = {Registration.ZORP_INGOT.get()};

        // Never create lazy optionals in getCapability. Always place them as fields in the tile entity:
    private final ItemStackHandler itemHandlerLeft = createHandler(1, inputLeftTags);
    private final ItemStackHandler itemHandlerRight = createHandler(1, inputRightItems);
    private final ItemStackHandler itemHandlerOut = createHandler(1);

    private final ItemStackHandler[] allSlots = {itemHandlerLeft, itemHandlerRight, itemHandlerOut};

    private final CombinedInvWrapper itemHandler = new CombinedInvWrapper(itemHandlerLeft, itemHandlerRight, itemHandlerOut);
    private final CombinedInvWrapper itemAutoHandler = new CombinedInvWrapper(itemHandlerLeft, itemHandlerRight, itemHandlerOut) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        };

        @Override
        @Nonnull
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            return ItemStack.EMPTY;
        }
    };

    private final LazyOptional<IItemHandler> handlerLeft = LazyOptional.of(() -> itemHandlerLeft);
    private final LazyOptional<IItemHandler> handlerRight = LazyOptional.of(() -> itemHandlerRight);
    private final LazyOptional<IItemHandler> handlerOut = LazyOptional.of(() -> itemHandlerOut);
    private final LazyOptional<IItemHandler> handlerInput = LazyOptional.of(() -> new CombinedInvWrapper(itemHandlerLeft, itemHandlerRight) {
        @Override
        @Nonnull
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            return ItemStack.EMPTY;
        }
    });
    private final LazyOptional<IItemHandler> handlerEverything = LazyOptional.of(() -> itemHandler);
    private final LazyOptional<IItemHandler> handlerAutomation = LazyOptional.of(() -> itemAutoHandler);

    private final LazyOptional[] slotTypes = {handlerAutomation, handlerInput, handlerOut};

    private final MachineCapability machineCap = createProgress();
    private final LazyOptional<IMachine> machineLazy = LazyOptional.of(() -> machineCap);
    private final LazyOptional<IEnergyStorage> energyLazy = LazyOptional.of(() -> machineCap);

    private boolean signal = false;
    private boolean isCrafting = false;

    public ZorpBE(BlockPos pos, BlockState state) {
        super(Registration.ZORP_TRANS_BE.get(), pos, state);
        machineCap.setProgress((int)(USAGE * TIME));

    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        handlerLeft.invalidate();
        handlerRight.invalidate();
        handlerOut.invalidate();
        handlerInput.invalidate();
        handlerEverything.invalidate();
        machineLazy.invalidate();
        energyLazy.invalidate();        
    }

    public void tickServer() {
        BlockState blockState = level.getBlockState(worldPosition);
        getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);

        // for (int i : machineCap.getSides()) {
        //     System.out.print(i + " ");
        // } System.out.println();




        if (hasRecipe() && hasNotReachedStackLimit() && machineCap.getEnergyStored() >= (USAGE * TPS * TIME) / SPEED && !isCrafting) {
            machineCap.setProgress((int)(TPS * TIME));
            isCrafting = true;
            level.setBlock(worldPosition, blockState.setValue(BlockStateProperties.POWERED, true), Block.UPDATE_ALL);
            setChanged();
        }
        if (hasRecipe() && isCrafting) {
            machineCap.incProcress();

            machineCap.consumeEnergy((int)(USAGE * SPEED));
            level.setBlock(worldPosition, blockState.setValue(BlockStateProperties.POWERED, true), Block.UPDATE_ALL);
            setChanged();

            if (machineCap.getProgress() <= 0) {
                itemHandler.extractItem(1, 1, false);
                itemHandler.setStackInSlot(2,
                    new ItemStack(itemHandler.getStackInSlot(0).getItem(), itemHandler.getStackInSlot(2).getCount() + 1));
                machineCap.setProgress((int)(TPS * TIME));
                isCrafting = false;
                setChanged();
            }
        }
        else {
            machineCap.setProgress((int)(TPS * TIME));
            level.setBlock(worldPosition, blockState.setValue(BlockStateProperties.POWERED, false), Block.UPDATE_ALL);
        }

        if (blockState.getValue(BlockStateProperties.POWERED) != (hasRecipe() && isCrafting)) {

        }
    }

    private void checkRedstone() {
        if (level.hasNeighborSignal(worldPosition)) {
            signal = true;
        } else signal = false;
    }

    @Override
    public void load(CompoundTag tag) {
        if (tag.contains("InventoryLeft")) {
            allSlots[0].deserializeNBT(tag.getCompound("InventoryLeft"));
        }
        if (tag.contains("InventoryRight")) {
            allSlots[1].deserializeNBT(tag.getCompound("InventoryRight"));
        }
        if (tag.contains("InventoryOut")) {
            allSlots[2].deserializeNBT(tag.getCompound("InventoryOut"));
        }
        if (tag.contains("Machine")) {
            machineCap.deserializeNBT(tag.getCompound("Machine"));
        }
        if (tag.contains("Info")) {
        }
        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.put("InventoryLeft", allSlots[0].serializeNBT());
        tag.put("InventoryRight", allSlots[1].serializeNBT());
        tag.put("InventoryOut", allSlots[2].serializeNBT());
        tag.put("Machine", machineCap.serializeNBT());

        CompoundTag infoTag = new CompoundTag();
        tag.put("Info", infoTag);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        if (tag != null) {
            load(tag);
            setChanged();
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        ClientboundBlockEntityDataPacket packet = ClientboundBlockEntityDataPacket.create(this);
        return packet;
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    private CustomEnergyStorage createEnergy() {
        return new CustomEnergyStorage(CAPACITY, RECEIVE) {
            @Override
            protected void onEnergyChanged() {
                setChanged();
            }
        };
    }

    private ItemStackHandler createHandler(int i, TagKey<Item>[] tags) {
        return new ItemStackHandler(i) {

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                if (stack.getItem().equals(Registration.ZORP_INGOT.get())) return false;
                for (TagKey<Item> tag : tags) {
                    if (isInArray(stack.getTags().toArray(), tag)) return true;
                } return false;
            }

            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
    }

    private ItemStackHandler createHandler(int i, Item[] items) {
        return new ItemStackHandler(i) {

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                for (Item item : items) {
                    if (stack.getItem().equals(item)) return true;
                } return false;
            }

            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
    }

    private ItemStackHandler createHandler(int i) {
        return new ItemStackHandler(i) {

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return false;
            }

            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
    }


    private MachineCapability createProgress() {
        return new MachineCapability(CAPACITY, RECEIVE, USAGE, SPEED) {
            @Override
            public void onChanged() {
                setChanged();
            }
        };
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side == null) return handlerEverything.cast();
            else return sideCapability(side);
        }
        if (cap == CapabilityEnergy.ENERGY) {
            return energyLazy.cast();
        }
        if (cap == MachineCapability.MACHINE) {
            return machineLazy.cast();
        }
        return super.getCapability(cap, side);
    }

    private <T> LazyOptional<T> sideCapability(Direction side) {
        Direction facing = level.getBlockState(worldPosition).getValue(BlockStateProperties.FACING);

        Matrix4f matrix = new Matrix4f(facing.getRotation());
        matrix.invert();
        side = Direction.rotate(matrix, side);

        // shuffling them around because i barely passed linalg
        switch(side) {
            case UP:
                return slotTypes[machineCap.getInSides(2)];
            case WEST:
                return slotTypes[machineCap.getInSides(1)];
            case SOUTH:
                return slotTypes[machineCap.getInSides(4)];
            case EAST:
                return slotTypes[machineCap.getInSides(3)];
            case DOWN:
                return slotTypes[machineCap.getInSides(5)];
            case NORTH:
                return slotTypes[machineCap.getInSides(0)];
            default:
                return handlerAutomation.cast();
            
        }
    }

    private boolean hasRecipe() {
        boolean a = isInArray(itemHandler.getStackInSlot(0).getTags().toArray(), Tags.Items.INGOTS) ||
                    isInArray(itemHandler.getStackInSlot(0).getTags().toArray(), Tags.Items.GEMS);
        boolean b = itemHandler.getStackInSlot(1).getItem() == Registration.ZORP_INGOT.get();
        return a && b;

        
    }

    private boolean hasNotReachedStackLimit() {
        return itemHandler.getStackInSlot(2).getCount() < itemHandler.getStackInSlot(2).getMaxStackSize();
    }

    private boolean isInArray(Object[] arr, Object tag) {
        for (Object check : arr) {
            if (check.equals(tag)) return true;
        } return false;
    }

    public int getCounterMax() {
        return (int)(TPS * TIME);
    }
}
