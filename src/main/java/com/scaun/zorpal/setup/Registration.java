package com.scaun.zorpal.setup;

import com.scaun.zorpal.Zorpal;
import com.scaun.zorpal.blocks.PowergenBE;
import com.scaun.zorpal.blocks.PowergenBlock;
import com.scaun.zorpal.blocks.PowergenContainer;
import com.scaun.zorpal.blocks.ZorpBE;
import com.scaun.zorpal.blocks.ZorpBlock;
import com.scaun.zorpal.blocks.ZorpContainer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Registration {

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Zorpal.MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Zorpal.MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Zorpal.MODID);
    private static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, Zorpal.MODID);

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        CONTAINERS.register(bus);
    }

    // PROPERTIES
    public static final BlockBehaviour.Properties FIRST_PROPERTIES = BlockBehaviour.Properties.of(Material.STONE).strength(2f).requiresCorrectToolForDrops();
    public static final BlockBehaviour.Properties STRONG_ORE_PROPERTIES = BlockBehaviour.Properties.of(Material.STONE).strength(3f).requiresCorrectToolForDrops();
    public static final Item.Properties ITEM_PROPERTIES = new Item.Properties().tab(ModSetup.ITEM_GROUP);

    // TAGS
    public static final TagKey<Block> ZORPAL_ORE = BlockTags.create(new ResourceLocation(Zorpal.MODID, "zorpal_ore"));
    public static final TagKey<Item> ZORPAL_ORE_ITEM = ItemTags.create(new ResourceLocation(Zorpal.MODID, "zorpal_ore"));

    // BLOCKS AND ITEMS
        // FIRST BLOCK
    public static final RegistryObject<Block> FIRST_BLOCK = BLOCKS.register("erics_first_block", () -> new Block(FIRST_PROPERTIES));
    public static final RegistryObject<Item> FIRST_BLOCK_ITEM = fromBlock(FIRST_BLOCK);
        // ZORPAL STUFF
    public static final RegistryObject<Block> ZORPAL_ORE_STONE = BLOCKS.register("zorpal_ore", () -> new Block(STRONG_ORE_PROPERTIES));
    public static final RegistryObject<Item> ZORPAL_ORE_STONE_ITEM = fromBlock(ZORPAL_ORE_STONE);
    public static final RegistryObject<Item> RAW_ZORP = ITEMS.register("raw_zorp", () -> new Item(ITEM_PROPERTIES));
    public static final RegistryObject<Item> ZORP_INGOT = ITEMS.register("zorp_ingot", () -> new Item(ITEM_PROPERTIES));
    public static final RegistryObject<Block> ZORPAL_BLOCK = BLOCKS.register("zorpal_block", () -> new Block(STRONG_ORE_PROPERTIES));
    public static final RegistryObject<Item> ZORPAL_BLOCK_ITEM = fromBlock(ZORPAL_BLOCK);
        // POWERGEN
    public static final RegistryObject<Block> POWERGEN = BLOCKS.register("powergen", PowergenBlock::new);
    public static final RegistryObject<Item> POWERGEN_ITEM = fromBlock(POWERGEN);
    public static final RegistryObject<BlockEntityType<PowergenBE>> POWERGEN_BE =
            BLOCK_ENTITIES.register("powergen", () -> BlockEntityType.Builder.of(PowergenBE::new, POWERGEN.get()).build(null));
    public static final RegistryObject<MenuType<PowergenContainer>> POWERGEN_CONTAINER =
            CONTAINERS.register("powergen", () -> IForgeMenuType.create(((windowId, inv, data) -> new PowergenContainer(windowId, data.readBlockPos(), inv, inv.player))));
        // ZORPAL TRANSFUSER
    public static final RegistryObject<Block> ZORP_TRANS = BLOCKS.register("zorp_trans", ZorpBlock::new);
    public static final RegistryObject<Item> ZORP_TRANS_ITEM = fromBlock(ZORP_TRANS);
    public static final RegistryObject<BlockEntityType<ZorpBE>> ZORP_TRANS_BE =
        BLOCK_ENTITIES.register("zorp_trans", () -> BlockEntityType.Builder.of(ZorpBE::new, ZORP_TRANS.get()).build(null));
    public static final RegistryObject<MenuType<ZorpContainer>> ZORP_TRANS_CONTAINER =
        CONTAINERS.register("zorp_trans", () -> IForgeMenuType.create(((windowId, inv, data) -> new ZorpContainer(windowId, data.readBlockPos(), inv, inv.player))));



    public static <B extends Block> RegistryObject<Item> fromBlock(RegistryObject<B> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), ITEM_PROPERTIES));
    }
}
