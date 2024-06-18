package com.addontemplate.forge;

import dev.architectury.platform.forge.EventBuses;
import com.addontemplate.common.AddonTemplate;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@SuppressWarnings("unused")
@Mod(AddonTemplate.MOD_ID)
public class AddonTemplateForge
{
    public AddonTemplateForge() {
		// Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(AddonTemplate.MOD_ID,FMLJavaModLoadingContext.get().getModEventBus());
        AddonTemplate.init();
    }
}