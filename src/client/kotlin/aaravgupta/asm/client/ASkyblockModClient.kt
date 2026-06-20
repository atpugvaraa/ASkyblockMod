package aaravgupta.asm.client

import aaravgupta.asm.ASkyblockMod
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.ClientCommands
import net.minecraft.network.chat.Component

object ASkyblockModClient : ClientModInitializer {
    override fun onInitializeClient() {
        ASkyblockMod.LOGGER.info("[ASM] Client init — Kotlin client entrypoint is alive.")

        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            ASkyblockMod.LOGGER.info("[ASM] Registering /asm command now…")
            dispatcher.register(
                ClientCommands.literal("asm").executes { ctx ->
                    ASkyblockMod.LOGGER.info("[ASM] /asm command executed!")
                    ctx.source.sendFeedback(Component.literal("§aASM is loaded. Kotlin↔Java hybrid working."))
                    1
                }
            )
        }
    }
}
