package com.scaun.zorpal.cap;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface IMachine {
    int getProgress();

    void setProgress(int i);

    void addProgress(int i);

    boolean[] getInput();

    boolean getInInput(int index);

    void setInput(boolean[] arr);

    void setInInput(int index, boolean val);

    boolean[] getOutput();

    boolean getInOutput(int index);

    void setOutput(boolean[] arr);

    void setInOutput(int index, boolean val);

    int getCapacity();

    void setCapacity(int val);

    int getUsage();

    void setUsage(int val);

    float getSpeed();

    void setSpeed(float val);

    void onChanged();
}
