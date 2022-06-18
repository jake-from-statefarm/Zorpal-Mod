package com.scaun.zorpal.blocks;

import java.sql.Time;

import javax.annotation.Nonnull;

import com.scaun.zorpal.cap.IMachine;
import com.scaun.zorpal.cap.MachineCapability;
import com.scaun.zorpal.setup.Registration;
import com.scaun.zorpal.tools.CustomEnergyStorage;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
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
    public static final int USAGE = 80; 
    public static final float SPEED = 1.0f;
    public static final int TPS = 20;
    public static final float TIME = 8.0f;

        // Never create lazy optionals in getCapability. Always place them as fields in the tile entity:
    private final ItemStackHandler itemHandlerLeft = createHandler(1);
    private final ItemStackHandler itemHandlerRight = createHandler(1);
    private final ItemStackHandler itemHandlerOut = createHandler(1);

    private final ItemStackHandler[] allSlots = {itemHandlerLeft, itemHandlerRight, itemHandlerOut};

    private final CombinedInvWrapper itemHandler = new CombinedInvWrapper(itemHandlerLeft, itemHandlerRight, itemHandlerOut);

    private final LazyOptional<IItemHandler> handlerLeft = LazyOptional.of(() -> itemHandlerLeft);
    private final LazyOptional<IItemHandler> handlerRight = LazyOptional.of(() -> itemHandlerRight);
    private final LazyOptional<IItemHandler> handlerOut = LazyOptional.of(() -> itemHandlerOut);
    private final LazyOptional<IItemHandler> handlerInput = LazyOptional.of(() -> new CombinedInvWrapper(itemHandlerLeft, itemHandlerRight));
    private final LazyOptional<IItemHandler> handlerEverything = LazyOptional.of(() -> itemHandler);


    private final CustomEnergyStorage energyStorage = createEnergy();
    private final LazyOptional<IEnergyStorage> energy = LazyOptional.of(() -> energyStorage);

    private final MachineCapability progressCap = createProgress();
    private final LazyOptional<IMachine> progress = LazyOptional.of(() -> progressCap);

    private boolean signal = false;
    private boolean isCrafting = false;

    public ZorpBE(BlockPos pos, BlockState state) {
        super(Registration.ZORP_TRANS_BE.get(), pos, state);
        progressCap.setProgress((int)(USAGE * TIME));
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        handlerEverything.invalidate();
        energy.invalidate();        
    }

    public void tickServer() {
        BlockState blockState = level.getBlockState(worldPosition);
        getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);

        if (hasRecipe() && hasNotReachedStackLimit() && energyStorage.getEnergyStored() >= (USAGE * TPS * TIME) / SPEED && !isCrafting) {
            progressCap.setProgress((int)(TPS * TIME));
            isCrafting = true;
            level.setBlock(worldPosition, blockState.setValue(BlockStateProperties.POWERED, true), Block.UPDATE_ALL);
            setChanged();
        }
        if (hasRecipe() && isCrafting) {
            progressCap.addProgress((int)(-SPEED));;

            energyStorage.addEnergy(-USAGE);
            level.setBlock(worldPosition, blockState.setValue(BlockStateProperties.POWERED, true), Block.UPDATE_ALL);
            setChanged();

            if (progressCap.getProgress() <= 0) {
                itemHandler.extractItem(1, 1, false);
                itemHandler.setStackInSlot(2,
                    new ItemStack(itemHandler.getStackInSlot(0).getItem(), itemHandler.getStackInSlot(2).getCount() + 1));
                progressCap.setProgress((int)(TPS * TIME));
                isCrafting = false;
                setChanged();
            }
        }
        else {
            progressCap.setProgress((int)(TPS * TIME));
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
        if (tag.contains("Energy")) {
            energyStorage.deserializeNBT(tag.get("Energy"));
        }
        if (tag.contains("progress")) {
            progressCap.deserializeNBT(tag.getCompound("progress"));
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
        tag.put("Energy", energyStorage.serializeNBT());
        tag.put("progress", progressCap.serializeNBT());

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

    private ItemStackHandler createHandler(int i) {
        return new ItemStackHandler(i) {

            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
    }

    private MachineCapability createProgress() {
        return new MachineCapability() {

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
            return handlerEverything.cast();
            // return fuckthis.cast();
        }
        if (cap == CapabilityEnergy.ENERGY) {
            return energy.cast();
        }
        if (cap == MachineCapability.MACHINE) {
            return progress.cast();
        }
        return super.getCapability(cap, side);
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
