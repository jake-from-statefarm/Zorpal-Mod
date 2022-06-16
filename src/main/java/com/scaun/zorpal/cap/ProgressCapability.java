package com.scaun.zorpal.cap;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.INBTSerializable;

public class ProgressCapability implements IProgress, INBTSerializable<CompoundTag> {

    private int progress;

    public static final Capability<IProgress> PROGRESS = CapabilityManager.get(new CapabilityToken<>() {});
    public static void register(RegisterCapabilitiesEvent event)
    {
        event.register(IProgress.class);
    }

    @Override
    public int getProgress() {
        return progress;
    }

    @Override
    public void setProgress(int i) {
        this.progress = i;
        onProgressChanged();
        
    }

    @Override
    public void addProgress(int i) {
        this.progress += i;
        onProgressChanged();
        
    }

    @Override
    public void onProgressChanged() {
        
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
    
}
