package aaravgupta.asm

import aaravgupta.asm.swift.ASMCore
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object ASkyblockMod : ModInitializer {
    const val MOD_ID: String = "asm"

    val LOGGER = LoggerFactory.getLogger(MOD_ID)

    override fun onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        LOGGER.info("[ASM] Common init — Kotlin entrypoint is alive.")
        // Prove Java<->Kotlin interop: call into a Java helper.
        LOGGER.info("[ASM] Java helper says: {}", ASMJavaBridge.greeting())

        try {
            val swiftText = ASMCore.sendChatMessage("hi from swift!")
            LOGGER.info("[ASM] Swift built a Minecraft Component, got back: {}", swiftText)
        } catch (t: Throwable) {
            LOGGER.error("[ASM] Swift→Minecraft Component call failed", t)
        }
    }
}
