package com.scaun.zorpal.cap;


public interface IMachine {
    int getProgress();

    void setProgress(int i);

    void addProgress(int i);

    int[] getSides();

    int getInSides(int index);

    void setSides(int[] arr);

    void setInSides(int index, int val);

    int getUsage();

    void setUsage(int val);

    float getSpeed();

    void setSpeed(float val);

    void onChanged();
}
