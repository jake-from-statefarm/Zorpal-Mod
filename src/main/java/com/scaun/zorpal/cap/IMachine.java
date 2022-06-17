package com.scaun.zorpal.cap;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface IMachine extends INBTSerializable<CompoundTag>{
    int getProgress();

    void setProgress(int i);

    void addProgress(int i);

    boolean[] getInput();

    void setInput(boolean[] arr);

    void setInInput(int index, boolean val);

    boolean[] getOutput();

    void setOutput(boolean[] arr);

    void setInOutput(int index, boolean val);

    void onChanged();
}
