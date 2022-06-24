package com.scaun.zorpal.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.scaun.zorpal.tools.CircularLinkedNode;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;

public class MachineButton extends MyButton {

    private static int INACTIVE = 0;
    private static int INPUT = 1;
    private static int OUTPUT = 2;
    
    private CircularLinkedNode<ResourceLocation> imgNode;
    private CircularLinkedNode<Integer> state;

    private int entries;

    public MachineButton(ResourceLocation[] p_imgs, int p_x, int p_y, int p_w, int p_h) {
        super(p_imgs[0], p_x, p_y, p_w, p_h);

        entries = p_imgs.length;

        imgNode = new CircularLinkedNode<ResourceLocation>(p_imgs[0]);
        state = new CircularLinkedNode<Integer>(INACTIVE);

        for (int i = 1; i < p_imgs.length; i++) {
            imgNode.setNext(p_imgs[i]);
            imgNode = imgNode.getNext();
            
            state.setNext(i);
            state = state.getNext();
        }
        imgNode = imgNode.getNext();      // reset the circularlinkednodes to the "beginning"
        state = state.getNext();
    }

    public void cycle() {
        imgNode = imgNode.getNext();
        state = state.getNext();
    }

    public void cycleTo(int i) {
        if (0 <= i && i < entries) {
            while (state.getData() != i) {
                cycle();
            }
        }
    }

    public CircularLinkedNode<ResourceLocation> getImgNode() {
        return imgNode;
    }

    public int getState() {
        return state.getData();
    }

    @Override
    public void render(AbstractContainerScreen<?> p_screen, PoseStack p_matrixStack) {
        RenderSystem.setShaderTexture(0, imgNode.getData()); 
        p_screen.blit(p_matrixStack, x, y, 0, 0, 256, 256);
    }
}
