package com.github.zululi.minecrafthunters

import com.github.zululi.minecrafthunters.Minecrafthunters.Main.admin
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.arplayer
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.arrive
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.blocks
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.board
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.cooltime
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.deathed
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.endportal
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.gamestart
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.hour
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.hozon
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.hunter
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.hunterplayer
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.killsranking
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.min
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.portal
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.portalhozon
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.portalplace
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.sec
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.serverre
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.startsec
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.survivor
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.survivorclear
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.survivorplayer
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.trackplayer
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.yousaix
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.yousaiz
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.block.BlockState
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Arrow
import org.bukkit.entity.EnderDragon
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.*
import org.bukkit.event.player.*
import org.bukkit.event.world.PortalCreateEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.CompassMeta
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.DisplaySlot
import java.util.*
import kotlin.math.roundToInt
import kotlin.math.sqrt


class Minecrafthunters : JavaPlugin() , Listener, CommandExecutor {
    object Main {
        var gamestart = 0
        var startsec = -999
        var survivorclear = false
        var min = 0
        var hour = 0
        var sec = 0
        var yousaix = 0
        var yousaiz = 0
        var board = Bukkit.getScoreboardManager()?.mainScoreboard
        var hunter = board?.registerNewTeam("hunter")
        var survivor = board?.registerNewTeam("survivor")
        var admin = board?.registerNewTeam("admin")
        var arrive = board?.registerNewTeam("arrive")
        var deathed = board?.registerNewTeam("deathed")
        var serverre = 300
        val hozon = mutableListOf<BlockState>()
        val portalhozon = arrayListOf<String>()
        var portal = 0
        var portalplace = ""
        var endportal = false
        var hunterplayer = 0
        var survivorplayer = 99
        var arplayer = 99
        var killsranking = mutableMapOf<UUID,Int>()
        var trackplayer = mutableMapOf<UUID,String>()
        var cooltime = arrayListOf<String>()
        var blocks = mutableMapOf<UUID,Int>()


    }
    override fun onEnable() {
        // Plugin startup logic

        Bukkit.getPluginManager().registerEvents(this, this)
        sec()
        tick()
        hunter?.setAllowFriendlyFire(false)
        survivor?.setAllowFriendlyFire(false)
        hunter?.color = ChatColor.RED
        survivor?.color = ChatColor.GREEN
        admin?.color = ChatColor.GOLD
        arrive?.color = ChatColor.LIGHT_PURPLE
        deathed?.color = ChatColor.GRAY
        admin?.prefix = "${ChatColor.GOLD}[A] "
        hunter?.prefix = "${ChatColor.RED}[H] "
        survivor?.prefix = "${ChatColor.GREEN}[S] "
        arrive?.prefix = "${ChatColor.LIGHT_PURPLE}[❤] "
        deathed?.prefix = "${ChatColor.GRAY}[♡] "
        Bukkit.getWorld("world")?.setGameRule(GameRule.SPAWN_RADIUS, 0)
        Bukkit.getWorld("world")?.setSpawnLocation(0, 200, 0)
        Bukkit.getWorld("world")?.worldBorder?.size = 20.0
        val hpscore = Bukkit.getScoreboardManager()?.mainScoreboard?.registerNewObjective("hp","health" , "hp")
        val kills = Bukkit.getScoreboardManager()?.mainScoreboard?.registerNewObjective("kills","playerKillCount" , "kills")
        kills?.displaySlot = DisplaySlot.PLAYER_LIST

        hpscore?.displayName = "${ChatColor.RED}❤"

        hpscore?.displaySlot = DisplaySlot.BELOW_NAME
        object : BukkitRunnable() {
            override fun run() {
                for (x1 in -10..10) {
                    val z1 = 10
                    val x2 = 10
                    for (z2 in -10..10) {
                        val x3 = -10
                        val z4 = -10
                        val block = Bukkit.getWorld("world")?.getBlockAt(x1, 150, z1)
                        val block2 = Bukkit.getWorld("world")?.getBlockAt(x2, 150, z2)
                        val block3 = Bukkit.getWorld("world")?.getBlockAt(x3, 150, z2)
                        val block4 = Bukkit.getWorld("world")?.getBlockAt(x1, 150, z4)
                        val setblock = Material.getMaterial("GLASS")!!.createBlockData()
                        block?.blockData = setblock
                        block2?.blockData = setblock
                        block3?.blockData = setblock
                        block4?.blockData = setblock
                    }
                }
                cancel()
            }
        }.runTaskTimer(this, 0, 0)
    }
    override fun onDisable() {
        // Plugin shutdown logic
        hunter?.unregister()
        survivor?.unregister()
        admin?.unregister()
        arrive?.unregister()
        deathed?.unregister()
        Bukkit.getScoreboardManager()?.mainScoreboard?.getObjective("hp")?.unregister()
        Bukkit.getScoreboardManager()?.mainScoreboard?.getObjective("kills")?.unregister()
        Bukkit.getScoreboardManager()?.mainScoreboard?.getObjective("game")?.unregister()

        for (player in Bukkit.getBannedPlayers()){
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pardon ${player.name}")
        }
    }
    @EventHandler
    fun onjoinplayer(e: PlayerJoinEvent) {
        val player = e.player
        player.removePotionEffect(PotionEffectType.GLOWING)
        player.addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION, 99999999, 1, true))
        player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 99, false))

        e.joinMessage = "${ChatColor.YELLOW}${player.name} joined."
        if (gamestart == 0 && player.scoreboard.getEntryTeam(player.name) == null) {
            survivor?.addEntry(player.name)
            val compass = ItemStack(Material.COMPASS)
            val metadatacompass = compass.itemMeta
            metadatacompass?.isUnbreakable = true
            val l0: MutableList<String> = ArrayList()
            l0.add("${ChatColor.GOLD}SoulBound")
            metadatacompass?.addEnchant(Enchantment.VANISHING_CURSE, 1, true)
            metadatacompass?.lore = (l0)
            compass.itemMeta = metadatacompass
            player.inventory.setItem(8, compass)
        }
        if (gamestart in 2..3) {
            deathed?.addEntry(player.name)
            player.gameMode = GameMode.SPECTATOR
        }
    }
    @EventHandler
    fun onleaveplayer(e:PlayerQuitEvent){
        val player = e.player
        if (player.scoreboard.getEntryTeam(player.name)?.name == "arrive") {
            deathed?.addEntry(player.name)
            player.gameMode = GameMode.SPECTATOR
            e.quitMessage = "${ChatColor.DARK_RED}${player.name}は死亡しました。"
            player.world.strikeLightningEffect(player.location)

        }
    }
    @EventHandler
    fun killeddragon(e: EntityDeathEvent) {
        val entity = e.entity
        if (entity is EnderDragon) {
            survivorclear = true
        }

    }
    @EventHandler
    fun item(e: PlayerDropItemEvent) {
        val player = e.player
        val item = e.itemDrop
        if (item.name == "Compass") {

            item.remove()
            player.playSound(player.location, Sound.ENTITY_ITEM_BREAK, 0.5f, 1f)
            player.sendMessage("${ChatColor.GRAY}|コンパスを捨てました。|\n|再度入手するには/compassを実行してください。|")
        } else if (item.name == "Lodestone Compass") {
            item.remove()
            player.playSound(player.location, Sound.ENTITY_ITEM_BREAK, 0.5f, 1f)
            player.sendMessage("${ChatColor.GRAY}|コンパスを捨てました。|\n|再度入手するには/compassを実行してください。|")
        }

        when (item.itemStack.itemMeta?.lore.toString()) {
            "[${ChatColor.GOLD}SoulBound]" -> {
                when (item.name) {
                    "Wooden Sword" -> {
                        item.remove()
                        player.playSound(player.location, Sound.ENTITY_ITEM_BREAK, 0.5f, 1f)
                    }

                    "Wooden Shovel" -> {
                        item.remove()
                        player.playSound(player.location, Sound.ENTITY_ITEM_BREAK, 0.5f, 1f)
                    }

                    "Wooden Pickaxe" -> {
                        item.remove()
                        player.playSound(player.location, Sound.ENTITY_ITEM_BREAK, 0.5f, 1f)

                    }

                    "Wooden Axe" -> {
                        item.remove()
                        player.playSound(player.location, Sound.ENTITY_ITEM_BREAK, 0.5f, 1f)
                    }

                    "Bread" -> {
                        item.remove()
                        player.playSound(player.location, Sound.ENTITY_ITEM_BREAK, 0.5f, 1f)
                    }
                }
            }
        }
    }
    @EventHandler
    fun damage(e: EntityDamageEvent) {
        when (gamestart) {
            0 -> {
                e.isCancelled = true
            }

            1 -> {
                e.isCancelled = false
                val entity = e.entity
                val damage = e.damage
                if (entity is Player) {
                    entity.giveExp((damage * -15).toInt())
                }
            }
        }

    }
    @EventHandler
    fun breakblock(e: BlockBreakEvent) {
        when (gamestart) {
            0 -> {
                e.isCancelled = true
            }

            1 -> {
                e.isCancelled = false
                val block = e.block
                val item = e.block.drops
                val player = e.player
                val blockplace = block.location
                if (player.scoreboard.getEntryTeam(player.name)?.name == "hunter" && hour == 0 && min in 0..2) {
                    e.isCancelled = true
                }

                for (i in item) {
                    when (e.block.type.toString()) {
                        "IRON_ORE" -> {
                            val range = (1..5)
                            val random = range.random()
                            val gi = ItemStack(Material.IRON_INGOT, random)
                            player.world.dropItemNaturally(blockplace, ItemStack(gi))
                            player.giveExp(15)
                        }

                        "GOLD_ORE" -> {

                            val range = (1..7)
                            val random = range.random()
                            if (random in 0..5) {
                                val gi = ItemStack(Material.GOLD_INGOT, random)
                                player.world.dropItemNaturally(blockplace, ItemStack(gi))
                            } else if (random in 6..7) {
                                val gi = ItemStack(Material.GOLDEN_APPLE, 8 - random)
                                player.world.dropItemNaturally(blockplace, ItemStack(gi))
                            }
                        }

                        "NETHER_GOLD_ORE" -> {

                            val range = (1..7)
                            val random = range.random()
                            if (random in 0..5) {
                                val gi = ItemStack(Material.GOLD_INGOT, random)
                                player.world.dropItemNaturally(blockplace, ItemStack(gi))
                            } else if (random in 6..7) {
                                val gi = ItemStack(Material.GOLDEN_APPLE, 8 - random)
                                player.world.dropItemNaturally(blockplace, ItemStack(gi))
                            }
                        }

                        "OAK_LOG" -> {
                            val range = (1..5)
                            val random = range.random()
                            val gi = ItemStack(Material.OAK_LOG, random)
                            player.world.dropItemNaturally(blockplace, ItemStack(gi))
                            player.giveExp(10)
                        }

                        "SPRUCE_LOG" -> {
                            val range = (1..5)
                            val random = range.random()
                            val gi = ItemStack(Material.SPRUCE_LOG, random)
                            player.world.dropItemNaturally(blockplace, ItemStack(gi))
                            player.giveExp(10)
                        }

                        "BIRCH_LOG" -> {
                            val range = (1..5)
                            val random = range.random()
                            val gi = ItemStack(Material.BIRCH_LOG, random)
                            player.world.dropItemNaturally(blockplace, ItemStack(gi))
                            player.giveExp(10)
                        }

                        "JUNGLE_LOG" -> {
                            val range = (1..5)
                            val random = range.random()
                            val gi = ItemStack(Material.JUNGLE_LOG, random)
                            player.world.dropItemNaturally(blockplace, ItemStack(gi))
                            player.giveExp(10)
                        }

                        "ACACIA_LOG" -> {
                            val range = (1..5)
                            val random = range.random()
                            val gi = ItemStack(Material.ACACIA_LOG, random)
                            player.world.dropItemNaturally(blockplace, ItemStack(gi))
                            player.giveExp(10)
                        }

                        "DARK_OAK_LOG" -> {
                            val range = (1..5)
                            val random = range.random()
                            val gi = ItemStack(Material.DARK_OAK_LOG, random)
                            player.world.dropItemNaturally(blockplace, ItemStack(gi))
                            player.giveExp(10)
                        }

                        "CRIMSON_STEM" -> {
                            val range = (1..5)
                            val random = range.random()
                            val gi = ItemStack(Material.CRIMSON_STEM, random)
                            player.world.dropItemNaturally(blockplace, ItemStack(gi))
                            player.giveExp(10)
                        }

                        "WARPED_STEM" -> {
                            val range = (1..5)
                            val random = range.random()
                            val gi = ItemStack(Material.WARPED_STEM, random)
                            player.world.dropItemNaturally(blockplace, ItemStack(gi))
                            player.giveExp(10)
                        }

                        "LAPIS_ORE" -> {
                            val range = (10..30)
                            val random = range.random()
                            val gi = ItemStack(Material.LAPIS_LAZULI, random)
                            player.world.dropItemNaturally(blockplace, ItemStack(gi))
                            player.giveExp(20)
                        }

                        "COAL_ORE" -> {
                            val range = (1..10)
                            val random = range.random()
                            player.world.dropItemNaturally(blockplace, ItemStack(Material.COAL, random))
                            if (random in 1..2) {
                                player.world.dropItemNaturally(blockplace, ItemStack(Material.COOKED_BEEF, random))
                            }
                            player.giveExp(5)
                        }
                        "DIAMOND_ORE"->{
                            val range = (1..3)
                            val random = range.random()
                            val gi = ItemStack(Material.DIAMOND, random)
                            player.world.dropItemNaturally(blockplace, ItemStack(gi))
                            player.giveExp(20)
                        }

                        else -> {
                            player.world.dropItemNaturally(blockplace, ItemStack(i))
                            player.giveExp(1)
                        }
                    }
                    block.type = Material.AIR
                }


            }
        }
    }
    @EventHandler
    fun placeblock(e: BlockPlaceEvent) {
        when (gamestart) {
            0 -> {
                e.isCancelled = true
            }

            1 -> {
                e.isCancelled = false
            }
        }
    }
    @EventHandler
    fun itempickup(e: EntityPickupItemEvent) {
        if (e.entity is Player) {
            when (gamestart) {
                0 -> {
                    e.isCancelled = true
                }

                1 -> {
                    e.isCancelled = false
                }
            }
        }

    }
    @EventHandler
    fun deathevent(e: PlayerDeathEvent) {
        val player = e.entity
        val killer = e.entity.killer?.uniqueId
        val death = e.deathMessage
        if (killer != null) {
            killsranking[killer] = killsranking[killer]?.plus(1)?:1
        }
        if (player.world.name == "world_the_end" && player.scoreboard.getEntryTeam(player.name)?.name == "survivor" && death?.contains(
                "place"
            ) == true
        ) {
            e.deathMessage = "${ChatColor.GREEN}${player.name}はエンドでの落下死のためリスポーンしました。"
            player.sendMessage("${ChatColor.GREEN}エンドでの落下死のためリスポーンしました。")
        } else if (player.scoreboard.getEntryTeam(player.name)?.name == "survivor") {
            hunter?.addEntry(player.name)
            e.keepInventory = true
            e.deathMessage = "${ChatColor.RED}${player.name}はハンターになりました。"
            player.sendMessage("${ChatColor.RED}死亡したため、ハンターになりました。")
            player.world.strikeLightningEffect(player.location)
        } else if (player.scoreboard.getEntryTeam(player.name)?.name == "hunter") {
            player.sendMessage("${ChatColor.GREEN}リスポーンしました。")
            e.keepInventory = true
            e.drops.clear()

        }
        object : BukkitRunnable() {
            override fun run() {
                e.entity.spigot().respawn()
                val compass = ItemStack(Material.COMPASS)
                val metadatacompass = compass.itemMeta
                metadatacompass?.isUnbreakable = true
                val l0: MutableList<String> = ArrayList()
                l0.add("${ChatColor.GOLD}SoulBound")
                metadatacompass?.addEnchant(Enchantment.VANISHING_CURSE, 1, true)
                metadatacompass?.lore = (l0)
                compass.itemMeta = metadatacompass
                player.inventory.setItem(8, compass)
                player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 6, false))
                player.addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION, 9999999, 1, true))
            }
        }.runTaskLater(this, 1L)
        if (player.scoreboard.getEntryTeam(player.name)?.name == "arrive") {
            e.deathMessage = "${ChatColor.DARK_RED}${player.name}は死亡しました。"
            player.world.strikeLightningEffect(player.location)
            object : BukkitRunnable() {
                override fun run() {
                    deathed?.addEntry(player.name)
                   player.gameMode = GameMode.SPECTATOR
                }
            }.runTaskLater(this, 10L)
        }
    }
    @EventHandler
    fun chatevent(e: AsyncPlayerChatEvent) {
        val player = e.player
        when (player.scoreboard.getEntryTeam(player.name)?.name) {
            "hunter" -> {
                for (hunter in board?.getTeam("hunter")!!.entries) {
                    Bukkit.getPlayer(hunter)
                        ?.sendMessage("${ChatColor.RED}[Teamchat] ${player.name}${ChatColor.YELLOW} : ${ChatColor.RESET}${e.message}")
                    e.isCancelled = true
                }
                for (admin in board?.getTeam("admin")!!.entries) {
                    Bukkit.getPlayer(admin)
                        ?.sendMessage("${ChatColor.RED}[Teamchat] ${player.name}${ChatColor.YELLOW} : ${ChatColor.RESET}${e.message}")
                }
            }

            "survivor" -> {
                for (hunter in board?.getTeam("survivor")!!.entries) {
                    Bukkit.getPlayer(hunter)
                        ?.sendMessage("${ChatColor.GREEN}[Teamchat] ${player.name}${ChatColor.YELLOW} : ${ChatColor.RESET}${e.message}")
                    e.isCancelled = true
                }
                for (admin in board?.getTeam("admin")!!.entries) {
                    Bukkit.getPlayer(admin)
                        ?.sendMessage("${ChatColor.GREEN}[Teamchat] ${player.name}${ChatColor.YELLOW} : ${ChatColor.RESET}${e.message}")
                }
            }

            "admin" -> {
                for (hunter in Bukkit.getOnlinePlayers()) {
                    Bukkit.getPlayer(hunter.name)
                        ?.sendMessage("${ChatColor.GOLD}${player.name}${ChatColor.YELLOW} : ${ChatColor.RESET}${e.message}")
                    e.isCancelled = true
                }
            }

            else -> {
                for (hunter in Bukkit.getOnlinePlayers()) {
                    Bukkit.getPlayer(hunter.name)
                        ?.sendMessage("${player.name}${ChatColor.YELLOW} : ${ChatColor.RESET}${e.message}")
                    e.isCancelled = true
                }
            }
        }
    }
    @EventHandler
    fun usingitem(e: PlayerInteractEvent) {
        val playerlist = ArrayList<String>()
        val player = e.player
        val item = e.item?.type
        val click = e.action
        if (item == Material.COMPASS && click == Action.RIGHT_CLICK_AIR) {
            if (player.scoreboard.getEntryTeam(player.name)?.name == "survivor") {
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1f, 1.0f)
                if (e.player.world.name == "world") {
                    val location =
                        player.world.locateNearestStructure(player.location, StructureType.STRONGHOLD, 32, false)
                    if (location == null) {
                        player.sendMessage("${ChatColor.RED}要塞が見つかりませんでした(´・ω・`)")
                    } else {
                        val playerx = player.location.blockX
                        val playerz = player.location.blockZ
                        val diffarencex = yousaix - playerx
                        val diffarencez = yousaiz - playerz
                        val distance = diffarencex * diffarencex + diffarencez * diffarencez
                        val result = sqrt(distance.toDouble())


                        if (e.player.inventory.getItem(8)!!
                                .isSimilar(ItemStack(Material.COMPASS)) || e.player.inventory.getItem(8) != ItemStack(
                                Material.AIR
                            )
                        ) {
                            val compass = e.player.inventory.getItem(8)?.itemMeta as CompassMeta
                            compass.lodestone = location
                            compass.isLodestoneTracked = false
                            e.player.inventory.getItem(8)?.itemMeta = compass
                        }
                        player.sendMessage("${ChatColor.YELLOW}要塞: " + yousaix + " ~ " + yousaiz + "  (残り${result.toInt()}ブロック)")
                    }
                    val location2 =
                        player.world.locateNearestStructure(player.location, StructureType.VILLAGE, 32, false)
                    if (location2 == null) {
                        player.sendMessage("${ChatColor.RED}村が見つかりませんでした(´・ω・`)")
                    }
                } else if (e.player.world.name == "world_nether") {
                    player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1f, 1.0f)
                    val location =
                        player.world.locateNearestStructure(player.location, StructureType.NETHER_FORTRESS, 32, false)
                    if (location == null) {
                        player.sendMessage("${ChatColor.RED}ネザー要塞が見つかりませんでした(´・ω・`)")
                    } else {
                        val x = location.blockX
                        val z = location.blockZ
                        val playerx = player.location.blockX
                        val playerz = player.location.blockZ
                        val diffarencex = x - playerx
                        val diffarencez = z - playerz
                        val distance = diffarencex * diffarencex + diffarencez * diffarencez
                        val result = sqrt(distance.toDouble())
                        if (e.player.inventory.getItem(8)!!
                                .isSimilar(ItemStack(Material.COMPASS)) || e.player.inventory.getItem(8) != ItemStack(
                                Material.AIR
                            )
                        ) {
                            val compass = e.player.inventory.getItem(8)?.itemMeta as CompassMeta
                            compass.lodestone = location
                            compass.isLodestoneTracked = false
                            e.player.inventory.getItem(8)?.itemMeta = compass
                            player.sendMessage("${ChatColor.YELLOW}ネザー要塞: " + location.blockX + " ~ " + location.blockZ + "  (残り${result.toInt()}ブロック)")
                        }
                    }
                }
            }


