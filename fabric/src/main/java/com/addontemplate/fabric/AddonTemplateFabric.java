package com.addontemplate.fabric;

import com.addontemplate.common.AddonTemplate;
import net.fabricmc.api.ModInitializer;

public class AddonTemplateFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        AddonTemplate.init();
    }
}