package com.scaun.zorpal.cap;

import com.scaun.zorpal.tools.CustomEnergyStorage;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.IEnergyStorage;

public class MachineCapability implements IMachine, IEnergyStorage, INBTSerializable<CompoundTag> {

    private int progress;

    private boolean[] input = new boolean[6];
    private boolean[] output = new boolean[6];

    private int energy;
    private int capacity;
    private int maxTransfer;
    private int usage;
    private float speed;

    public static final Capability<IMachine> MACHINE = CapabilityManager.get(new CapabilityToken<>() {});
    public static void register(RegisterCapabilitiesEvent event)
    {
        event.register(IMachine.class);
    }

    public MachineCapability(int capacity, int maxTransfer, int usage, float speed) {
        this.capacity = capacity;
        this.maxTransfer = maxTransfer;
        this.usage = usage;
        this.speed = speed;
        this.energy = Math.max(0 , Math.min(capacity, energy));
    }

    public void incProcress() {
        this.progress -= this.speed;
        onChanged();
    }

    @Override
    public int getProgress() {
        return progress;
    }

    @Override
    public void setProgress(int i) {
        this.progress = i;
        onChanged();
        
    }

    @Override
    public void addProgress(int i) {
        this.progress += i;
        onChanged();
    }

    @Override
    public boolean[] getInput() {
        return input;
    }

    public boolean getInInput(int index) {
        return input[index];
    }

    @Override
    public void setInput(boolean[] arr) {
        input = arr;
        onChanged();
    }

    @Override
    public void setInInput(int index, boolean val) {
        input[index] = val;
        onChanged();
    }

    @Override
    public boolean[] getOutput() {
        return output;
    }

    public boolean getInOutput(int index) {
        return output[index];
    }

    @Override
    public void setOutput(boolean[] arr) {
        output = arr;
        onChanged();
    }

    @Override
    public void setInOutput(int index, boolean val) {
        output[index] = val;
        onChanged();
    }

    @Override
    public int getUsage() {
        return usage;
    }

    @Override
    public void setUsage(int val) {
        usage = val;
        onChanged();
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public void setSpeed(float val) {
        speed = val;
        onChanged();
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return this.maxTransfer > 0;
    }

    public void use() {
    }

    @Override
    public void onChanged() {
        
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("progress", this.progress);
        tag.putInt("energy", this.energy);
        return tag;
    }

    public void deserializeNBT(CompoundTag nbt) {
        setProgress(nbt.getInt("progress"));
        setEnergy(nbt.getInt("energy"));
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive())
        return 0;

        int energyReceived = Math.min(capacity - energy, Math.min(this.maxTransfer, maxReceive));
        if (!simulate)
            energy += energyReceived;
        return energyReceived;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
        onChanged();
    }

    public void addEnergy(int energy) {
        this.energy += energy;
        if (this.energy > getMaxEnergyStored()) {
            this.energy = getMaxEnergyStored();
        }
        onChanged();
    }

    public void consumeEnergy(int energy) {
        this.energy -= energy;
        if (this.energy < 0) {
            this.energy = 0;
        }
        onChanged();
    }
}
