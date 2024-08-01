package com.sg8.storage.factory

/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can get one at https://mozilla.org/MPL/2.0/.
 *
 * @author Hiroku
 * @since November 29th, 2021
 *
 * package com.cobblemon.mod.common.api.storage.factory
 * interface PokemonStoreFactory
 */

import com.sg8.storage.Store
import com.sg8.storage.StorePosition
import com.sg8.storage.TypeStored
import java.util.UUID

// Eight's implementation
interface StoreFactory {

    fun <P: StorePosition, T : TypeStored, S : Store<P, T>> getStore(
        storeClass: Class<out S>,
        storeID: UUID
    ): S?

    fun shutdown()

}
