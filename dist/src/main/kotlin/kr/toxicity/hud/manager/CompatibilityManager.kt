package kr.toxicity.hud.manager

import kr.toxicity.hud.api.update.UpdateEvent
import kr.toxicity.hud.compatibility.mmocore.MMOCoreCompatibility
import kr.toxicity.hud.compatibility.mmoitems.MMOItemsCompatibility
import kr.toxicity.hud.compatibility.mythiclib.MythicLibCompatibility
import kr.toxicity.hud.compatibility.mythicmobs.MythicMobsCompatibility
import kr.toxicity.hud.compatibility.parties.PartiesCompatibility
import kr.toxicity.hud.compatibility.skript.SkriptCompatibility
import kr.toxicity.hud.compatibility.vault.VaultCompatibility
import kr.toxicity.hud.compatibility.worldguard.WorldGuardCompatibility
import kr.toxicity.hud.resource.GlobalResource
import kr.toxicity.hud.util.CONSOLE
import kr.toxicity.hud.util.PLUGIN
import kr.toxicity.hud.util.runWithExceptionHandling
import net.kyori.adventure.audience.Audience
import org.bukkit.Bukkit
import java.util.function.Function

object CompatibilityManager: BetterHudManager {

    private val compatibilities = mapOf(
        "MMOCore" to {
            MMOCoreCompatibility()
        },
        "MythicMobs" to {
            MythicMobsCompatibility()
        },
        "WorldGuard" to {
            WorldGuardCompatibility()
        },
        "Vault" to {
            VaultCompatibility()
        },
        "MythicLib" to {
            MythicLibCompatibility()
        },
        "Skript" to {
            SkriptCompatibility()
        },
        "MMOItems" to {
            MMOItemsCompatibility()
        },
        "Parties" to {
            PartiesCompatibility()
        }
    )

    override fun start() {
        compatibilities.forEach {
            if (Bukkit.getPluginManager().isPluginEnabled(it.key)) {
                runWithExceptionHandling(CONSOLE, "Unable to load ${it.key} support.") {
                    val obj = it.value()
                    val namespace = it.key.lowercase()
                    obj.listeners.forEach { entry ->
                        PLUGIN.listenerManager.addListener("${namespace}_${entry.key}") { c ->
                            val reason = entry.value(c)
                            Function { u: UpdateEvent ->
                                reason(u)
                            }
                        }
                    }
                    obj.numbers.forEach { entry ->
                        PLUGIN.placeholderManager.numberContainer.addPlaceholder("${namespace}_${entry.key}", entry.value)
                    }
                    obj.strings.forEach { entry ->
                        PLUGIN.placeholderManager.stringContainer.addPlaceholder("${namespace}_${entry.key}", entry.value)
                    }
                    obj.booleans.forEach { entry ->
                        PLUGIN.placeholderManager.booleanContainer.addPlaceholder("${namespace}_${entry.key}", entry.value)
                    }
                    obj.triggers.forEach { entry ->
                        PLUGIN.triggerManager.addTrigger("${namespace}_${entry.key}", entry.value)
                    }
                }
            }
        }
    }

    override fun reload(sender: Audience, resource: GlobalResource) {
    }

    override fun end() {
    }
}