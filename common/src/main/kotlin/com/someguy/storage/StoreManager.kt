package com.someguy.storage

// From the Cobblemon Mod I adapted this script from
/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * @author Hiroku
 * @since November 29th, 2021
 *
 * package com.cobblemon.mod.common.api.storage
 * open class PokemonStoreManager
 */

import com.cobblemon.mod.common.api.PrioritizedList
import com.cobblemon.mod.common.api.Priority
import com.someguy.storage.factory.StoreFactory
import java.util.UUID

// Eight's implementation below
open class StoreManager {

    private val factories = PrioritizedList<StoreFactory>()

    open fun registerFactory(priority: Priority, factory: StoreFactory) =
        factories.add(priority = priority, value = factory)

    open fun unregisterFactory(factory: StoreFactory) {
        factory.shutdown()
        factories.remove(value = factory)
    }

    open fun unregisterAll() = factories.toList().forEach(::unregisterFactory)

    open fun <P : StorePosition, C : ClassStored,St : Store<P, C>> getStore(
        storeClass: Class<out St>,
        uuid: UUID,
    ): St? {
        for (factory in factories) {
            factory
                .getStore(storeClass = storeClass, storeID = uuid)
                ?.run { return this }
        }
        return null
    }

}
