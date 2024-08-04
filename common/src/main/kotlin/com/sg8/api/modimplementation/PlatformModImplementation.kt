package com.sg8.api.modimplementation


abstract class PlatformModImplementation(protected val common: ModImplementation) {

    protected fun initializeCommon() = common.initialize(platformImplementation = this)

    /** Always call [initializeCommon] inside the platform implementation of [initialize] */
    abstract fun initialize()
    abstract fun initializeConfig()
    abstract fun registerEvents()

}
