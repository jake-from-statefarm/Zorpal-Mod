package com.scaun.zorpal.cap;

import com.scaun.zorpal.tools.CustomEnergyStorage;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.INBTSerializable;

public class MachineCapability extends CustomEnergyStorage implements IMachine {



    private int progress;

    private boolean[] input = new boolean[6];
    private boolean[] output = new boolean[6];

    private int capacity;
    private int usage;
    private float speed;

    public static final Capability<IMachine> MACHINE = CapabilityManager.get(new CapabilityToken<>() {});
    public static void register(RegisterCapabilitiesEvent event)
    {
        event.register(IMachine.class);
    }

    public MachineCapability(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
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
        
    }

    @Override
    public void setInInput(int index, boolean val) {
        input[index] = val;
        
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
    }

    @Override
    public void setInOutput(int index, boolean val) {
        output[index] = val;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public void setCapacity(int val) {
        capacity = val;
        
    }

    @Override
    public int getUsage() {
        return usage;
    }

    @Override
    public void setUsage(int val) {
        usage = val;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public void setSpeed(float val) {
        speed = val;
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
        int result = nbt.getInt("progress");
        setProgress(result);
        result = nbt.getInt("energy");
        setEnergy(result);
    }
}
