package us.timinc.mc.cobblemon.chaining

import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraftforge.event.server.ServerStartedEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import us.timinc.mc.cobblemon.chaining.config.HiddenBoostConfig
import us.timinc.mc.cobblemon.chaining.config.IvBoostConfig
import us.timinc.mc.cobblemon.chaining.config.ShinyBoostConfig
import us.timinc.mc.cobblemon.chaining.config.SynchronizedNaturesConfig
import us.timinc.mc.cobblemon.chaining.modules.HiddenBooster
import us.timinc.mc.cobblemon.chaining.modules.IvBooster
import us.timinc.mc.cobblemon.chaining.modules.ShinyBooster
import us.timinc.mc.cobblemon.chaining.modules.SynchronizedNatures

@Mod(Chaining.MOD_ID)
object Chaining {
    const val MOD_ID = "unchained"

    private lateinit var shinyBoostConfig: ShinyBoostConfig
    private lateinit var ivBoostConfig: IvBoostConfig
    private lateinit var hiddenBoostConfig: HiddenBoostConfig
    private lateinit var synchronizedNaturesConfig: SynchronizedNaturesConfig

    private lateinit var ivBooster: IvBooster
    private lateinit var shinyBooster: ShinyBooster
    private lateinit var hiddenBooster: HiddenBooster
    private lateinit var synchronizedNatures: SynchronizedNatures

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    object Registration {
        @SubscribeEvent
        fun onInit(e: ServerStartedEvent) {
            shinyBoostConfig = ShinyBoostConfig.Builder.load()
            ivBoostConfig = IvBoostConfig.Builder.load()
            hiddenBoostConfig = HiddenBoostConfig.Builder.load()
            synchronizedNaturesConfig = SynchronizedNaturesConfig.Builder.load()

            shinyBooster = ShinyBooster(shinyBoostConfig)
            ivBooster = IvBooster(ivBoostConfig)
            hiddenBooster = HiddenBooster(hiddenBoostConfig)
            synchronizedNatures = SynchronizedNatures(synchronizedNaturesConfig)
        }
    }

    fun possiblyMakeShiny(entity: PokemonEntity, ctx: SpawningContext) {
        shinyBooster.act(entity, ctx)
    }

    fun possiblyModifyIvs(entity: PokemonEntity, ctx: SpawningContext) {
        ivBooster.act(entity, ctx)
    }

    fun possiblyAddHiddenAbility(entity: PokemonEntity, ctx: SpawningContext) {
        hiddenBooster.act(entity, ctx)
    }

    fun possiblySynchronizeNatures(entity: PokemonEntity, ctx: SpawningContext) {
        synchronizedNatures.act(entity, ctx)
    }
}