//            for (all in Bukkit.getOnlinePlayers()) {
//                if (player.scoreboard.getEntryTeam(player.name)?.name == "hunter") {
//                    for (survivor2 in board?.getTeam("survivor")!!.entries) {
//                        playerlist.add(survivor2.toString())
//
//                    }
//                }
//            }

//            val x = Bukkit.getPlayer(chengeplayer)?.location?.blockX
//            val z = Bukkit.getPlayer(chengeplayer)?.location?.blockZ
//            val playerx = player.location.blockX
//            val playerz = player.location.blockZ
//            val diffarencex  = x?.minus(playerx)
//            val diffarencez  = z?.minus(playerz)
//            val distance = (diffarencex?.times(diffarencex) ?: 1) + (diffarencez?.times(diffarencez) ?: 1)
//            val result = sqrt(distance.toDouble())


            if (player.scoreboard.getEntryTeam(player.name)?.name == "hunter") {
                when (player.world.name) {
                    "world" -> {
                        for (all in Bukkit.getOnlinePlayers()) {
                            if (all.world.name == "world") {
                                if (all.scoreboard.getEntryTeam(all.name)?.name == "survivor") {

                                    playerlist.add(all.name)
                                    playerlist.remove(trackplayer[player.uniqueId])
                                }
                            }
                        }
                    }

                    "world_nether" -> {
                        for (all in Bukkit.getOnlinePlayers()) {
                            if (all.world.name == "world_nether") {
                                if (all.scoreboard.getEntryTeam(all.name)?.name == "survivor") {

                                    playerlist.add(all.name)
                                    playerlist.remove(trackplayer[player.uniqueId])
                                }
                            }
                        }
                    }

                    "world_the_end" -> {
                        for (all in Bukkit.getOnlinePlayers()) {
                            if (all.world.name == "world_the_end") {
                                if (all.scoreboard.getEntryTeam(all.name)?.name == "survivor") {
                                    playerlist.add(all.name)
                                    playerlist.remove(trackplayer[player.uniqueId])
                                }
                            }
                        }
                    }
                }
            }

            playerlist.shuffle()

            if (playerlist.isEmpty()) {
                if (player.scoreboard.getEntryTeam(player.name)?.name == "hunter") {
                    player.sendMessage("${ChatColor.RED}${player.world.name}にはプレイヤーがいないようです。")
                }
            } else {
                val chengeplayer = playerlist[0]

                when (Bukkit.getPlayer(chengeplayer)?.world?.name) {

                    "world" -> {
                        val plworld2 = "${ChatColor.GREEN}overworld"
                        if (e.player.inventory.getItem(8)!!
                                .isSimilar(ItemStack(Material.COMPASS)) || e.player.inventory.getItem(8) != ItemStack(
                                Material.AIR
                            )
                        ) {

                            val x = Bukkit.getPlayer(chengeplayer)?.location?.blockX
                            val z = Bukkit.getPlayer(chengeplayer)?.location?.blockZ

                            val playerx = player.location.blockX

                            val playerz = player.location.blockZ
                            val diffarencex = x?.minus(playerx)
                            val diffarencez = z?.minus(playerz)
                            val distance = diffarencez?.times(diffarencez)
                                ?.let { (diffarencex?.times(diffarencex))!!.plus(it) }
                            val result = sqrt(distance!!.toDouble())
                            val result2 = result.roundToInt()
                            trackplayer[e.player.uniqueId] = Bukkit.getPlayer(chengeplayer)!!.name
                            player.sendMessage("${ChatColor.YELLOW}${chengeplayer}の現在位置にターゲットしました。\n${ChatColor.GRAY}(残り$result2 マス${ChatColor.GRAY})")

                        }
                    }

                    "world_nether" -> {
                        val plworld2 = "${ChatColor.RED}nether"
                        if (e.player.inventory.getItem(8)!!
                                .isSimilar(ItemStack(Material.COMPASS)) || e.player.inventory.getItem(8) != ItemStack(
                                Material.AIR
                            )
                        ) {
                            val compass = e.player.inventory.getItem(8)?.itemMeta as CompassMeta
                            compass.lodestone = Bukkit.getPlayer(chengeplayer)?.location
                            compass.isLodestoneTracked = false
                            e.player.inventory.getItem(8)?.itemMeta = compass
                            val x = Bukkit.getPlayer(chengeplayer)?.location?.blockX
                            val z = Bukkit.getPlayer(chengeplayer)?.location?.blockZ

                            val playerx = player.location.blockX

                            val playerz = player.location.blockZ
                            val diffarencex = x?.minus(playerx)
                            val diffarencez = z?.minus(playerz)
                            val distance = diffarencez?.times(diffarencez)
                                ?.let { (diffarencex?.times(diffarencex))!!.plus(it) }
                            val result = sqrt(distance!!.toDouble())
                            val result2 = result.roundToInt()
                            trackplayer[e.player.uniqueId] = Bukkit.getPlayer(chengeplayer)!!.name
                            player.sendMessage("${ChatColor.YELLOW}${chengeplayer}の現在位置にターゲットしました。\n${ChatColor.GRAY}(残り$result2 マス${ChatColor.GRAY})")
                        }
                    }

                    "world_the_end" -> {
                        val plworld2 = "${ChatColor.DARK_PURPLE}end"
                        if (e.player.inventory.getItem(8)!!
                                .isSimilar(ItemStack(Material.COMPASS)) || e.player.inventory.getItem(8) != ItemStack(
                                Material.AIR
                            )
                        ) {
                            val compass = e.player.inventory.getItem(8)?.itemMeta as CompassMeta
                            compass.lodestone = Bukkit.getPlayer(chengeplayer)?.location
                            compass.isLodestoneTracked = false
                            e.player.inventory.getItem(8)?.itemMeta = compass
                            val x = Bukkit.getPlayer(chengeplayer)?.location?.blockX
                            val z = Bukkit.getPlayer(chengeplayer)?.location?.blockZ

                            val playerx = player.location.blockX

                            val playerz = player.location.blockZ
                            val diffarencex = x?.minus(playerx)
                            val diffarencez = z?.minus(playerz)
                            val distance = diffarencez?.times(diffarencez)
                                ?.let { (diffarencex?.times(diffarencex))!!.plus(it) }
                            val result = sqrt(distance!!.toDouble())
                            val result2 = result.roundToInt()
                            trackplayer[e.player.uniqueId] = Bukkit.getPlayer(chengeplayer)!!.name
                            player.sendMessage("${ChatColor.YELLOW}${chengeplayer}の現在位置にターゲットしました。\n${ChatColor.GRAY}(残り$result2 マス${ChatColor.GRAY})")

                        }
                    }

                }


                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1f, 1.0f)
            }
        }

        when (item?.toString()) {
            "WOODEN_PICKAXE" -> {
                val im = player.inventory.itemInMainHand.itemMeta
                im!!.addEnchant(Enchantment.DIG_SPEED, 2, true)

                im.isUnbreakable = true
                player.inventory.itemInMainHand.itemMeta = im
            }

            "STONE_PICKAXE" -> {
                val im = player.inventory.itemInMainHand.itemMeta
                im!!.addEnchant(Enchantment.DIG_SPEED, 2, true)
                im.isUnbreakable = true
                player.inventory.itemInMainHand.itemMeta = im
            }

            "IRON_PICKAXE" -> {
                val im = player.inventory.itemInMainHand.itemMeta
                im!!.addEnchant(Enchantment.DIG_SPEED, 2, true)
                im.isUnbreakable = true
                player.inventory.itemInMainHand.itemMeta = im
            }

            "GOLDEN_PICKAXE" -> {
                val im = player.inventory.itemInMainHand.itemMeta
                im!!.addEnchant(Enchantment.DIG_SPEED, 2, true)
                im.isUnbreakable = true
                player.inventory.itemInMainHand.itemMeta = im
            }

            "DIAMOND_PICKAXE" -> {
                val im = player.inventory.itemInMainHand.itemMeta
                im!!.addEnchant(Enchantment.DIG_SPEED, 2, true)
                im.isUnbreakable = true
                player.inventory.itemInMainHand.itemMeta = im
            }

            "NETHERITE_PICKAXE" -> {
                val im = player.inventory.itemInMainHand.itemMeta
                im!!.addEnchant(Enchantment.DIG_SPEED, 2, true)
                im.isUnbreakable = true
                player.inventory.itemInMainHand.itemMeta = im
            }

            "WOODEN_AXE" -> {
                val im = player.inventory.itemInMainHand.itemMeta
                im!!.addEnchant(Enchantment.DIG_SPEED, 2, true)
                im.isUnbreakable = true
                player.inventory.itemInMainHand.itemMeta = im
            }

            "STONE_AXE" -> {
                val im = player.inventory.itemInMainHand.itemMeta
                im!!.addEnchant(Enchantment.DIG_SPEED, 2, true)
                im.isUnbreakable = true
                player.inventory.itemInMainHand.itemMeta = im
            }

            "IRON_AXE" -> {
                val im = player.inventory.itemInMainHand.itemMeta
                im!!.addEnchant(Enchantment.DIG_SPEED, 2, true)
                im.isUnbreakable = true
                player.inventory.itemInMainHand.itemMeta = im
            }

            "GOLDEN_AXE" -> {
                val im = player.inventory.itemInMainHand.itemMeta
                im!!.addEnchant(Enchantment.DIG_SPEED, 2, true)
                im.isUnbreakable = true
                player.inventory.itemInMainHand.itemMeta = im
            }

            "DIAMOND_AXE" -> {
                val im = player.inventory.itemInMainHand.itemMeta
                im!!.addEnchant(Enchantment.DIG_SPEED, 2, true)
                im.isUnbreakable = true
                player.inventory.itemInMainHand.itemMeta = im
            }

            "NETHERITE_AXE" -> {
                val im = player.inventory.itemInMainHand.itemMeta
                im!!.addEnchant(Enchantment.DIG_SPEED, 2, true)
                im.isUnbreakable = true
                player.inventory.itemInMainHand.itemMeta = im
            }

            "WOODEN_SHOVEL" -> {
                val im = player.inventory.itemInMainHand.itemMeta
                im!!.addEnchant(Enchantment.DIG_SPEED, 2, true)
                im.isUnbreakable = true
                player.inventory.itemInMainHand.itemMeta = im
            }

            "STONE_SHOVEL" -> {
                val im = player.inventory.itemInMainHand.itemMeta
                im!!.addEnchant(Enchantment.DIG_SPEED, 2, true)
                im.isUnbreakable = true
                player.inventory.itemInMainHand.itemMeta = im
            }

            "IRON_SHOVEL" -> {
                val im = player.inventory.itemInMainHand.itemMeta
                im!!.addEnchant(Enchantment.DIG_SPEED, 2, true)
                im.isUnbreakable = true
                player.inventory.itemInMainHand.itemMeta = im
            }

            "GOLDEN_SHOVEL" -> {
                val im = player.inventory.itemInMainHand.itemMeta
                im!!.addEnchant(Enchantment.DIG_SPEED, 2, true)
                im.isUnbreakable = true
                player.inventory.itemInMainHand.itemMeta = im
            }

            "DIAMOND_SHOVEL" -> {
                val im = player.inventory.itemInMainHand.itemMeta
                im!!.addEnchant(Enchantment.DIG_SPEED, 2, true)
                im.isUnbreakable = true
                player.inventory.itemInMainHand.itemMeta = im
            }

            "NETHERITE_SHOVEL" -> {
                val im = player.inventory.itemInMainHand.itemMeta
                im!!.addEnchant(Enchantment.DIG_SPEED, 2, true)
                im.isUnbreakable = true
                player.inventory.itemInMainHand.itemMeta = im
            }
        }
    }
    @EventHandler
    fun portalEvent(e: PortalCreateEvent) {

        val canportal = e.blocks
        val world = e.world.name

        hozon.clear()
        hozon.addAll(canportal)

        val hozon2 = hozon[0].block
        when (world) {
            "world" -> {
                Bukkit.broadcastMessage("${ChatColor.GREEN}ワールドの" + hozon2.x.toString() + "," + hozon2.y + "," + hozon2.z + "にネザーポータルが生成されました。")
                portalhozon.add("${ChatColor.GREEN}" + hozon2.x.toString() + "," + hozon2.y + "," + hozon2.z)
                portal++


            }

            "world_nether" -> {
                Bukkit.broadcastMessage("${ChatColor.RED}ネザーの" + hozon2.x.toString() + "," + hozon2.y + "," + hozon2.z + "にネザーポータルが生成されました。")
                portalhozon.add("${ChatColor.RED}" + hozon2.x.toString() + "," + hozon2.y + "," + hozon2.z)
                portal++
            }
        }
        e.isCancelled = false
    }
    @EventHandler
    fun portalEnd(e: PlayerPortalEvent) {
        if (e.cause == PlayerTeleportEvent.TeleportCause.END_PORTAL){
            portalplace = "${e.player.location.x.toInt()},${e.player.location.y.toInt()},${e.player.location.z.toInt()}"
            endportal = true
        }
    }
    @EventHandler
    fun onPlayerAttack(e: EntityDamageByEntityEvent) {
        if (e.entity is Player ||e.entityType == EntityType.ARROW&& e.damager is Player) {
            // Your code here.
            val player = e.entity
            val attacker = e.damager
            object : BukkitRunnable() {
                override fun run() {
                    val health = Bukkit.getPlayer(player.name)?.health
                    if (health != null) {
                        attacker.sendMessage("${ChatColor.GREEN}${ChatColor.UNDERLINE}${player.name}${ChatColor.RESET}${ChatColor.YELLOW}はあと${ChatColor.RED}${(health * 10.0).roundToInt() / 10.0} HP${ChatColor.YELLOW}です。")
                    }
                }
            }.runTaskLater(this, 1L)
        }
        if (e.cause == EntityDamageEvent.DamageCause.PROJECTILE) {
            if (e.damager is Arrow && e.entity is Player) {
                val arrow = e.damager as Arrow
                if (arrow.shooter !is Player) return
                (arrow.shooter as Player?)!!.playSound(
                    (arrow.shooter as Player?)!!.location,
                    Sound.BLOCK_NOTE_BLOCK_PLING,
                    1f,
                    2f
                )
                object : BukkitRunnable() {
                    override fun run() {
                        val health = (e.entity as Player).health
                        (arrow.shooter as Player?)!!.sendMessage("${ChatColor.GREEN}${ChatColor.UNDERLINE}${e.entity.name}${ChatColor.RESET}${ChatColor.YELLOW}はあと${ChatColor.RED}${(health * 10.0).roundToInt() / 10.0} HP${ChatColor.YELLOW}です。")
                    }
                }.runTaskLater(this, 1L)
            }
        }
    }
    @EventHandler
    fun move(e:PlayerMoveEvent){
        val player = e.player
        val trakingplayer = trackplayer[player.uniqueId]
        if (player.name in cooltime){
            return
        }

        val compass = e.player.inventory.getItem(8)?.itemMeta as CompassMeta
        if (trakingplayer != null&&player.scoreboard.getEntryTeam(player.name)?.name == "hunter") {
            val x = Bukkit.getPlayer(trakingplayer)?.location?.blockX
            val z = Bukkit.getPlayer(trakingplayer)?.location?.blockZ

            val playerx = player.location.blockX

            val playerz = player.location.blockZ
            val diffarencex = x?.minus(playerx)
            val diffarencez = z?.minus(playerz)
            val distance = diffarencez?.times(diffarencez)
                ?.let { (diffarencex?.times(diffarencex))!!.plus(it) }
            val result = sqrt(distance!!.toDouble())
            val result2 = result.roundToInt()
            blocks[e.player.uniqueId] = result2
            compass.lodestone = Bukkit.getPlayer(trakingplayer.toString())?.location
            compass.isLodestoneTracked = false
            e.player.inventory.getItem(8)?.itemMeta = compass
        }
        if (player.scoreboard.getEntryTeam(player.name)?.name == "survivor") {
            if (e.player.world.name == "world") {
                val location =
                    player.world.locateNearestStructure(player.location, StructureType.STRONGHOLD, 32, false)
                if (location == null) {
                    return
                } else {
                    val playerx = player.location.blockX
                    val playerz = player.location.blockZ
                    val diffarencex = yousaix - playerx
                    val diffarencez = yousaiz - playerz
                    val distance = diffarencex * diffarencex + diffarencez * diffarencez
                    val result = sqrt(distance.toDouble())
                    blocks[e.player.uniqueId] = result.toInt()


                    if (e.player.inventory.getItem(8)!!
                            .isSimilar(ItemStack(Material.COMPASS)) || e.player.inventory.getItem(8) != ItemStack(
                            Material.AIR
                        )
                    ) {

                        compass.lodestone = location
                        compass.isLodestoneTracked = false
                        e.player.inventory.getItem(8)?.itemMeta = compass
                    }
                }
            } else if (e.player.world.name == "world_nether") {
                val location =
                    player.world.locateNearestStructure(player.location, StructureType.NETHER_FORTRESS, 32, false)
                if (location == null) {
                    return
                } else {
                    val x = location.blockX
                    val z = location.blockZ
                    val playerx = player.location.blockX
                    val playerz = player.location.blockZ
                    val diffarencex = x - playerx
                    val diffarencez = z - playerz
                    val distance = diffarencex * diffarencex + diffarencez * diffarencez
                    val result = sqrt(distance.toDouble())
                    blocks[e.player.uniqueId] = result.toInt()
                    if (e.player.inventory.getItem(8)!!
                            .isSimilar(ItemStack(Material.COMPASS)) || e.player.inventory.getItem(8) != ItemStack(
                            Material.AIR
                        )
                    ) {

                        compass.lodestone = location
                        compass.isLodestoneTracked = false
                        e.player.inventory.getItem(8)?.itemMeta = compass
                    }
                }
            }
        }
        cooltime.add(player.name)
        object : BukkitRunnable() {
            override fun run() {
                cooltime.remove(player.name)
            }
        }.runTaskLater(this,20)
    }


    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when(command.name){
            "test"->{
                for (player in hunter?.players!!) {
                    Bukkit.broadcastMessage(player.toString())
                    Bukkit.broadcastMessage(Bukkit.getPlayerExact(player.toString()).toString())
               
                }
            }

            "start"->{
                if (sender.isOp) {
                    if (Bukkit.getOnlinePlayers().size > 2) {
                        startsec = 10
                        min = 0
                        autowarifuri()

                    }else{
                        sender.sendMessage("${ChatColor.RED}You must have at least 3 online players to start")
                    }

                    val location =
                        Bukkit.getPlayer(sender.name)?.world?.locateNearestStructure(Bukkit.getPlayer(sender.name)!!.location, StructureType.STRONGHOLD, 90000, false)
                    if (location == null) {
                        sender.sendMessage("${ChatColor.RED}要塞が見つかりませんでした(´・ω・`)")
                    } else {
                        yousaix = location.blockX
                        yousaiz = location.blockZ
                    }
                    Bukkit.getWorld("world")?.worldBorder?.setCenter(0.0 , 0.0)

                    Bukkit.getWorld("world")?.setSpawnLocation(0, 200, 0)

                }
            }
            "qs"->{
                if (sender.isOp) {
                    if (Bukkit.getOnlinePlayers().size > 2) {
                        startsec = 1
                        autowarifuri()
                    }else{
                        sender.sendMessage("${ChatColor.RED}You must have at least 3 online players to start")
                    }
                    val location =
                        Bukkit.getPlayer(sender.name)?.world?.locateNearestStructure(Bukkit.getPlayer(sender.name)!!.location, StructureType.STRONGHOLD, 32, false)
                    if (location == null) {
                        sender.sendMessage("${ChatColor.RED}要塞が見つかりませんでした(´・ω・`)")
                    } else {
                        yousaix = location.blockX
                        yousaiz = location.blockZ
                        Bukkit.getWorld("world")?.worldBorder?.setCenter(0.0 , 0.0)
                        Bukkit.getWorld("world")?.worldBorder?.size = 20.0
                    }


                }
            }
            "compass"->{
                Bukkit.getPlayer(sender.name)?.inventory?.addItem(ItemStack(Material.COMPASS))
                sender.sendMessage("${ChatColor.GRAY}コンパスを渡しました。")
            }
            "return"->{
                if (sender.isOp){
                    gamestart = 0
                }
            }
            "g"->{
                val player = sender.name
                val message = args[0]

                when (Bukkit.getPlayer(player)?.scoreboard?.getEntryTeam(player)?.name) {
                    "hunter" -> {
                        for (all in Bukkit.getOnlinePlayers()) {
                            Bukkit.getPlayer(all.name)?.sendMessage("${ChatColor.DARK_PURPLE}[Global] ${ChatColor.RED}${player}${ChatColor.YELLOW} : ${ChatColor.RESET}${message}")
                        }
                    }
                    "survivor" -> {

                        for (all in Bukkit.getOnlinePlayers()) {
                            Bukkit.getPlayer(all.name)?.sendMessage("${ChatColor.DARK_PURPLE}[Global] ${ChatColor.GREEN}${player}${ChatColor.YELLOW} : ${ChatColor.RESET}${message}")
                        }
                    }
                    else -> {
                        for (all in Bukkit.getOnlinePlayers()) {
                            Bukkit.getPlayer(all.name)?.sendMessage("${ChatColor.DARK_PURPLE}[Global] ${ChatColor.RESET}${player}${ChatColor.YELLOW} : ${ChatColor.RESET}${message}")
                        }
                    }
                }
            }
            "settimer"->{
                if (sender.isOp) {


                    hour = args[0].toInt()
                    min = args[1].toInt()
                    sec = args[2].toInt()
                }
            }
        }
        return true
    }
    private fun sec(){
        object : BukkitRunnable() {
            override fun run() {
                when(gamestart){
                    0->{
                        prescoreboard()
                        for (player in Bukkit.getOnlinePlayers()){
                            player.foodLevel = 20
                            player.health = 40.0
                            player.saturation = 20F
                        }
                        if (startsec > 0){
                            if (startsec in 6..10) {
                                Bukkit.broadcastMessage("${ChatColor.YELLOW}試合開始まで${ChatColor.GOLD} $startsec${ChatColor.GOLD} 秒")
                            }else if (startsec in 0..5){
                                Bukkit.broadcastMessage("${ChatColor.YELLOW}試合開始まで${ChatColor.RED} $startsec${ChatColor.GOLD} 秒")
                            }
                            startsec -= 1
                            for (player in Bukkit.getOnlinePlayers()){
                                player.playSound(player.location,Sound.UI_BUTTON_CLICK,1.0F,1.0F)
                            }
                        }else if (startsec==0){
                            gamestart = 1
                            Bukkit.broadcastMessage("${ChatColor.GOLD}試合開始!")
                            for (player in Bukkit.getOnlinePlayers()) {
                                val all = Bukkit.getPlayer(player.name)
                                killsranking = mutableMapOf(all!!.uniqueId to 0)
                                val woodensword = ItemStack(Material.STONE_SWORD)
                                val metadatasword = woodensword.itemMeta
                                metadatasword?.isUnbreakable = true
                                val l0: MutableList<String> = ArrayList()
                                l0.add("${ChatColor.GOLD}SoulBound")
                                metadatasword?.addEnchant(Enchantment.VANISHING_CURSE, 1, true)
                                metadatasword?.lore = (l0)
                                woodensword.itemMeta = metadatasword
                                all.inventory.setItem(0, woodensword)

                                val woodenpickaxe = ItemStack(Material.STONE_PICKAXE)
                                val metadatapickaxe = woodenpickaxe.itemMeta
                                metadatapickaxe?.isUnbreakable = true
                                metadatapickaxe?.addEnchant(Enchantment.VANISHING_CURSE, 1, true)
                                val l1: MutableList<String> = ArrayList()
                                l1.add("${ChatColor.GOLD}SoulBound")
                                metadatapickaxe?.lore = (l1)
                                woodenpickaxe.itemMeta = metadatapickaxe
                                all.inventory.setItem(1, woodenpickaxe)

                                val woodenaxe = ItemStack(Material.STONE_AXE)
                                val metadataaxe = woodenaxe.itemMeta
                                metadataaxe?.addEnchant(Enchantment.VANISHING_CURSE, 1, true)

                                metadataaxe?.isUnbreakable = true
                                val l2: MutableList<String> = ArrayList()
                                l2.add("${ChatColor.GOLD}SoulBound")
                                metadataaxe?.lore = (l2)
                                woodenaxe.itemMeta = metadataaxe
                                all.inventory.setItem(2, woodenaxe)


                                val woodenshovel = ItemStack(Material.STONE_SHOVEL)
                                val metadatashovel = woodenshovel.itemMeta
                                metadatashovel?.addEnchant(Enchantment.VANISHING_CURSE, 1, true)
                                metadatashovel?.isUnbreakable = true
                                val l3: MutableList<String> = ArrayList()
                                l3.add("${ChatColor.GOLD}SoulBound")
                                metadatashovel?.lore = (l3)
                                woodenshovel.itemMeta = metadatashovel
                                all.inventory.setItem(3, woodenshovel)

                                all.inventory.setItem(4, ItemStack(Material.BREAD, 64))
                            }
                            for(x in -10 .. 10) {
                                for (z in -10..10) {
                                    val block = Bukkit.getWorld("world")?.getBlockAt(x, 148, z)
                                    val setblock = Material.getMaterial("GLASS")!!.createBlockData()
                                    block?.blockData = setblock
                                }
                            }
                            for (player in board?.getTeam("hunter")!!.entries){
                                Bukkit.getPlayer(player)?.teleport(Location(Bukkit.getWorld("world"),0.5,150.0,0.5))
                            }
                            for (player in Bukkit.getOnlinePlayers()){
                                player.playSound(player.location,Sound.ENTITY_WITHER_SPAWN,1.0F,1.0F)
                                Bukkit.getWorld("world")?.worldBorder?.size = 2147483647.0
                                val compass = ItemStack(Material.COMPASS)
                                val metadatacompass = compass.itemMeta
                                metadatacompass?.isUnbreakable = true
                                val l0: MutableList<String> = ArrayList()
                                l0.add("${ChatColor.GOLD}SoulBound")
                                metadatacompass?.addEnchant(Enchantment.VANISHING_CURSE, 1, true)
                                metadatacompass?.lore = (l0)
                                compass.itemMeta = metadatacompass
                                player.inventory.setItem(8, compass)
                            }

                        }
                    }
                    1->{

                        sec++
                        if (min == 59 &&sec == 60) {
                            min = 0
                            sec = 0
                            hour++
                        }else if (sec == 60){
                            sec = 0
                            min++
                        }
                        if (hour==0&&min == 3&&sec == 0){
                            for(x in -10 .. 10) {
                                for (z in -10..10) {
                                    val block = Bukkit.getWorld("world")?.getBlockAt(x, 148, z)
                                    val setblock = Material.getMaterial("AIR")!!.createBlockData()
                                    block?.blockData = setblock
                                    for (player in board?.getTeam("hunter")!!.entries){
                                        Bukkit.getPlayer(player)?.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1200, 6, true))
                                    }


                                }
                            }
                            for (player in Bukkit.getOnlinePlayers()){
                                player.playSound(player.location,Sound.ENTITY_WITHER_AMBIENT,1.0f,2.0f)
                            }
                            Bukkit.broadcastMessage("${ChatColor.GOLD}----------------------------------")
                            Bukkit.broadcastMessage("${ChatColor.GOLD}   ハンターが放出されました。")
                            Bukkit.broadcastMessage("${ChatColor.GOLD}----------------------------------")
                        }
                        if (hour == 0 && min in 0.. 9){
                            for (player in Bukkit.getOnlinePlayers()){
                                player.saturation = 20f
                                player.foodLevel = 20
                            }
                        }else if (hour == 0 && min==10&&sec==0){
                            for (player in Bukkit.getOnlinePlayers()){
                                player.sendMessage("${ChatColor.GOLD}----------------------------------")
                                player.sendMessage("${ChatColor.GOLD}  満腹度が減るようになりました。")
                                player.sendMessage("${ChatColor.GOLD}----------------------------------")
                                player.playSound(player.location,Sound.ENTITY_WITHER_AMBIENT,1.0f,2.0f)
                            }
                        }
                        if (hour == 1&& min == 0 && sec==0||hour == 1 && min == 10 && sec == 0 || hour == 1 && min == 20 && sec == 0||
                                hour== 1 && min == 25&&sec== 0||hour==1&&min==27&&sec==0||hour == 1 && min ==28&&sec == 0||hour == 1 && min==29&&sec==0){
                            for (player in Bukkit.getOnlinePlayers()){
                                player.sendMessage("${ChatColor.GOLD}ゲーム終了まで${ChatColor.RED}${30-min}${ChatColor.GOLD}分")
                                player.playSound(player.location,Sound.UI_BUTTON_CLICK,1f,1f)
                            }
                        }else if (hour==1&&min==29&&sec in 45..60){
                            for (player in Bukkit.getOnlinePlayers()){
                                player.sendMessage("${ChatColor.GOLD}ゲーム終了まで${ChatColor.RED}${60-sec}${ChatColor.GOLD}秒")
                                player.playSound(player.location,Sound.UI_BUTTON_CLICK,1f,1f)
                            }
                        }
                        gamescoreboard()
                    }
                    2->{
                        gamescoreboard()
                    }
                    3->{
                        gamescoreboard()
                        serverre--
                    }
                }
            }
        }.runTaskTimer(this, 0, 20)
    }
    private fun tick(){
        object : BukkitRunnable() {
            override fun run() {
                for (all in Bukkit.getOnlinePlayers()) {
                    if (all.scoreboard.getEntryTeam(all.name)?.name == "hunter" && trackplayer[all.uniqueId] != null){
                        if (all.world == Bukkit.getPlayer(trackplayer[all.uniqueId].toString())!!.world) {

                            val component = TextComponent()
                            component.text =
                                "${ChatColor.YELLOW}現在の座標: ${all.location.blockX} ${all.location.blockY} ${all.location.blockZ} ${ChatColor.GREEN}${trackplayer[all.uniqueId]}: 残り${blocks[all.uniqueId]}マス"
                            all.spigot().sendMessage(ChatMessageType.ACTION_BAR, component)
                        }else{
                            val component = TextComponent()
                            component.text =
                                "${ChatColor.YELLOW}現在の座標: ${all.location.blockX} ${all.location.blockY} ${all.location.blockZ} ${ChatColor.GREEN}${trackplayer[all.uniqueId]}: ${ChatColor.RED}${ChatColor.UNDERLINE}ここにはいません"
                            all.spigot().sendMessage(ChatMessageType.ACTION_BAR, component)
                        }

                    }else if (all.scoreboard.getEntryTeam(all.name)?.name == "survivor"){
                        val component = TextComponent()
                        component.text =
                            "${ChatColor.YELLOW}現在の座標: ${all.location.blockX} ${all.location.blockY} ${all.location.blockZ} ${ChatColor.GREEN}残り${blocks[all.uniqueId]}マス"
                        all.spigot().sendMessage(ChatMessageType.ACTION_BAR, component)
                    }
                }
                when(gamestart){
                    0->{
                        for (all in Bukkit.getOnlinePlayers()){
                            Bukkit.getPlayer(all.name)?.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = 40.0
                        }
                    }
                    1->{
                        for (all in Bukkit.getOnlinePlayers()){
                            Bukkit.getPlayer(all.name)?.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = 40.0
                        }

                        if (survivorplayer == 0){
                            for (player in Bukkit.getOnlinePlayers()){
                                player.playSound(player.location,Sound.ENTITY_GENERIC_EXPLODE,1f,1.0f)
                                player.playSound(player.location,Sound.ENTITY_FIREWORK_ROCKET_LAUNCH,1f,1.0f)
                                player.sendMessage("${ChatColor.GOLD}--------------------------------\n試合終了!\n${ChatColor.YELLOW}生存者がいなくなったため、ハンターの勝利です。${ChatColor.GOLD}--------------------------------\n")
                                cancel()
                                gamestart =2

                            }
                            end()
                        }else if (hour == 1 && min == 30 && sec == 0){
                            for (player in Bukkit.getOnlinePlayers()){
                                player.playSound(player.location,Sound.ENTITY_GENERIC_EXPLODE,1f,1.0f)
                                player.playSound(player.location,Sound.ENTITY_FIREWORK_ROCKET_LAUNCH,1f,1.0f)
                                player.sendMessage("${ChatColor.GOLD}--------------------------------\n試合終了!\n${ChatColor.YELLOW}制限時間になったため、ハンターの勝利です。${ChatColor.GOLD}--------------------------------\n")
                                cancel()
                                gamestart =2
                            }
                            end()
                        }else if (survivorclear){
                            for (player in Bukkit.getOnlinePlayers()){
                                player.playSound(player.location,Sound.ENTITY_GENERIC_EXPLODE,1f,1.0f)
                                player.playSound(player.location,Sound.ENTITY_FIREWORK_ROCKET_LAUNCH,1f,1.0f)
                                player.sendMessage("${ChatColor.GOLD}--------------------------------\n試合終了!\n${ChatColor.YELLOW}エンダードラゴンを倒したため、生存者の勝利です。${ChatColor.GOLD}--------------------------------\n")
                                cancel()
                                gamestart =2
                            }
                            end()
                        }
                    }
                }
            }
        }.runTaskTimer(this, 0, 0)
    }
    private fun prescoreboard(){
        Bukkit.getScoreboardManager()?.mainScoreboard?.getObjective("pre")?.unregister()
        val prescore = Bukkit.getScoreboardManager()?.mainScoreboard?.registerNewObjective("pre","Dummy" , "aaa")
        prescore?.displayName = "${ChatColor.DARK_PURPLE}${ChatColor.BOLD}${ChatColor.ITALIC}Manhunt ${ChatColor.GRAY}4.0.0"
        prescore?.displaySlot = DisplaySlot.SIDEBAR
        prescore?.getScore("${ChatColor.GOLD}サーバー人数: ${Bukkit.getOnlinePlayers().size}")?.score = 0
    }
    private fun gamescoreboard() {
        when (gamestart) {
            1-> {
                var surplmin = 0
                var hunplmin = 0
                for (a in survivor!!.entries) {
                    if (Bukkit.getPlayer(a) == null) {
                        surplmin++
                    }
                }
                for (a in hunter!!.entries) {
                    if (Bukkit.getPlayer(a) == null) {
                        hunplmin++
                    }
                }

                survivorplayer = survivor?.size?.minus(surplmin)!!
                hunterplayer = hunter?.size?.minus(hunplmin)!!


                Bukkit.getScoreboardManager()?.mainScoreboard?.getObjective("game")?.unregister()
                val prescore =
                    Bukkit.getScoreboardManager()?.mainScoreboard?.registerNewObjective("game", "Dummy", "game")

                prescore?.displayName =
                    "${ChatColor.DARK_PURPLE}${ChatColor.BOLD}${ChatColor.ITALIC}Manhunt ${ChatColor.GRAY}4.0.0"
                prescore?.displaySlot = DisplaySlot.SIDEBAR
                prescore?.getScore("${ChatColor.GREEN}残り人数: $survivorplayer")?.score = 11
                prescore?.getScore("${ChatColor.RED}ハンター: $hunterplayer")?.score = 10
                prescore?.getScore("${ChatColor.GOLD}${ChatColor.STRIKETHROUGH}                 ")?.score = 12
                prescore?.getScore("${ChatColor.RED}${ChatColor.GOLD}${ChatColor.STRIKETHROUGH}                 ")?.score =
                    9

                if (portal == 1) {
                    prescore?.getScore("${ChatColor.RED}${ChatColor.UNDERLINE}ネザーポータル:")?.score = 8
                    prescore?.getScore(portalhozon[0])?.score = 7
                }

                if (portal == 2) {
                    prescore?.getScore("${ChatColor.RED}${ChatColor.UNDERLINE}ネザーポータル:")?.score = 8
                    prescore?.getScore(portalhozon[0])?.score = 7
                    prescore?.getScore(portalhozon[1])?.score = 6
                }
                if (portal == 3) {
                    prescore?.getScore("${ChatColor.RED}${ChatColor.UNDERLINE}ネザーポータル:")?.score = 8
                    prescore?.getScore(portalhozon[0])?.score = 7
                    prescore?.getScore(portalhozon[1])?.score = 6
                    prescore?.getScore(portalhozon[2])?.score = 5
                }

                if (portal == 4) {
                    prescore?.getScore("${ChatColor.RED}${ChatColor.UNDERLINE}ネザーポータル:")?.score = 8
                    prescore?.getScore(portalhozon[0])?.score = 7
                    prescore?.getScore(portalhozon[1])?.score = 6
                    prescore?.getScore(portalhozon[2])?.score = 5
                    prescore?.getScore(portalhozon[3])?.score = 4
                }
                if (portal >= 5) {
                    prescore?.getScore("${ChatColor.RED}${ChatColor.UNDERLINE}ネザーポータル:")?.score = 8
                    prescore?.getScore(portalhozon[0])?.score = 7
                    prescore?.getScore(portalhozon[1])?.score = 6
                    prescore?.getScore(portalhozon[2])?.score = 5
                    prescore?.getScore(portalhozon[3])?.score = 4
                    prescore?.getScore(portalhozon[4])?.score = 3
                }

                if (endportal) {
                    prescore?.getScore("${ChatColor.LIGHT_PURPLE}${ChatColor.UNDERLINE}エンドポータル:")?.score = 2
                    prescore?.getScore("${ChatColor.LIGHT_PURPLE}" + portalplace)?.score = 1
                }
                if (min in 0..9 && sec in 0..9) {
                    prescore?.getScore("${ChatColor.GOLD}経過時間: $hour : 0$min : 0$sec")?.score = 15
                } else if (min in 0..9 && sec in 10..59) {
                    prescore?.getScore("${ChatColor.GOLD}経過時間: $hour : 0$min : $sec")?.score = 15
                } else if (min in 9..59 && sec in 0..9) {
                    prescore?.getScore("${ChatColor.GOLD}経過時間: $hour : $min : 0$sec")?.score = 15
                } else if (min in 10..59 && sec in 10..59) {
                    prescore?.getScore("${ChatColor.GOLD}経過時間: $hour : $min : $sec")?.score = 15
                }

                if (hour == 0 && min in 0..2 && sec == 0) {
                    prescore?.getScore("${ChatColor.GOLD}ハンター解放まで: ${3 - min} : 00")?.score = 14
                } else if (hour == 0 && min in 0..2 && sec in 1..50) {
                    prescore?.getScore("${ChatColor.GOLD}ハンター解放まで: ${2 - min} : ${60 - sec}")?.score = 14
                } else if (hour == 0 && min in 0..2 && sec in 51..59) {
                    prescore?.getScore("${ChatColor.GOLD}ハンター解放まで: ${2 - min} : 0${60 - sec}")?.score = 14
                }
                if (hour == 0 && min in 0..9 && sec == 0) {
                    prescore?.getScore("${ChatColor.GOLD}満腹度回復終了まで: ${10 - min} : 00")?.score = 13
                } else if (hour == 0 && min in 0..9 && sec in 1..50) {
                    prescore?.getScore("${ChatColor.GOLD}満腹度回復終了まで: ${9 - min} : ${60 - sec}")?.score = 13

                } else if (hour == 0 && min in 0..9 && sec in 51..59) {
                    prescore?.getScore("${ChatColor.GOLD}満腹度回復終了まで: ${9 - min} : 0${60 - sec}")?.score = 13
                }

                if (hour == 1 && sec == 0) {
                    prescore?.getScore("${ChatColor.RED}${ChatColor.UNDERLINE}ゲーム終了まで: ${29 - min} : 00")?.score = 14
                } else if (hour == 1 && sec in 1..50) {
                    prescore?.getScore("${ChatColor.RED}${ChatColor.UNDERLINE}ゲーム終了まで: ${29 - min} : ${60 - sec}")?.score =
                        14
                } else if (hour == 1 && sec in 51..59) {
                    prescore?.getScore("${ChatColor.RED}${ChatColor.UNDERLINE}ゲーム終了まで: ${29 - min} : 0${60 - sec}")?.score =
                        14
                }

            }
            2->{
                var arplmin = 0
                for (a in arrive!!.entries) {
                    if (Bukkit.getPlayer(a) == null) {
                        arplmin++
                    }
                }
                arplayer = arrive?.size?.minus(arplmin)!!
                Bukkit.getScoreboardManager()?.mainScoreboard?.getObjective("game")?.unregister()
                val prescore =
                    Bukkit.getScoreboardManager()?.mainScoreboard?.registerNewObjective("game", "Dummy", "game")

                prescore?.displayName =
                    "${ChatColor.DARK_PURPLE}${ChatColor.BOLD}${ChatColor.ITALIC}Manhunt ${ChatColor.GRAY}4.0.0"
                prescore?.displaySlot = DisplaySlot.SIDEBAR
                prescore?.getScore("${ChatColor.YELLOW}${ChatColor.UNDERLINE}残り人数: $arplayer")?.score = 0
                for (a in Bukkit.getOnlinePlayers()){
                    a.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, 999999999, 1, true))

                }
                if (arplayer == 1){
                    val player = arrive!!.entries //エントリーの全体
                    var winner = ""
                    for(b in player){
                        for(a in Bukkit.getOfflinePlayers()){ //オンラインプレイヤーの全体
                            if(!player.contains(a.name)) {
                                winner = b
                            }
                        }
                    }
                    Bukkit.broadcastMessage("${ChatColor.GOLD}${winner}が優勝しました。")
                    Bukkit.getPlayer(winner)?.gameMode = GameMode.CREATIVE
                    Bukkit.getWorld("world")?.worldBorder?.size = 99999999.9

                    for (p in Bukkit.getOnlinePlayers()){
                        p.playSound(p.location,Sound.ENTITY_GENERIC_EXPLODE,1f,1.0f)
                        p.playSound(p.location,Sound.ENTITY_FIREWORK_ROCKET_LAUNCH,1f,1.0f)
                    }
                    gamestart = 3
                }
            }
            3->{
                Bukkit.getScoreboardManager()?.mainScoreboard?.getObjective("game")?.unregister()
                val prescore =
                    Bukkit.getScoreboardManager()?.mainScoreboard?.registerNewObjective("game", "Dummy", "game")

                prescore?.displayName =
                    "${ChatColor.DARK_PURPLE}${ChatColor.BOLD}${ChatColor.ITALIC}Manhunt ${ChatColor.GRAY}4.0.0"
                prescore?.displaySlot = DisplaySlot.SIDEBAR
                prescore?.getScore("${ChatColor.YELLOW}${ChatColor.UNDERLINE}サーバー停止まで: $serverre")?.score = 0
                if (serverre==0) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "stop")
                }

            }
        }
    }
    private fun autowarifuri(){
        for (player in Bukkit.getOnlinePlayers()) {
            survivor?.addEntry(player.name)
        }
        val playersize = Bukkit.getOnlinePlayers().size
        val teamlist = ArrayList<String>()
        for (player in Bukkit.getOnlinePlayers()) {
            teamlist.add(player.name)

        }
        teamlist.shuffle()
        val hunters = playersize/3
        for (i in 0 until hunters){
            val chooseplayer = teamlist[1]
            teamlist.remove(chooseplayer)
            hunter?.addEntry(chooseplayer)

        }
    }
    private fun end(){
        Bukkit.broadcastMessage("${ChatColor.YELLOW}10秒後に初期地点にTPします。")

        val ranking: MutableMap<Int?, String> = TreeMap { m: Int?, n: Int? ->
            m!!.compareTo(
                n!!
            ) * -1
        }

        for (name in killsranking.keys) {
            ranking[killsranking[name]] = name.toString()
        }
        for (player in Bukkit.getOnlinePlayers()) {
            var i = 1
            for (nKey in ranking.keys) {
                val playername = Bukkit.getPlayer(UUID.fromString(ranking[nKey]))?.name
                player.sendMessage(ChatColor.YELLOW.toString() + i + ". " + ChatColor.AQUA + playername + ChatColor.WHITE + "(" + nKey + ")")
                i += 1
            }
        }
        object : BukkitRunnable() {
            override fun run() {
                for (player in Bukkit.getOnlinePlayers()){
                    player.teleport(Location(Bukkit.getWorld("world"),0.5,150.0,0.5))

                }
                for (player in  board?.getTeam("hunter")!!.entries){
                    arrive?.addEntry(player)
                    Bukkit.getPlayer(player)?.health = 40.0
                    Bukkit.getPlayer(player)?.foodLevel = 20
                }
                for (player in board?.getTeam("survivor")!!.entries){
                    arrive?.addEntry(player)
                    Bukkit.getPlayer(player)?.health = 40.0
                    Bukkit.getPlayer(player)?.foodLevel = 20
                }
                for (player in board?.getTeam("arrive")!!.entries){
                    Bukkit.getPlayer(player)?.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 6, false))
                }
                Bukkit.getWorld("world")?.worldBorder?.size = 100.0

            }
        }.runTaskLater(this, 200L)
    }
}