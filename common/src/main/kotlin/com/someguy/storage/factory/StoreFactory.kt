package com.someguy.storage.factory

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
 * package com.cobblemon.mod.common.api.storage.factory
 * interface PokemonStoreFactory
 */

import com.someguy.storage.Store
import com.someguy.storage.StorePosition
import com.someguy.storage.ClassStored
import java.util.UUID

// Eight's implementation
interface StoreFactory {

    fun <P: StorePosition, C : ClassStored, St : Store<P, C>> getStore(
        storeClass: Class<out St>,
        storeID: UUID
    ): St?

    fun shutdown()

}
