package com.scaun.zorpal.blocks;

import java.sql.Time;

import javax.annotation.Nonnull;

import com.scaun.zorpal.cap.IProgress;
import com.scaun.zorpal.cap.ProgressCapability;
import com.scaun.zorpal.setup.Registration;
import com.scaun.zorpal.tools.CustomEnergyStorage;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class ZorpBE extends BlockEntity {

    public static final int CAPACITY = 80000;
    public static final int RECEIVE = 2000;
    public static final int USAGE = 320 / 20; 
    public static final float SPEED = 1.0f;
    public static final int TPS = 20;
    public static final float TIME = 8.0f;

        // Never create lazy optionals in getCapability. Always place them as fields in the tile entity:
    private final ItemStackHandler itemHandler = createHandler();
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

    private final CustomEnergyStorage energyStorage = createEnergy();
    private final LazyOptional<IEnergyStorage> energy = LazyOptional.of(() -> energyStorage);

    private final ProgressCapability progressCap = createProgress();
    private final LazyOptional<IProgress> progress = LazyOptional.of(() -> progressCap);

    private boolean signal = false;
    private boolean isCrafting = false;

    public ZorpBE(BlockPos pos, BlockState state) {
        super(Registration.ZORP_TRANS_BE.get(), pos, state);
        progressCap.setProgress((int)(USAGE * TIME));
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        handler.invalidate();
        energy.invalidate();
    }

    public void tickServer() {
        getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);

        if (hasRecipe() && hasNotReachedStackLimit() && energyStorage.getEnergyStored() >= (USAGE * TPS * TIME) / SPEED && !isCrafting) {
            progressCap.setProgress((int)(USAGE * TIME));
            isCrafting = true;
            setChanged();
        }
        if (hasRecipe() && isCrafting) {
            progressCap.addProgress((int)(-SPEED));;
            energyStorage.addEnergy(-USAGE);
            setChanged();

            if (progressCap.getProgress() <= 0) {
                itemHandler.extractItem(1, 1, false);
                itemHandler.setStackInSlot(2,
                    new ItemStack(itemHandler.getStackInSlot(0).getItem(), itemHandler.getStackInSlot(2).getCount() + 1));
                progressCap.setProgress((int)(USAGE * TIME));
                isCrafting = false;
                setChanged();
            }
        }
        else {
            progressCap.setProgress((int)(USAGE * TIME));
        }
    }

    private void checkRedstone() {
        if (level.hasNeighborSignal(worldPosition)) {
            signal = true;
        } else signal = false;
    }

    @Override
    public void load(CompoundTag tag) {
        if (tag.contains("Inventory")) {
            itemHandler.deserializeNBT(tag.getCompound("Inventory"));
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
        tag.put("Inventory", itemHandler.serializeNBT());
        tag.put("Energy", energyStorage.serializeNBT());
        tag.put("progress", progressCap.serializeNBT());

        CompoundTag infoTag = new CompoundTag();
        int tmp = 0;
        infoTag.putInt("Counter", tmp);
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

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(3) {

            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
    }

    private ProgressCapability createProgress() {
        return new ProgressCapability() {

            @Override
            public void onProgressChanged() {
                setChanged();
            }
        };
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return handler.cast();
        }
        if (cap == CapabilityEnergy.ENERGY) {
            return energy.cast();
        }
        if (cap == ProgressCapability.PROGRESS) {
            return progress.cast();
        }
        return super.getCapability(cap, side);
    }

    private boolean hasRecipe() {
        boolean a = isInArray(itemHandler.getStackInSlot(0).getTags().toArray(), Tags.Items.INGOTS);
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
        return (int)(TIME * TPS);
    }
}
