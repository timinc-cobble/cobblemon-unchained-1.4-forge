package us.timinc.mc.cobblemon.chaining.modules

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.world.entity.player.Player
import us.timinc.mc.cobblemon.chaining.config.HiddenBoostConfig
import us.timinc.mc.cobblemon.chaining.util.Util
import kotlin.random.Random.Default.nextInt

class HiddenBooster(private val config: HiddenBoostConfig) : SpawnActionModifier("hiddenBooster") {
    override fun act(entity: PokemonEntity, ctx: SpawningContext) {
        val pokemon = entity.pokemon
        val species = pokemon.species.name.lowercase()

        info("$species spawned at ${ctx.position.toShortString()}", config.debug)

        if (!Util.matchesList(pokemon, config.whitelist, config.blacklist)) {
            info("$species is blocked by the blacklist", config.debug)
            return
        }

        val nearbyPlayers = getNearbyPlayers(ctx, config.effectiveRange.toDouble())
        info("nearby players: ${nearbyPlayers.size} ${
            nearbyPlayers.map { player: Player ->
                "${player.name.string}:${
                    config.getPoints(
                        player, species
                    )
                }"
            }
        }", config.debug)
        val possibleMaxPlayer = nearbyPlayers.stream().max(Comparator.comparingInt { player: Player? ->
            config.getPoints(player!!, species)
        })
        if (possibleMaxPlayer.isEmpty) {
            info("conclusion: no nearby players", config.debug)
            return
        }

        val maxPlayer = possibleMaxPlayer.get()
        val points = config.getPoints(maxPlayer, species)
        val goodMarbles = config.getThreshold(points)
        val totalMarbles = config.marbles

        if (goodMarbles == 0) {
            info("${maxPlayer.name.string} wins with $points points, has no chance", config.debug)
            info("conclusion: winning player didn't get any hidden ability chance", config.debug)
            return
        }

        val hiddenAbilityRoll = nextInt(0, totalMarbles)
        val successfulRoll = hiddenAbilityRoll < goodMarbles

        info(
            "${maxPlayer.name.string} wins with $points points, has a $goodMarbles out of ${totalMarbles}, rolls a $hiddenAbilityRoll, ${if (successfulRoll) "wins" else "loses"}",
            config.debug
        )

        if (!successfulRoll) {
            info("conclusion: did not give $species its hidden ability", config.debug)
            return
        }

        val tForm = pokemon.form
        val potentialAbilityMapping = tForm.abilities.mapping[Priority.LOW] ?: return
        val potentialAbility = potentialAbilityMapping[0]
        val newAbilityBuilder = potentialAbility.template.builder
        val newAbility = newAbilityBuilder.invoke(potentialAbility.template, false)
        newAbility.index = 0
        newAbility.priority = Priority.LOW
        pokemon.ability = newAbility
        info("conclusion: gave $species its hidden ability", config.debug)
    }
}