package com.someguy.api

abstract class PlatformModImplementation(
    protected val common: ModImplementation )
{
    /** Always call in [initialize] */
    protected fun initializeCommon() = common.initialize( platformImplementation = this )

    /** Always call [initializeCommon] inside the platform implementation of [initialize] */
    abstract fun initialize()
    abstract fun initializeConfig()
    abstract fun registerEvents()
}
