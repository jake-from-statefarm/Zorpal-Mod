package com.scaun.zorpal.client;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;

public class MyButton {

    public static final int SMALL = 0;
    public static final int LARGE = 1;

    protected int x, y, w, h;
    protected ResourceLocation img;

    private int mode = SMALL;

    public MyButton(ResourceLocation p_img, int p_x, int p_y, int p_w, int p_h) {
        x = p_x;
        y = p_y;
        w = p_w;
        h = p_h;

        img = p_img;
    }

    public boolean isColliding(int p_x, int p_y) {
        return (x <= p_x && p_x <= (x + w)) && 
               (y <= p_y && p_y <= (y + h));
    }

    public void changeSize(int p_w, int p_h) {
        if(w <= p_w) mode = LARGE;
        else mode = SMALL;
        
        x += w - p_w;
        w = p_w;
        h = p_h;


    }

    public void render(AbstractContainerScreen<?> p_screen, PoseStack p_matrixStack) {
        RenderSystem.setShaderTexture(0, img); 
        p_screen.blit(p_matrixStack, x, y, 0, 0, 256, 256);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public void setX(int p) {
        x = p;
    }

    public void setY(int p) {
        y = p;
    }

    public void setW(int p) {
        w = p;
    }

    public void setH(int p) {
        h = p;
    }

    public ResourceLocation getImg() {
        return img;
    }

    public void setImg(ResourceLocation p_new) {
        img = p_new;
    }

    public int getMode() {
        return mode;
    }

}
