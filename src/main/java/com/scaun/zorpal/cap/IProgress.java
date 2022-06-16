package com.scaun.zorpal.cap;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface IProgress extends INBTSerializable<CompoundTag>{
    int getProgress();

    void setProgress(int i);

    void addProgress(int i);

    void onProgressChanged();
}
