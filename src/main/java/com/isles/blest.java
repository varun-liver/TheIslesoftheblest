package com.isles;

import com.isles.client.renderer.SkyGuardianRenderer;
import com.isles.client.renderer.SkyGuardianModel;
import com.isles.entity.SkyGuardianEntity;
import com.isles.client.renderer.TheInfectionModel;
import com.isles.client.renderer.TheInfectionRenderer;
import com.isles.entity.TheInfectionEntity;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.TierSortingRegistry;

import java.util.List;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(blest.MODID)
public class blest {

    // Define mod id in a common place for everything to reference.
    public static final String MODID = "theislesoftheblest";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "theislesoftheblest" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "theislesoftheblest" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // Create a Deferred Register to hold EntityTypes which will all be registered under the "theislesoftheblest" namespace
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "theislesoftheblest" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    //----------------------------------BLOCK/ITEM REG----------------------------------
    //----------------------------------BLOCK/ITEM REG----------------------------------
    // Creates a new Block with the id "theislesoftheblest:sky_grass", combining the namespace and path
    public static final RegistryObject<Block> sky_grass = BLOCKS.register("sky_grass", () -> new Block(
            BlockBehaviour.Properties.of()
                    .strength(0.8f,1.0f)
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()));
    // Creates a new BlockItem with the id "theislesoftheblest:sky_grass", combining the namespace and path
    public static final RegistryObject<Item> sky_grass_ITEM = ITEMS.register("sky_grass", () -> new BlockItem(sky_grass.get(), new Item.Properties()));
    public static final RegistryObject<Block> sky_crystal = BLOCKS.register("sky_crystal", () -> new Block(
            BlockBehaviour.Properties.of()
                    .strength(3.0f, 3.0f)
                    .mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> sky_crystal_ITEM = ITEMS.register("sky_crystal", () -> new BlockItem(sky_crystal.get(), new Item.Properties()));
    //----------------------------------ITEM REG----------------------------------
    //----------------------------------ITEM REG----------------------------------
    // Creates a new food item with the id "theislesoftheblest:example_id", nutrition 1 and saturation 2
    public static final RegistryObject<Item> golden_cherry = ITEMS.register("golden_cherry", () -> new Item(new Item.Properties().food(new FoodProperties.Builder().alwaysEat().nutrition(3).saturationMod(2f).build())));
    public static final RegistryObject<Item> sky_catalyst = ITEMS.register(
            "sky_catalyst",
            () -> new Item(new Item.Properties())
    );
    public static final RegistryObject<Item> unshaped_sky_catalyst = ITEMS.register(
            "unshaped_sky_catalyst",
            () -> new Item(new Item.Properties())
    );
    public static final RegistryObject<EntityType<SkyGuardianEntity>> sky_guardian = ENTITY_TYPES.register("sky_guardian",
            () -> EntityType.Builder.of(SkyGuardianEntity::new, MobCategory.MONSTER)
                    .sized(0.9F, 1.3F)
                    .build(MODID + ":sky_guardian"));
    public static final RegistryObject<EntityType<TheInfectionEntity>> the_infection = ENTITY_TYPES.register("the_infection",
            () -> EntityType.Builder.of(TheInfectionEntity::new, MobCategory.MONSTER)
                    .sized(0.9F, 1.3F)
                    .build(MODID + ":the_infection"));
    public static final RegistryObject<Item> sky_guardian_spawn_egg = ITEMS.register("sky_guardian_spawn_egg",
            () -> new ForgeSpawnEggItem(sky_guardian, 0x9dd5ef, 0x1c5f87, new Item.Properties()));
    public static final RegistryObject<Item> the_infection_spawn_egg = ITEMS.register("the_infection_spawn_egg",
            () -> new ForgeSpawnEggItem(the_infection, 0x4f5e3f, 0x101011, new Item.Properties()));
    //----------------------------------SKY TIER----------------------------------
    //----------------------------------SKY TIER----------------------------------
    private static final TagKey<Block> INCORRECT_FOR_SKY_TOOL =
            BlockTags.create(ResourceLocation.fromNamespaceAndPath("minecraft", "incorrect_for_iron_tool"));
    private static final TagKey<Block> INCORRECT_FOR_Legendary_Tier =
            BlockTags.create(ResourceLocation.fromNamespaceAndPath("minecraft","incorrect_for_netherite_tool"));
    public static final Tier SKY_TIER = TierSortingRegistry.registerTier(
            new ForgeTier(
                    2,          // harvest level (2 = iron)
                    750,        // durability
                    8.0F,       // mining speed
                    3.0F,       // attack damage bonus
                    18,         // enchantability
                    INCORRECT_FOR_SKY_TOOL,
                    () -> Ingredient.of(sky_catalyst.get())
            ),
            ResourceLocation.fromNamespaceAndPath(MODID, "sky_tier"),
            List.of(net.minecraft.world.item.Tiers.IRON),      // after iron
            List.of(net.minecraft.world.item.Tiers.DIAMOND)    // before diamond
    );
    public static final  Tier Legendary_Tier = TierSortingRegistry.registerTier(
            new ForgeTier(
                    5,
                    10000,
                    10F,
                    20F,
                    100,
                    INCORRECT_FOR_Legendary_Tier,
                    () -> Ingredient.of(sky_catalyst.get())
            ),
            ResourceLocation.fromNamespaceAndPath(MODID,"lengendary_tier"),
            List.of(Tiers.NETHERITE),List.of()
            );
    public static final RegistryObject<Item> SKY_SWORD = ITEMS.register("sky_sword",
            () -> new SwordItem(SKY_TIER, 3, -2.4F, new Item.Properties()));

    public static final RegistryObject<Item> SKY_PICKAXE = ITEMS.register("sky_pickaxe",
            () -> new PickaxeItem(SKY_TIER, 1, -2.8F, new Item.Properties()));

    public static final RegistryObject<Item> SKY_AXE = ITEMS.register("sky_axe",
            () -> new AxeItem(SKY_TIER, 5.0F, -3.0F, new Item.Properties()));

    public static final RegistryObject<Item> SKY_SHOVEL = ITEMS.register("sky_shovel",
            () -> new ShovelItem(SKY_TIER, 1.5F, -3.0F, new Item.Properties()));

    public static final RegistryObject<Item> SKY_HOE = ITEMS.register("sky_hoe",
            () -> new HoeItem(SKY_TIER, -2, 0.0F, new Item.Properties()));
    public static final RegistryObject<Item> Harpe = ITEMS.register("harpe",
            ()-> new SwordItem(Legendary_Tier,5,-2F,new Item.Properties()));
    // Creates a creative tab with the id "theislesoftheblest:example_tab" for the example item, that is placed after the combat tab
    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder().withTabsBefore(CreativeModeTabs.COMBAT).icon(() -> sky_grass_ITEM.get().getDefaultInstance()).displayItems((parameters, output) -> {
        output.accept(sky_grass_ITEM.get());
        output.accept(sky_crystal_ITEM.get());
        output.accept(golden_cherry.get());
        output.accept(sky_guardian_spawn_egg.get());
        output.accept(the_infection_spawn_egg.get());
        output.accept(sky_catalyst.get());
        output.accept(unshaped_sky_catalyst.get());
        output.accept(SKY_SWORD.get());
        output.accept(SKY_PICKAXE.get());
        output.accept(SKY_AXE.get());
        output.accept(SKY_SHOVEL.get());
        output.accept(SKY_HOE.get());
    }).build());
    public blest() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so entities get registered
        ENTITY_TYPES.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::registerAttributes);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        if (Config.logDirtBlock) LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));

        event.enqueueWork(() -> {
            ResourceKey<Biome> skyForest = ResourceKey.create(
                    Registries.BIOME,
                    ResourceLocation.fromNamespaceAndPath(MODID, "sky_forest")
            );
            BiomeManager.addBiome(BiomeManager.BiomeType.COOL, new BiomeManager.BiomeEntry(skyForest, 8));
        });
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) event.accept(sky_grass_ITEM);
        if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) event.accept(sky_crystal_ITEM.get());
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) event.accept(sky_catalyst.get());
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) event.accept(unshaped_sky_catalyst.get());
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) event.accept(sky_guardian_spawn_egg);
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) event.accept(the_infection_spawn_egg);
    }

    private void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(sky_guardian.get(), SkyGuardianEntity.createAttributes().build());
        event.put(the_infection.get(), TheInfectionEntity.createAttributes().build());
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(sky_guardian.get(), SkyGuardianRenderer::new);
            event.registerEntityRenderer(the_infection.get(), TheInfectionRenderer::new);
        }

        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(SkyGuardianModel.LAYER_LOCATION, SkyGuardianModel::createBodyLayer);
            event.registerLayerDefinition(TheInfectionModel.LAYER_LOCATION, TheInfectionModel::createBodyLayer);
        }
    }
}
