package com.scaun.zorpal.client;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.scaun.zorpal.Zorpal;
import com.scaun.zorpal.blocks.ZorpContainer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;



public class ZorpTransScreen extends AbstractContainerScreen<ZorpContainer> {

    private final ResourceLocation GUI = new ResourceLocation(Zorpal.MODID, "textures/gui/zorp_trans_gui.png");
    private final ResourceLocation ARR = new ResourceLocation(Zorpal.MODID, "textures/gui/zorp_trans/zorp_trans_arrow3.png");
    private final ResourceLocation SIDES_SMALL = new ResourceLocation(Zorpal.MODID, "textures/gui/zorp_trans/sides_s.png");
    private final ResourceLocation POWER_SMALL = new ResourceLocation(Zorpal.MODID, "textures/gui/zorp_trans/power_s.png");
    private final ResourceLocation SIDES_LARGE = new ResourceLocation(Zorpal.MODID, "textures/gui/zorp_trans/sides_l.png");
    private final ResourceLocation POWER_LARGE = new ResourceLocation(Zorpal.MODID, "textures/gui/zorp_trans/power_l.png");

    private final int SMALLX = -19;
    private final int SIDESY = 14;
    private final int POWERY = 35;

    private final int SMALLW = 21;
    private final int LARGEW = 62;
    private final int SMALLH = 20;
    private final int LARGEH = 60;

    private final int LARGEX = SMALLX - (LARGEW - SMALLW);
    private final int LARGEY = POWERY + (LARGEH - SMALLH);

    private boolean clicking = false;

    private MyButton sides = new MyButton(SIDES_SMALL, SMALLX, SIDESY, SMALLW, SMALLH);
    private MyButton power = new MyButton(POWER_SMALL, SMALLX, POWERY, SMALLW, SMALLH);

    public ZorpTransScreen(ZorpContainer container, Inventory inv, Component name) {
        super(container, inv, name);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        renderBars(matrixStack);
        renderButtons(matrixStack, mouseX, mouseY);

        if (clicking) clicking = false;
    }

    private void renderBars(PoseStack matrixStack) {
        Font font = Minecraft.getInstance().font;

        // bar is wxh px at xpos,ypos
        int xpos = 44;
        int ypos = 9;
        int w = 10;
        int h = 52;
        int o = 1; // amount to offset the "inner" energy bar
        int trueY = (int)(ypos + (h - ((float)h * energyPercent())));

        // progress is at 8x7 at 79, 30
        int pxpos = 80;
        int pypos = 31;
        int pw = 8;
        int ph = 8;
        int ptrueY = (int)(pypos + (ph - (float)ph * progressPercent()));

        DecimalFormat oneDecimal = new DecimalFormat("#.0");

        String percentString = oneDecimal.format((energyPercent() * 100)) + "% -";

        float fscale = 0.8f;
        float fx = 38.0f;
        float fy = (float)trueY - 1.0f;
        float fw = font.width(percentString) * fscale;

        matrixStack.scale(fscale, fscale, fscale);
        drawString(matrixStack, font, percentString, (int)((fx / fscale) - fw), (int)(fy / fscale), 0xffffff);
        matrixStack.scale(1/fscale, 1/fscale, 1/fscale);

        // Draw the "energy bar"
        //                x1        y2     x2            y2         color
        fill(matrixStack, xpos,     trueY, xpos + w,     ypos + h,  0xffff0000);
        fill(matrixStack, xpos + o, trueY, xpos + w - o, ypos + h,  0xffff4040);
        // Draw the "progress bar"
        fill(matrixStack, pxpos,    pypos, pxpos + pw,   ptrueY,    0xffffffff);
        RenderSystem.setShaderTexture(0, ARR);
        int relX = 1; // (this.width - this.imageWidth) / 2;
        int relY = 1; // (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }

    private void renderButtons(PoseStack matrixStack, int mouseX, int mouseY) {
        int mx = mouseX - getGuiLeft();
        int my = mouseY - getGuiTop();


        // handle collision and resizing of the "sides" button
        if (sides.isColliding(mx, my)) {
            renderSides();
            if (sides.getMode() == MyButton.SMALL) {
                sides.setImg(SIDES_LARGE);
                sides.changeSize(LARGEW, LARGEH);

                power.setY(LARGEY);
            }
        } else if (sides.getMode() == MyButton.LARGE) {
            sides.setImg(SIDES_SMALL);
            sides.changeSize(SMALLW, SMALLH);
            
            power.setY(POWERY);
        }

        // handle collision and resizing of the "power" button
        if (power.isColliding(mx, my)) {
            renderPower();
            if (power.getMode() == MyButton.SMALL) {
                power.setImg(POWER_LARGE);
                power.changeSize(LARGEW, LARGEH);
            }
        } else if (power.getMode() == MyButton.LARGE) {
            power.setImg(POWER_SMALL);
            power.changeSize(SMALLW, SMALLH);
        }

        sides.render(this, matrixStack);
        power.render(this, matrixStack);
    }

    private void renderSides() {
    }

    private void renderPower() {
        
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, GUI);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        // String str = relX + ", " + relY + ": " + this.imageWidth + "x" + this.imageHeight;
        // System.out.println(str);
        this.blit(matrixStack, relX, relY, 0, 0, 256, 256);
    }

    @Override
    public boolean mouseClicked(double p_97748_, double p_97749_, int p_97750_) {
        clicking = true; 
        return super.mouseClicked(p_97748_, p_97749_, p_97750_);
    }

    private float energyPercent() {
        return (float)menu.getEnergy() / (float)menu.getMaxEnergy();
    }

    private float progressPercent() {
        //System.out.println("CLIENT: " + menu.getCounter());
        return (float)menu.getCounter() / (float)menu.getCounterMax();
    }
    
}
