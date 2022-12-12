package com.github.zululi.minecrafthunters

import com.github.zululi.minecrafthunters.Minecrafthunters.Main.admin
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.board
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.endportal
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.gamestart
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.hour
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.hunter
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.min
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.sec
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.yousaix
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.yousaiz
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.startsec
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.survivor
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.survivorclear
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.hozon
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.portalhozon
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.portal
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.portalplace
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.block.BlockState
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EnderDragon
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.event.world.PortalCreateEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.CompassMeta
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.DisplaySlot
import java.util.*
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
        val hozon = mutableListOf<BlockState>()
        val portalhozon = arrayListOf<String>()
        var portal = 0
        var portalplace = ""
        var endportal = false
    }

    override fun onEnable() {
        // Plugin startup logic
        hunter?.unregister()
        survivor?.unregister()
        board = Bukkit.getScoreboardManager()?.mainScoreboard
        hunter = board?.registerNewTeam("hunter")
        survivor = board?.registerNewTeam("survivor")
        Bukkit.getPluginManager().registerEvents(this, this)
        sec()
        tick()
        hunter?.setAllowFriendlyFire(false)
        survivor?.setAllowFriendlyFire(false)
        hunter?.color = ChatColor.RED
        survivor?.color = ChatColor.GREEN
        admin?.color = ChatColor.GOLD
        admin?.prefix = "${ChatColor.GOLD}${ChatColor.UNDERLINE}[A] "
        hunter?.prefix = "${ChatColor.RED}[H] "
        survivor?.prefix = "${ChatColor.GREEN}[S] "
        Bukkit.getWorld("world")?.setGameRule(GameRule.SPAWN_RADIUS, 0)
        Bukkit.getWorld("world")?.setSpawnLocation(0, 200, 0)
        Bukkit.getWorld("world")?.worldBorder?.size = 20.0
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
    }

    @EventHandler
    fun onjoinplayer(e: PlayerJoinEvent) {
        val player = e.player
        e.joinMessage = "${ChatColor.YELLOW}${player.name} joined."
        if (gamestart == 1 && player.scoreboard.getEntryTeam(player.name) == null) {
            hunter?.addEntry(player.name)
            player.inventory.setItem(8, ItemStack(Material.COMPASS))
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
        if (item.name == "Material.COMPASS") {
            e.isCancelled = true
        } else if (item.name == "Lodestone Compass") {
            player.sendMessage(item.name)
            e.isCancelled = true
        }

        item.itemStack.itemMeta
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
        if (item.itemStack == ItemStack(Material.COMPASS)) {
            e.isCancelled = true
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
                            player.giveExp(70)
                        }

                        "COAL_ORE" -> {
                            val range = (1..5)
                            val random = range.random()
                            val gi = ItemStack(Material.COAL, random)
                            player.world.dropItemNaturally(blockplace, ItemStack(gi))
                            player.world.dropItemNaturally(blockplace, ItemStack(Material.COOKED_BEEF, 8))
                            player.giveExp(50)
                        }

                        else -> {
                            player.world.dropItemNaturally(blockplace, ItemStack(i))
                            player.giveExp(5)
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
        val death = e.deathMessage
        if (player.world.name == "world_the_end" && player.scoreboard.getEntryTeam(player.name)?.name == "survivor" && death?.contains(
                "place"
            ) == true
        ) {
            player.sendMessage("${ChatColor.GREEN}エンドでの落下死のためリスポーンしました。")
        } else if (player.scoreboard.getEntryTeam(player.name)?.name == "survivor") {
            hunter?.addEntry(player.name)
            e.keepInventory = true
            player.sendMessage("${ChatColor.RED}死亡したため、ハンターになりました。")
        } else if (player.scoreboard.getEntryTeam(player.name)?.name == "hunter") {
            player.sendMessage("${ChatColor.GREEN}リスポーンしました。")
            e.keepInventory = true
        }
        object : BukkitRunnable() {
            override fun run() {
                e.entity.spigot().respawn()
                player.inventory.setItem(8, ItemStack(Material.COMPASS))
                player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 6, false))

            }
        }.runTaskLater(this, 1L)
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
                    } else {
                        val x = location2.blockX
                        val z = location2.blockZ
                        val playerx = player.location.blockX
                        val playerz = player.location.blockZ
                        val diffarencex = x - playerx
                        val diffarencez = z - playerz
                        val distance = diffarencex * diffarencex + diffarencez * diffarencez
                        val result = sqrt(distance.toDouble())
                        player.sendMessage("${ChatColor.GREEN}村: " + location2.blockX + " ~ " + location2.blockZ + "  (残り${result.toInt()}ブロック)")
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
                                }
                            }
                        }
                    }

                    "world_nether" -> {
                        for (all in Bukkit.getOnlinePlayers()) {
                            if (all.world.name == "world_nether") {
                                if (all.scoreboard.getEntryTeam(all.name)?.name == "survivor") {

                                    playerlist.add(all.name)
                                }
                            }
                        }
                    }

                    "the_end" -> {
                        for (all in Bukkit.getOnlinePlayers()) {
                            if (all.world.name == "the_end") {
                                if (all.scoreboard.getEntryTeam(all.name)?.name == "survivor") {

                                    playerlist.add(all.name)
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
                            val compass = e.player.inventory.getItem(8)?.itemMeta as CompassMeta
                            compass.lodestone = Bukkit.getPlayer(chengeplayer)?.location
                            compass.isLodestoneTracked = false
                            e.player.inventory.getItem(8)?.itemMeta = compass
                            player.sendMessage("${ChatColor.YELLOW}${chengeplayer}の現在位置にターゲットしました。\n${ChatColor.GRAY}($plworld2${ChatColor.GRAY})")

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
                            player.sendMessage("${ChatColor.YELLOW}${chengeplayer}の現在位置にターゲットしました。\n${ChatColor.GRAY}($plworld2${ChatColor.GRAY})")
                        }
                    }

                    "the_end" -> {
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
                        }
                        player.sendMessage("${ChatColor.YELLOW}${chengeplayer}の現在位置にターゲットしました。\n${ChatColor.GRAY}($plworld2${ChatColor.GRAY})")
                    }

                }

                val location2 = player.world.locateNearestStructure(player.location, StructureType.VILLAGE, 32, false)
                if (location2 != null) {
                    val x = location2.blockX
                    val z = location2.blockZ
                    val playerx = player.location.blockX
                    val playerz = player.location.blockZ
                    val diffarencex = x - playerx
                    val diffarencez = z - playerz
                    val distance = diffarencex * diffarencex + diffarencez * diffarencez
                    val result = sqrt(distance.toDouble())
                    player.sendMessage("${ChatColor.GREEN}村: " + location2.blockX + " ~ " + location2.blockZ + "  (残り${result.toInt()}ブロック)\n${ChatColor.GRAY}コンパスには反映されません")
                }

                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1f, 1.0f)
            }
        }

        if (item?.toString() == "COMPASS") {
            val test = player.inventory.all(Material.COMPASS).keys

            for (int in test) {
                if (int != 8) {
                    player.inventory.setItem(int, ItemStack(Material.AIR))
                    player.inventory.setItem(8, ItemStack(Material.COMPASS))
                }
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
    fun clickevent(e: InventoryClickEvent) {
        val slot = e.slot
        val currentitem = e.currentItem
        val player = e.whoClicked

        if (slot == 8 && currentitem?.type.toString() == "COMPASS") {
            e.isCancelled = true
        }




        if (currentitem?.type.toString() == "AIR") {

            if (player.inventory.getItem(8)?.type.toString() == "null") {

                player.inventory.setItem(8, ItemStack(Material.COMPASS))
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
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when(command.name){
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
            "g"->{
                val player = sender.name
                var message = args[0]

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


                    min = args[0].toInt()
                    sec = args[1].toInt()
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
                                val woodensword = ItemStack(Material.STONE_SWORD)
                                val metadatasword = woodensword.itemMeta
                                metadatasword?.isUnbreakable = true
                                val l0: MutableList<String> = ArrayList()
                                l0.add("${ChatColor.GOLD}SoulBound")
                                metadatasword?.lore = (l0)
                                woodensword.itemMeta = metadatasword
                                all?.inventory?.setItem(0, woodensword)

                                val woodenpickaxe = ItemStack(Material.STONE_PICKAXE)
                                val metadatapickaxe = woodenpickaxe.itemMeta
                                metadatapickaxe?.isUnbreakable = true
                                val l1: MutableList<String> = ArrayList()
                                l1.add("${ChatColor.GOLD}SoulBound")
                                metadatapickaxe?.lore = (l1)
                                woodenpickaxe.itemMeta = metadatapickaxe
                                all?.inventory?.setItem(1, woodenpickaxe)

                                val woodenaxe = ItemStack(Material.STONE_AXE)
                                val metadataaxe = woodenaxe.itemMeta
                                val modifier = AttributeModifier(
                                    UUID.randomUUID(),
                                    "generic.attackDamage",
                                    2.0,
                                    AttributeModifier.Operation.ADD_NUMBER,
                                    EquipmentSlot.HAND
                                )
                                metadataaxe?.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, modifier)
                                metadataaxe?.isUnbreakable = true
                                val l2: MutableList<String> = ArrayList()
                                l2.add("${ChatColor.GOLD}SoulBound")
                                metadataaxe?.lore = (l2)
                                woodenaxe.itemMeta = metadataaxe
                                all?.inventory?.setItem(2, woodenaxe)


                                val woodenshovel = ItemStack(Material.STONE_SHOVEL)
                                val metadatashovel = woodenshovel.itemMeta
                                metadatashovel?.isUnbreakable = true
                                val l3: MutableList<String> = ArrayList()
                                l3.add("${ChatColor.GOLD}SoulBound")
                                metadatashovel?.lore = (l3)
                                woodenshovel.itemMeta = metadatashovel
                                all?.inventory?.setItem(3, woodenshovel)

                                all?.inventory?.setItem(4, ItemStack(Material.BREAD, 64))
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
                                player.inventory.setItem(8, ItemStack(Material.COMPASS))
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
                        gamescoreboard()
                    }
                }
            }
        }.runTaskTimer(this, 0, 20)
    }
    private fun tick(){
        object : BukkitRunnable() {
            override fun run() {
                for (all in Bukkit.getOnlinePlayers()){
                    val component = TextComponent()
                    component.text = "${ChatColor.YELLOW}現在の座標: ${all.location.blockX} ${all.location.blockY} ${all.location.blockZ}"
                    all.spigot().sendMessage(ChatMessageType.ACTION_BAR, component)
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

                        if (survivor?.size == 0){
                            for (player in Bukkit.getOnlinePlayers()){
                                player.playSound(player.location,Sound.ENTITY_GENERIC_EXPLODE,1f,1.0f)
                                player.playSound(player.location,Sound.ENTITY_FIREWORK_ROCKET_LAUNCH,1f,1.0f)
                                player.sendMessage("${ChatColor.GOLD}--------------------------------\n試合終了!\n${ChatColor.YELLOW}生存者がいなくなったため、ハンターの勝利です。${ChatColor.GOLD}--------------------------------\n")
                                cancel()
                                gamestart =2
                            }
                        }else if (hour == 1 && min == 30 && sec == 0){
                            for (player in Bukkit.getOnlinePlayers()){
                                player.playSound(player.location,Sound.ENTITY_GENERIC_EXPLODE,1f,1.0f)
                                player.playSound(player.location,Sound.ENTITY_FIREWORK_ROCKET_LAUNCH,1f,1.0f)
                                player.sendMessage("${ChatColor.GOLD}--------------------------------\n試合終了!\n${ChatColor.YELLOW}制限時間になったため、ハンターの勝利です。${ChatColor.GOLD}--------------------------------\n")
                                cancel()
                                gamestart =2
                            }
                        }else if (survivorclear){
                            for (player in Bukkit.getOnlinePlayers()){
                                player.playSound(player.location,Sound.ENTITY_GENERIC_EXPLODE,1f,1.0f)
                                player.playSound(player.location,Sound.ENTITY_FIREWORK_ROCKET_LAUNCH,1f,1.0f)
                                player.sendMessage("${ChatColor.GOLD}--------------------------------\n試合終了!\n${ChatColor.YELLOW}エンダードラゴンを倒したため、生存者の勝利です。${ChatColor.GOLD}--------------------------------\n")
                                cancel()
                                gamestart =2
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(this, 0, 0)
    }
    private fun prescoreboard(){
        Bukkit.getScoreboardManager()?.mainScoreboard?.getObjective("pre")?.unregister()
        val prescore = Bukkit.getScoreboardManager()?.mainScoreboard?.registerNewObjective("pre","Dummy" , "aaa")
        prescore?.displayName = "${ChatColor.DARK_PURPLE}${ChatColor.BOLD}${ChatColor.ITALIC}Manhunt ${ChatColor.GRAY}2.2.2"
        prescore?.displaySlot = DisplaySlot.SIDEBAR
        prescore?.getScore("${ChatColor.GOLD}サーバー人数: ${Bukkit.getOnlinePlayers().size}")?.score = 0
    }
    private fun gamescoreboard() {
        Bukkit.getScoreboardManager()?.mainScoreboard?.getObjective("game")?.unregister()
        val prescore = Bukkit.getScoreboardManager()?.mainScoreboard?.registerNewObjective("game","Dummy" , "aaa")
        prescore?.displayName = "${ChatColor.DARK_PURPLE}${ChatColor.BOLD}${ChatColor.ITALIC}Manhunt ${ChatColor.GRAY}2.2.2"
        prescore?.displaySlot = DisplaySlot.SIDEBAR
        prescore?.getScore("${ChatColor.GREEN}残り人数: ${survivor?.size}")?.score = 11
        prescore?.getScore("${ChatColor.RED}ハンター: ${hunter?.size}")?.score = 10
        prescore?.getScore("${ChatColor.GOLD}${ChatColor.STRIKETHROUGH}                 ")?.score = 12
        prescore?.getScore("${ChatColor.RED}${ChatColor.GOLD}${ChatColor.STRIKETHROUGH}                 ")?.score = 9

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
        if (endportal){
            prescore?.getScore("${ChatColor.LIGHT_PURPLE}${ChatColor.UNDERLINE}エンドポータル:")?.score = 2
            prescore?.getScore("${ChatColor.LIGHT_PURPLE}"+portalplace)?.score = 1
        }
        if (min in 0..9 &&sec in 0..9  ){
            prescore?.getScore("${ChatColor.GOLD}経過時間: $hour : 0$min : 0$sec")?.score = 15
        }else if (min in 0..9&&sec in 10..59){
            prescore?.getScore("${ChatColor.GOLD}経過時間: $hour : 0$min : $sec")?.score = 15
        }else if (min in 9..59&& sec in 0..9){
            prescore?.getScore("${ChatColor.GOLD}経過時間: $hour : $min : 0$sec")?.score = 15
        }else if (min in 10..59&&sec in 10..59){
            prescore?.getScore("${ChatColor.GOLD}経過時間: $hour : $min : $sec")?.score = 15
        }
        if (min in 0..2&&sec == 0) {
            prescore?.getScore("${ChatColor.GOLD}ハンター解放まで: ${3-min} : 00")?.score = 14
        }else if(min in 0..2&&sec in 1..50){
            prescore?.getScore("${ChatColor.GOLD}ハンター解放まで: ${2-min} : ${60-sec}")?.score = 14
        }else if (min in 0..2&&sec in 51..59){
            prescore?.getScore("${ChatColor.GOLD}ハンター解放まで: ${2-min} : 0${60-sec}")?.score = 14
        }
        if (min in 0..9&&sec == 0) {
            prescore?.getScore("${ChatColor.GOLD}満腹度回復終了まで: ${10-min} : 00")?.score = 13
        }else if (min in 0..9&&sec in 1..50){
            prescore?.getScore("${ChatColor.GOLD}満腹度回復終了まで: ${9-min} : ${60-sec}")?.score = 13

        }else if (min in 0..9&&sec in 51..59){
            prescore?.getScore("${ChatColor.GOLD}満腹度回復終了まで: ${9-min} : 0${60-sec}")?.score = 13
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

}