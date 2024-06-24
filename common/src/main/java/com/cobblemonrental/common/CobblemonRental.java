package com.cobblemonrental.common;

import net.minecraft.server.MinecraftServer;

public final class CobblemonRental
{
    public static final CobblemonRental instance = new CobblemonRental();
    
    private MinecraftServer server;
    
    public MinecraftServer getServer() { return server; }
    
    public void initialize(MinecraftServer server)
    {
        RentalManager.instance.initialize(server);
    }

}
