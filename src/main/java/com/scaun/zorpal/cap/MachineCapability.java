package com.scaun.zorpal.cap;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.INBTSerializable;

public class MachineCapability implements IMachine, INBTSerializable<CompoundTag> {

    private int progress;
    private boolean[] input;
    private boolean[] output;

    public static final Capability<IMachine> MACHINE = CapabilityManager.get(new CapabilityToken<>() {});
    public static void register(RegisterCapabilitiesEvent event)
    {
        event.register(IMachine.class);
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
    public void onChanged() {
        
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("progress", this.getProgress());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        int result = nbt.getInt("progress");
        setProgress(result);
    }

    
    @Override
    public boolean[] getInput() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setInput(boolean[] arr) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setInInput(int index, boolean val) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean[] getOutput() {
        return output;
    }

    @Override
    public void setOutput(boolean[] arr) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setInOutput(int index, boolean val) {
        // TODO Auto-generated method stub
        
    }

}
