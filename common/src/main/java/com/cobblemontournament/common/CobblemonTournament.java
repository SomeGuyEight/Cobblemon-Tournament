package com.cobblemontournament.common;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.platform.events.PlatformEvents;
import com.cobblemonrental.common.CobblemonRental;
import kotlin.Unit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CobblemonTournament
{
	public static final String MOD_ID = "cobblemontournament";
	public static final Logger LOGGER = LoggerFactory.getLogger("cobblemontournament");
	public static CommonInterface implementation;
	
	public static void initialize(CommonInterface implementation)
	{
		implementation.initializeConfig();
		CobblemonTournament.implementation = implementation;
		implementation.registerCommands();
		implementation.registerEvents();
	}
	
	public static void registerEvents()
	{
		PlatformEvents.SERVER_STARTING.subscribe(Priority.HIGHEST,(event) -> {
			var server = event.getServer();
			CobblemonRental.instance.initialize(server);
			return Unit.INSTANCE;
		});
	}
	
}
