package com.someguy.mod

import com.someguy.mod.commands.CommandManager
import dev.architectury.event.events.common.CommandRegistrationEvent
import dev.architectury.event.events.common.LifecycleEvent
import dev.architectury.utils.GameInstance
import net.minecraft.server.MinecraftServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class ModImplementation(val commandManager: CommandManager) {

    private var platformImplementation: PlatformModImplementation? = null

    val LOGGER: Logger = LoggerFactory.getLogger(this::class.simpleName)

    var server: MinecraftServer? = null
        get() {
            field ?: run { field = GameInstance.getServer() }
            return field
        }
        private set

    /**
     * If this method is overridden ->
     *
     * ALWAYS call `super.initialize()` inside new function implementation
     */
    open fun initialize(platformImplementation: PlatformModImplementation) {
        this.platformImplementation = platformImplementation
        this.platformImplementation!!.initializeConfig()
        this.platformImplementation!!.registerEvents()
        this.registerEvents()
    }

    /**
     * If this method is overridden ->
     *
     * ALWAYS call `super.registerEvents()` inside new function implementation
     */
    open fun registerEvents() {
        LifecycleEvent.SERVER_STARTING.register { this.server = it }
        CommandRegistrationEvent.EVENT.register(this.commandManager::registerCommands)
    }

}
