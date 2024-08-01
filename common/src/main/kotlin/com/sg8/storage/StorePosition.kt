package com.sg8.storage

import java.util.UUID

// From the Cobblemon Mod this is from
/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can get one at https://mozilla.org/MPL/2.0/.
 */
// package com.cobblemon.mod.common.api.storage

interface StorePosition

data class UuidPosition(val uuid: UUID) : StorePosition

data class IntPosition(val int: Int) : StorePosition

data class Vector2Position(val x: Int,val y: Int) : StorePosition

data class Vector3Position(val x: Int,val y: Int,val z: Int) : StorePosition
