package plus.dragons.createenchantmentindustry;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import plus.dragons.createenchantmentindustry.content.contraptions.fluids.OpenEndedPipeEffects;
import plus.dragons.createenchantmentindustry.entry.*;
import plus.dragons.createenchantmentindustry.foundation.config.CeiConfigs;
import plus.dragons.createenchantmentindustry.foundation.data.advancement.CeiAdvancements;
import plus.dragons.createenchantmentindustry.foundation.data.advancement.CeiTriggers;
import plus.dragons.createenchantmentindustry.foundation.data.lang.LangMerger;

@Mod(EnchantmentIndustry.ID)
public class EnchantmentIndustry {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String ID = "create_enchantment_industry";
    private static final NonNullSupplier<CreateRegistrate> REGISTRATE = CreateRegistrate.lazy(ID);

    public EnchantmentIndustry() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
    
        CeiConfigs.register(ModLoadingContext.get());
        
        registerEntries(modEventBus);
        registerForgeEvents(forgeEventBus);
        
        modEventBus.addListener(EnchantmentIndustry::setup);
        modEventBus.addListener(EnchantmentIndustry::datagen);
        
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> EnchantmentIndustryClient.onClient(modEventBus, forgeEventBus));
    }

    private void registerEntries(IEventBus modEventBus) {
        CeiBlocks.register();
        CeiBlockEntities.register();
        CeiContainerTypes.register();
        CeiEntityTypes.register();
        CeiFluids.register();
        CeiItems.register();
        CeiRecipeTypes.register(modEventBus);
        CeiTags.register();
    }
    
    private void registerForgeEvents(IEventBus forgeEventBus) {
        forgeEventBus.addGenericListener(BlockEntityType.class, CeiBlockEntities::remap);
        forgeEventBus.addListener(CeiFluids::handleInkEffect);
        forgeEventBus.addListener(CeiItems::fillCreateItemGroup);
    }

    public static void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CeiPackets.registerPackets();
            CeiAdvancements.register();
            CeiTriggers.register();
            OpenEndedPipeEffects.register();
        });
    }
    
    public static void datagen(final GatherDataEvent event) {
        DataGenerator datagen = event.getGenerator();
        datagen.addProvider(new LangMerger(datagen));
        datagen.addProvider(new CeiAdvancements(datagen));
    }

    public static ResourceLocation genRL(String name) {
        return new ResourceLocation(ID, name);
    }

    public static CreateRegistrate registrate() {
        return REGISTRATE.get();
    }
}
