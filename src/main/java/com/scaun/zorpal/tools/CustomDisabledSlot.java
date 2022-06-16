package com.scaun.zorpal.tools;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraft.world.item.ItemStack;

public class CustomDisabledSlot extends SlotItemHandler {

    public CustomDisabledSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        //TODO Auto-generated constructor stub
        SlotItemHandler a = new SlotItemHandler(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }
}
