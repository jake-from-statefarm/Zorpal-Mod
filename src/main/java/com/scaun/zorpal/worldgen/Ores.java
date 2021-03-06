package com.scaun.zorpal.worldgen;

import com.scaun.zorpal.setup.Registration;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraftforge.event.world.BiomeLoadingEvent;

public class Ores {
    
    public static final int NORMAL_VEINSIZE = 6;
    public static final int NORMAL_AMOUNT = 5;
    public static final int DEEPSLATE_VEINSIZE = 4;
    public static final int DEEPSLATE_AMOUNT = 6;
    public static final int DENSE_VEINSIZE = 4;
    public static final int DENSE_AMOUNT = 5;
    
    public static Holder<PlacedFeature> NORMAL_OREGEN;
    public static Holder<PlacedFeature> DEEPSLATE_OREGEN;
    public static Holder<PlacedFeature> DENSE_OREGEN;

    public static void registerConfiguredFeatures() {
        OreConfiguration normalConfig = new OreConfiguration(
            OreFeatures.STONE_ORE_REPLACEABLES,
            Registration.ZORPAL_ORE_STONE.get().defaultBlockState(),
            NORMAL_VEINSIZE
        );
        NORMAL_OREGEN = registerPlacedFeature(
            "normal_zorpal_ore", 
            new ConfiguredFeature<>(Feature.ORE, normalConfig),
            CountPlacement.of(NORMAL_AMOUNT),
            InSquarePlacement.spread(),
            BiomeFilter.biome(),
            HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(20))
        );

        OreConfiguration deepslateConfig = new OreConfiguration(
            OreFeatures.DEEPSLATE_ORE_REPLACEABLES,
            Registration.ZORPAL_ORE_DEEPSLATE.get().defaultBlockState(),
            DEEPSLATE_VEINSIZE
        );
        DEEPSLATE_OREGEN = registerPlacedFeature(
            "deepslate_zorpal_ore", 
            new ConfiguredFeature<>(Feature.ORE, deepslateConfig),
            CountPlacement.of(DEEPSLATE_AMOUNT),
            InSquarePlacement.spread(),
            BiomeFilter.biome(),
            HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(15), VerticalAnchor.absolute(0))
        );

        OreConfiguration denseConfig = new OreConfiguration(
            OreFeatures.DEEPSLATE_ORE_REPLACEABLES,
            Registration.ZORPAL_ORE_DENSE.get().defaultBlockState(),
            DENSE_VEINSIZE
        );
        DENSE_OREGEN = registerPlacedFeature(
            "dense_zorpal_ore", 
            new ConfiguredFeature<>(Feature.ORE, denseConfig),
            CountPlacement.of(DENSE_AMOUNT),
            InSquarePlacement.spread(),
            BiomeFilter.biome(),
            HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(0), VerticalAnchor.aboveBottom(15))
        );
    }

    private static <C extends FeatureConfiguration, F extends Feature<C>> Holder<PlacedFeature> registerPlacedFeature(String registryName, ConfiguredFeature<C, F> feature, PlacementModifier... placementModifiers) {
        return PlacementUtils.register(registryName, Holder.direct(feature), placementModifiers);
    }

    public static void onBiomeLoadingEvent(BiomeLoadingEvent event) {
        if (event.getCategory() == Biome.BiomeCategory.NETHER) {

        } else if (event.getCategory() == Biome.BiomeCategory.THEEND) {
            
        } else {
            event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, NORMAL_OREGEN);
            event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, DEEPSLATE_OREGEN);
            event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, DENSE_OREGEN);
        }
    }
}
