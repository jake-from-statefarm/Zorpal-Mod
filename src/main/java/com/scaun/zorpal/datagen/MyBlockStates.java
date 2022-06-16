package com.scaun.zorpal.datagen;

import java.util.function.Function;

import com.scaun.zorpal.Zorpal;
import com.scaun.zorpal.setup.Registration;

import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class MyBlockStates extends BlockStateProvider {
    public MyBlockStates(DataGenerator gen, ExistingFileHelper helper) {
        super(gen, Zorpal.MODID, helper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(Registration.FIRST_BLOCK.get());
        simpleBlock(Registration.ZORPAL_ORE_STONE.get());
        simpleBlock(Registration.ZORPAL_BLOCK.get());
        
        registerPowergen(Registration.POWERGEN.get()); 
        
    }

    private void registerPowergen(Block block) {
        ResourceLocation off = new ResourceLocation(Zorpal.MODID, "block/powergen_off");
        ResourceLocation on = new ResourceLocation(Zorpal.MODID, "block/powergen_on");
        
        BlockModelBuilder modelOFF = models().cube("powergen_off", off, off, off, off, off, off);
        BlockModelBuilder modelON = models().cube("powergen_on", on, on, on, on, on, on);

        stateBlock(block, state -> {
            if (state.getValue(BlockStateProperties.POWERED)) {
                return modelON;
            } else return modelOFF;
        });
    }

    private void stateBlock(Block block, Function<BlockState, ModelFile> modelFunc) {
        getVariantBuilder(block)
            .forAllStates(state -> {
                return ConfiguredModel.builder()
                    .modelFile(modelFunc.apply(state))
                    .build();
            });
    }

    private void orientedBlock(Block block, Function<BlockState, ModelFile> modelFunc) {
        getVariantBuilder(block)
                .forAllStates(state -> {
                    Direction dir = state.getValue(BlockStateProperties.FACING);
                    return ConfiguredModel.builder()
                            .modelFile(modelFunc.apply(state))
                            .rotationX(dir.getAxis() == Direction.Axis.Y ?  dir.getAxisDirection().getStep() * -90 : 0)
                            .rotationY(dir.getAxis() != Direction.Axis.Y ? ((dir.get2DDataValue() + 2) % 4) * 90 : 0)
                            .build();
                });
    }
}
