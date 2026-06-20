package aaravgupta.asm.client

import aaravgupta.asm.ASkyblockMod
import aaravgupta.asm.swift.ASMCore

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.ClientCommands
import net.minecraft.network.chat.Component

object ASkyblockModClient : ClientModInitializer {
    private var pendingOpen = false

    override fun onInitializeClient() {
        ASkyblockMod.LOGGER.info("[ASM] Client init — Kotlin client entrypoint is alive.")

        // Open the screen on the next tick, AFTER the chat screen has closed.
        ClientTickEvents.END_CLIENT_TICK.register { _ ->
            if (pendingOpen) {
                pendingOpen = false
                val result = ASMCore.openAsmScreen()
                ASkyblockMod.LOGGER.info("[ASM] openAsmScreen returned: {}", result)
            }
        }

        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(
                ClientCommands.literal("asm").executes { ctx ->
                    ASkyblockMod.LOGGER.info("[ASM] /asm executed — scheduling screen open")
                    pendingOpen = true
                    1
                }
            )
        }
    }
}
