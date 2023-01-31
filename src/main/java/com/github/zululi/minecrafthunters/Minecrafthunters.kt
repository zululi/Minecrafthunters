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
import org.bukkit.attribute.AttributeModifier
import org.bukkit.block.BlockState
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.*
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.player.*
import org.bukkit.event.world.PortalCreateEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.meta.CompassMeta
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.DisplaySlot
import java.io.File
import java.io.IOException
import java.nio.channels.SelectionKey
import java.util.*
import kotlin.math.roundToInt
import kotlin.math.sqrt


class Minecrafthunters : JavaPlugin() , Listener, CommandExecutor,Plugin {
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
        var killsranking = mutableMapOf<UUID, Int>()
        var trackplayer = mutableMapOf<UUID, String>()
        var cooltime = arrayListOf<String>()
        var blocks = mutableMapOf<UUID, Int>()


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
        val hpscore = Bukkit.getScoreboardManager()?.mainScoreboard?.registerNewObjective("hp", "health", "hp")
        val kills =
            Bukkit.getScoreboardManager()?.mainScoreboard?.registerNewObjective("kills", "playerKillCount", "kills")
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

        //recipe setting

        val stoneaxe = ItemStack(Material.STONE_AXE, 1)
        val sim1 = stoneaxe.itemMeta
        sim1!!.addEnchant(Enchantment.DIG_SPEED, 2, true)
        sim1.addEnchant(Enchantment.FIRE_ASPECT, 1, true)
        sim1.setCustomModelData(1)
        sim1.isUnbreakable = true
        val smodifier1 = AttributeModifier(
            UUID.randomUUID(),
            "generic.attackDamage",
            5.0,
            AttributeModifier.Operation.ADD_NUMBER,
            EquipmentSlot.HAND
        )
        sim1.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, smodifier1)
        stoneaxe.itemMeta = sim1

        val ironaxe = ItemStack(Material.IRON_AXE, 1)
        val iim = ironaxe.itemMeta
        iim!!.addEnchant(Enchantment.DIG_SPEED, 2, true)
        iim.setCustomModelData(1)
        iim.isUnbreakable = true
        val imodifier = AttributeModifier(
            UUID.randomUUID(),
            "generic.attackDamage",
            6.5,
            AttributeModifier.Operation.ADD_NUMBER,
            EquipmentSlot.HAND
        )
        iim.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, imodifier)
        ironaxe.itemMeta = iim


        val diamondaxe = ItemStack(Material.DIAMOND_AXE, 1)
        val dim = diamondaxe.itemMeta
        dim!!.addEnchant(Enchantment.DIG_SPEED, 2, true)
        dim.setCustomModelData(1)
        dim.isUnbreakable = true
        val dmodifier = AttributeModifier(
            UUID.randomUUID(),
            "generic.attackDamage",
            8.5,
            AttributeModifier.Operation.ADD_NUMBER,
            EquipmentSlot.HAND
        )
        dim.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, dmodifier)
        diamondaxe.itemMeta = dim

        val netheriteaxe = ItemStack(Material.NETHERITE_AXE, 1)
        val nim = netheriteaxe.itemMeta
        nim!!.addEnchant(Enchantment.DIG_SPEED, 2, true)
        nim.setCustomModelData(1)
        nim.isUnbreakable = true
        val nmodifier = AttributeModifier(
            UUID.randomUUID(),
            "generic.attackDamage",
            10.0,
            AttributeModifier.Operation.ADD_NUMBER,
            EquipmentSlot.HAND
        )
        nim.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, nmodifier)
        netheriteaxe.itemMeta = nim

        val encih = ItemStack(Material.IRON_HELMET, 1)
        val ihim = encih.itemMeta
        ihim!!.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true)
        ihim.setCustomModelData(1)
        encih.itemMeta = ihim

        val encic = ItemStack(Material.IRON_CHESTPLATE, 1)
        val icim = encic.itemMeta
        icim!!.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true)
        icim.setCustomModelData(1)
        encic.itemMeta = icim

        val encil = ItemStack(Material.IRON_LEGGINGS, 1)
        val ilim = encil.itemMeta
        ilim!!.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true)
        ilim.setCustomModelData(1)
        encil.itemMeta = ilim

        val encib = ItemStack(Material.IRON_BOOTS, 1)
        val ibim = encib.itemMeta
        ibim!!.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true)
        ibim.setCustomModelData(1)
        encib.itemMeta = ibim

        val encdh = ItemStack(Material.DIAMOND_HELMET, 1)
        val ihdm = encdh.itemMeta
        ihdm!!.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true)
        ihdm.setCustomModelData(1)
        encdh.itemMeta = ihdm

        val encdc = ItemStack(Material.DIAMOND_CHESTPLATE, 1)
        val icdm = encdc.itemMeta
        icdm!!.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true)
        icdm.setCustomModelData(1)
        encdc.itemMeta = icdm

        val encdl = ItemStack(Material.DIAMOND_LEGGINGS, 1)
        val ildm = encdl.itemMeta
        ildm!!.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true)
        ildm.setCustomModelData(1)
        encdl.itemMeta = ildm

        val encdb = ItemStack(Material.DIAMOND_BOOTS, 1)
        val ibdm = encdb.itemMeta
        ibdm!!.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true)
        ibdm.setCustomModelData(1)
        encdb.itemMeta = ibdm

        val encds = ItemStack(Material.DIAMOND_SWORD, 1)
        val dsdm = encds.itemMeta
        dsdm!!.addEnchant(Enchantment.DAMAGE_ALL, 2, true)
        dsdm.setCustomModelData(1)
        encds.itemMeta = dsdm

        val encis = ItemStack(Material.IRON_SWORD, 1)
        val isdm = encis.itemMeta
        isdm!!.addEnchant(Enchantment.DAMAGE_ALL, 2, true)
        isdm.setCustomModelData(1)
        encis.itemMeta = isdm

        //----------------------------------------------------------------------------------
        val stone1 = ShapedRecipe(NamespacedKey(this, "stone1"), stoneaxe)
        stone1.shape(
            "00",
            "10",
            "1 "
        )
        stone1.setIngredient('0', Material.FURNACE)
        stone1.setIngredient('1', Material.STICK)

        val stone2 = ShapedRecipe(NamespacedKey(this, "stone2"), stoneaxe)
        stone2.shape(
            "00",
            "01",
            " 1"
        )
        stone2.setIngredient('0', Material.FURNACE)
        stone2.setIngredient('1', Material.STICK)


        val iron1 = ShapedRecipe(NamespacedKey(this, "iron1"), ironaxe)
        iron1.shape(
            "00",
            "10",
            "1 "
        )
        iron1.setIngredient('0', Material.IRON_BLOCK)
        iron1.setIngredient('1', Material.STICK)

        val iron2 = ShapedRecipe(NamespacedKey(this, "iron2"), ironaxe)
        iron2.shape(
            "00",
            "01",
            " 1"
        )
        iron2.setIngredient('0', Material.IRON_BLOCK)
        iron2.setIngredient('1', Material.STICK)

        val diamond1 = ShapedRecipe(NamespacedKey(this, "diamond1"), diamondaxe)
        diamond1.shape(
            "00",
            "10",
            "1 "
        )
        diamond1.setIngredient('0', Material.DIAMOND_BLOCK)
        diamond1.setIngredient('1', Material.STICK)

        val diamond2 = ShapedRecipe(NamespacedKey(this, "diamond2"), diamondaxe)
        diamond2.shape(
            "00",
            "01",
            " 1"
        )
        diamond2.setIngredient('0', Material.DIAMOND_BLOCK)
        diamond2.setIngredient('1', Material.STICK)

        val netherite1 = ShapedRecipe(NamespacedKey(this, "netherite1"), netheriteaxe)
        netherite1.shape(
            "00",
            "10",
            "1 "
        )
        netherite1.setIngredient('0', Material.NETHERITE_BLOCK)
        netherite1.setIngredient('1', Material.STICK)

        val netherite2 = ShapedRecipe(NamespacedKey(this, "netherite2"), netheriteaxe)
        netherite2.shape(
            "00",
            "01",
            " 1"
        )
        netherite2.setIngredient('0', Material.NETHERITE_BLOCK)
        netherite2.setIngredient('1', Material.STICK)


        val gapple = ShapedRecipe(NamespacedKey(this, "gapple"), ItemStack(Material.ENCHANTED_GOLDEN_APPLE))
        gapple.shape(
            "101",
            "020",
            "101"
        )
        gapple.setIngredient('0', Material.GOLD_BLOCK)
        gapple.setIngredient('1', Material.DIAMOND)
        gapple.setIngredient('2', Material.GOLDEN_APPLE)

        val ironupgrade1 = ShapedRecipe(NamespacedKey(this, "ironhelmet"), encih)
        ironupgrade1.shape(
            "000",
            "010",
            "000"
        )
        ironupgrade1.setIngredient('0', Material.IRON_BLOCK)
        ironupgrade1.setIngredient('1', Material.IRON_HELMET)

        val ironupgrade2 = ShapedRecipe(NamespacedKey(this, "ironchestplate"), encic)
        ironupgrade2.shape(
            "000",
            "010",
            "000"
        )
        ironupgrade2.setIngredient('0', Material.IRON_BLOCK)
        ironupgrade2.setIngredient('1', Material.IRON_CHESTPLATE)

        val ironupgrade3 = ShapedRecipe(NamespacedKey(this, "ironleggings"), encil)
        ironupgrade3.shape(
            "000",
            "010",
            "000"
        )
        ironupgrade3.setIngredient('0', Material.IRON_BLOCK)
        ironupgrade3.setIngredient('1', Material.IRON_LEGGINGS)

        val ironupgrade4 = ShapedRecipe(NamespacedKey(this, "ironboots"), encib)
        ironupgrade4.shape(
            "000",
            "010",
            "000"
        )
        ironupgrade4.setIngredient('0', Material.IRON_BLOCK)
        ironupgrade4.setIngredient('1', Material.IRON_BOOTS)

        val diamondupgrade1 = ShapedRecipe(NamespacedKey(this, "diamondhelmet"), encdh)
        diamondupgrade1.shape(
            "000",
            "010",
            "000"
        )
        diamondupgrade1.setIngredient('0', Material.DIAMOND_BLOCK)
        diamondupgrade1.setIngredient('1', Material.DIAMOND_HELMET)

        val diamondupgrade2 = ShapedRecipe(NamespacedKey(this, "diamondchestplate"), encdc)
        diamondupgrade2.shape(
            "000",
            "010",
            "000"
        )
        diamondupgrade2.setIngredient('0', Material.DIAMOND_BLOCK)
        diamondupgrade2.setIngredient('1', Material.DIAMOND_CHESTPLATE)

        val diamondupgrade3 = ShapedRecipe(NamespacedKey(this, "diamondleggings"), encdl)
        diamondupgrade3.shape(
            "000",
            "010",
            "000"
        )
        diamondupgrade3.setIngredient('0', Material.DIAMOND_BLOCK)
        diamondupgrade3.setIngredient('1', Material.DIAMOND_LEGGINGS)

        val diamondupgrade4 = ShapedRecipe(NamespacedKey(this, "diamondboots"), encdb)
        diamondupgrade4.shape(
            "000",
            "010",
            "000"
        )
        diamondupgrade4.setIngredient('0', Material.DIAMOND_BLOCK)
        diamondupgrade4.setIngredient('1', Material.DIAMOND_BOOTS)

        val ds = ShapedRecipe(NamespacedKey(this, "diamondsword"), encds)
        ds.shape(
            "000",
            "010",
            "000"
        )
        ds.setIngredient('0', Material.DIAMOND_BLOCK)
        ds.setIngredient('1', Material.DIAMOND_SWORD)

        val irons = ShapedRecipe(NamespacedKey(this, "ironsword"), encis)
        irons.shape(
            "000",
            "010",
            "000"
        )
        irons.setIngredient('0', Material.IRON_BLOCK)
        irons.setIngredient('1', Material.IRON_SWORD)
        //recipe list
        Bukkit.getServer().addRecipe(stone1)
        Bukkit.getServer().addRecipe(stone2)
        Bukkit.getServer().addRecipe(iron1)
        Bukkit.getServer().addRecipe(iron2)
        Bukkit.getServer().addRecipe(diamond1)
        Bukkit.getServer().addRecipe(diamond2)
        Bukkit.getServer().addRecipe(netherite1)
        Bukkit.getServer().addRecipe(gapple)
        Bukkit.getServer().addRecipe(ironupgrade1)
        Bukkit.getServer().addRecipe(ironupgrade2)
        Bukkit.getServer().addRecipe(ironupgrade3)
        Bukkit.getServer().addRecipe(ironupgrade4)
        Bukkit.getServer().addRecipe(diamondupgrade1)
        Bukkit.getServer().addRecipe(diamondupgrade2)
        Bukkit.getServer().addRecipe(diamondupgrade3)
        Bukkit.getServer().addRecipe(diamondupgrade4)
        Bukkit.getServer().addRecipe(ds)
        Bukkit.getServer().addRecipe(irons)

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

        for (player in Bukkit.getBannedPlayers()) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pardon ${player.name}")
        }
    }

    @EventHandler
    fun craftitem(e: CraftItemEvent) {
        val playera = e.viewers
        val item = e.recipe.result.type
        val itemmeta = e.recipe.result.itemMeta?.hasCustomModelData()
        for (player in playera) {
            if (item == Material.STONE_AXE || item == Material.IRON_AXE || item == Material.DIAMOND_AXE || item == Material.NETHERITE_AXE) {
                if (!itemmeta!!) {
                    e.isCancelled = true
                    player.sendMessage("${ChatColor.RED}この方法で作ることはできません。")
                    Bukkit.getPlayer(player.name)?.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f)
                }
            }
            if (item == Material.WOODEN_AXE) {
                e.isCancelled = true
                player.sendMessage("${ChatColor.RED}このアイテムは作れません。")
                Bukkit.getPlayer(player.name)?.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f)
            }
        }
    }

    @EventHandler
    fun pre(e: PrepareItemCraftEvent) {
        val playera = e.viewers
        val item = e.recipe?.result?.type
        var itemmeta = e.recipe?.result?.itemMeta
        for (player in playera) {
            if (item == Material.WOODEN_AXE) {
                val nim = itemmeta
                nim!!.addEnchant(Enchantment.DIG_SPEED, 2, true)
                nim.setCustomModelData(1)
                nim.isUnbreakable = true
                val nmodifier = AttributeModifier(
                    UUID.randomUUID(),
                    "generic.attackDamage",
                    4.0,
                    AttributeModifier.Operation.ADD_NUMBER,
                    EquipmentSlot.HAND
                )
                nim.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, nmodifier)
                itemmeta = nim
            }
        }
    }

    @EventHandler
    fun onjoinplayer(e: PlayerJoinEvent) {
        val player = e.player
        createplayerdata(player)

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
        if (gamestart == 1 && player.scoreboard.getEntryTeam(player.name) == null) {
            hunter?.addEntry(player.name)
            val compass = ItemStack(Material.COMPASS)
            val metadatacompass = compass.itemMeta
            metadatacompass?.isUnbreakable = true
            val l: MutableList<String> = ArrayList()
            l.add("${ChatColor.GOLD}SoulBound")
            metadatacompass?.addEnchant(Enchantment.VANISHING_CURSE, 1, true)
            metadatacompass?.lore = (l)
            compass.itemMeta = metadatacompass
            player.inventory.setItem(8, compass)
            killsranking = mutableMapOf(player.uniqueId to 0)
            val woodensword = ItemStack(Material.STONE_SWORD)
            val metadatasword = woodensword.itemMeta
            metadatasword?.isUnbreakable = true
            val l0: MutableList<String> = ArrayList()
            l0.add("${ChatColor.GOLD}SoulBound")
            metadatasword?.addEnchant(Enchantment.VANISHING_CURSE, 1, true)
            metadatasword?.lore = (l0)
            woodensword.itemMeta = metadatasword
            player.inventory.setItem(0, woodensword)

            val woodenpickaxe = ItemStack(Material.STONE_PICKAXE)
            val metadatapickaxe = woodenpickaxe.itemMeta
            metadatapickaxe?.isUnbreakable = true
            metadatapickaxe?.addEnchant(Enchantment.VANISHING_CURSE, 1, true)
            val l1: MutableList<String> = ArrayList()
            l1.add("${ChatColor.GOLD}SoulBound")
            metadatapickaxe?.lore = (l1)
            woodenpickaxe.itemMeta = metadatapickaxe
            player.inventory.setItem(1, woodenpickaxe)

            val woodenaxe = ItemStack(Material.STONE_AXE)
            val metadataaxe = woodenaxe.itemMeta
            metadataaxe?.addEnchant(Enchantment.VANISHING_CURSE, 1, true)

            metadataaxe?.isUnbreakable = true
            val l2: MutableList<String> = ArrayList()
            l2.add("${ChatColor.GOLD}SoulBound")
            metadataaxe?.lore = (l2)
            woodenaxe.itemMeta = metadataaxe
            player.inventory.setItem(2, woodenaxe)


            val woodenshovel = ItemStack(Material.STONE_SHOVEL)
            val metadatashovel = woodenshovel.itemMeta
            metadatashovel?.addEnchant(Enchantment.VANISHING_CURSE, 1, true)
            metadatashovel?.isUnbreakable = true
            val l3: MutableList<String> = ArrayList()
            l3.add("${ChatColor.GOLD}SoulBound")
            metadatashovel?.lore = (l3)
            woodenshovel.itemMeta = metadatashovel
            player.inventory.setItem(3, woodenshovel)

            player.inventory.setItem(4, ItemStack(Material.BREAD, 64))
        }
        if (gamestart in 2..3) {
            deathed?.addEntry(player.name)
            player.gameMode = GameMode.SPECTATOR
        }


    }


    @EventHandler
    fun onleaveplayer(e: PlayerQuitEvent) {
        val player = e.player
        if (player.scoreboard.getEntryTeam(player.name)?.name == "arrive") {
            deathed?.addEntry(player.name)
            player.gameMode = GameMode.SPECTATOR
            e.quitMessage = "${ChatColor.DARK_RED}${player.name}は死亡しました。"
            player.world.strikeLightningEffect(player.location)

        }
    }

    @EventHandler
    fun killedentity(e: EntityDeathEvent) {
        val entity = e.entity
        val killer = e.entity.killer!!
        if (entity is EnderDragon) {
            addxp(killer,50)
            killer.sendMessage("${ChatColor.GOLD}+50Xp")
            survivorclear = true

        }
        when (entity){
            is Zombie->{
            addxp(killer,1)
                killer.sendMessage("${ChatColor.GOLD}+1Xp")
            }
            is Skeleton->{
                addxp(killer,1)
                killer.sendMessage("${ChatColor.GOLD}+1Xp")
            }
            is Creeper->{
                addxp(killer,1)
                killer.sendMessage("${ChatColor.GOLD}+1Xp")
            }
            is Blaze->{
                addxp(killer,2)
                killer.sendMessage("${ChatColor.GOLD}+2Xp")
            }
            is Piglin->{
                addxp(killer,1)
                killer.sendMessage("${ChatColor.GOLD}+1Xp")
            }
            is PigZombie->{
                addxp(killer,1)
                killer.sendMessage("${ChatColor.GOLD}+1Xp")
            }
            is WitherSkeleton->{
                addxp(killer,1)
                killer.sendMessage("${ChatColor.GOLD}+1Xp")
            }
            is Enderman->{
                addxp(killer,2)
                killer.sendMessage("${ChatColor.GOLD}+2Xp")
            }
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
            "${ChatColor.GOLD}SoulBound" -> {
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
    fun disabledamage(e: EntityDamageEvent) {
        when (gamestart) {
            0 -> {
                e.isCancelled = true
            }

            1 -> {
                e.isCancelled = false
                val entity = e.entity
                val damage = e.damage
                if (entity is Player) {
                    entity.giveExp((damage * -5).toInt())
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

                        "DIAMOND_ORE" -> {
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
    fun playerdeathevent(e: PlayerDeathEvent) {
        val player = e.entity
        val killer = e.entity.killer?.uniqueId
        val killername = e.entity.killer
        val death = e.deathMessage
        if (killer != null) {
            killsranking[killer] = killsranking[killer]?.plus(1) ?: 1
            killername?.inventory?.addItem(ItemStack(Material.GOLDEN_APPLE, 10))
        }
        if (player.world.name == "world_the_end" && player.scoreboard.getEntryTeam(player.name)?.name == "survivor" && death?.contains(
                "place"
            ) == true
        ) {
            e.deathMessage = "${ChatColor.GREEN}${player.name}はエンドでの落下死のためリスポーンしました。"
            player.sendMessage("${ChatColor.GREEN}エンドでの落下死のためリスポーンしました。")
        } else if (player.scoreboard.getEntryTeam(player.name)?.name == "survivor") {

            e.keepInventory = true
            if (killer != null) {
                e.deathMessage =
                    "${ChatColor.RED}${player.name}はハンターになりました。${ChatColor.RED}${killername?.name} ${ChatColor.RESET}-> ${ChatColor.GREEN}${player.name}"
                killername?.sendMessage("${ChatColor.YELLOW}${player.name}を倒しました。${ChatColor.GOLD}+50Xp")
                setxp(killername!!, getxp(killername) + 50)
                setkills(killername, getkills(killername) + 1)

            } else {
                e.deathMessage =
                    "${ChatColor.RED}${player.name}はハンターになりました。"
            }


            hunter?.addEntry(player.name)
            player.sendMessage("${ChatColor.RED}死亡したため、ハンターになりました。")
            player.world.strikeLightningEffect(player.location)
        } else if (player.scoreboard.getEntryTeam(player.name)?.name == "hunter") {
            if (killer != null) {
                player.sendMessage("${ChatColor.GREEN}リスポーンしました。")
                setxp(killername!!,getxp(killername)+20)
                killername.sendMessage("${ChatColor.YELLOW}${player.name}を倒しました。${ChatColor.GOLD}+20Xp")

                e.keepInventory = true
            }else{
                e.keepInventory = true
                e.drops.clear()
            }

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
            if (e.player.inventory.getItem(8)?.type != Material.COMPASS) {
                player.sendMessage("${ChatColor.GRAY}コンパスがホットバーの一番右にないので調べられません。")
                return
            }
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
                    player.world.locateNearestStructure(player.location, StructureType.VILLAGE, 32, false)

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
                        "${ChatColor.GREEN}overworld"

                        if (e.player.inventory.getItem(8)!!
                                .isSimilar(ItemStack(Material.COMPASS)) || e.player.inventory.getItem(8) != ItemStack(
                                Material.AIR
                            )
                        ) {

                            val x = Bukkit.getPlayer(chengeplayer)?.location?.blockX
                            val y= Bukkit.getPlayer(chengeplayer)?.location?.blockY
                            val z = Bukkit.getPlayer(chengeplayer)?.location?.blockZ

                            val playerx = player.location.blockX
                            val playery = player.location.blockY

                            val playerz = player.location.blockZ
                            val diffarencex = x?.minus(playerx)
                            val diffarencez = z?.minus(playerz)
                            val distance = diffarencez?.times(diffarencez)
                                ?.let { (diffarencex?.times(diffarencex))!!.plus(it) }
                            val distancef = distance?.plus(Math.pow(y?.minus(playery)!!.toDouble(),2.0))
                            val result = sqrt(distancef!!.toDouble())
                            val result2 = result.roundToInt()
                            trackplayer[e.player.uniqueId] = Bukkit.getPlayer(chengeplayer)!!.name
                            player.sendMessage("${ChatColor.YELLOW}${chengeplayer}の現在位置にターゲットしました。\n${ChatColor.GRAY}(残り$result2 マス${ChatColor.GRAY})")

                        }
                    }

                    "world_nether" -> {
                        "${ChatColor.RED}nether"
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
                            val y = Bukkit.getPlayer(chengeplayer)?.location?.blockY
                            val z = Bukkit.getPlayer(chengeplayer)?.location?.blockZ

                            val playerx = player.location.blockX
                            val playery = player.location.blockY

                            val playerz = player.location.blockZ
                            val diffarencex = x?.minus(playerx)
                            val diffarencez = z?.minus(playerz)
                            val distance = diffarencez?.times(diffarencez)
                                ?.let { (diffarencex?.times(diffarencex))!!.plus(it) }
                            val distancef = distance?.plus(Math.pow(y?.minus(playery)!!.toDouble(),2.0))
                            val result = sqrt(distancef!!.toDouble())
                            val result2 = result.roundToInt()
                            trackplayer[e.player.uniqueId] = Bukkit.getPlayer(chengeplayer)!!.name
                            player.sendMessage("${ChatColor.YELLOW}${chengeplayer}の現在位置にターゲットしました。\n${ChatColor.GRAY}(残り$result2 マス${ChatColor.GRAY})")
                        }
                    }

                    "world_the_end" -> {
                        "${ChatColor.DARK_PURPLE}end"
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
                            val y = Bukkit.getPlayer(chengeplayer)?.location?.blockY
                            val z = Bukkit.getPlayer(chengeplayer)?.location?.blockZ

                            val playerx = player.location.blockX
                            val playery = player.location.blockY

                            val playerz = player.location.blockZ
                            val diffarencex = x?.minus(playerx)
                            val diffarencez = z?.minus(playerz)
                            val distance = diffarencez?.times(diffarencez)
                                ?.let { (diffarencex?.times(diffarencex))!!.plus(it) }
                            val distancef = distance?.plus(Math.pow(y?.minus(playery)!!.toDouble(),2.0))
                            val result = sqrt(distancef!!.toDouble())
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
        if (e.cause == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            portalplace = "${e.player.location.x.toInt()},${e.player.location.y.toInt()},${e.player.location.z.toInt()}"
            endportal = true
        }
    }

    @EventHandler
    fun PlayerAttack(e: EntityDamageByEntityEvent) {
        if (e.entity is Player || e.entityType == EntityType.ARROW && e.damager is Player) {
            // Your code here.
            val player = e.entity
            val attacker = e.damager
            object : BukkitRunnable() {
                override fun run() {
                    val health = Bukkit.getPlayer(player.name)?.health
                    if (health != null) {
                        if (gamestart != 0) {
                            attacker.sendMessage("${ChatColor.GREEN}${ChatColor.UNDERLINE}${player.name}${ChatColor.RESET}${ChatColor.YELLOW}はあと${ChatColor.RED}${(health * 10.0).roundToInt() / 10.0} HP${ChatColor.YELLOW}です。")
                        }
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
                    2f,
                    1f
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
    fun move(e: PlayerMoveEvent) {
        val player = e.player
        val trakingplayer = trackplayer[player.uniqueId]
        if (player.name in cooltime) {
            return
        }
        if (e.player.inventory.getItem(8)?.type == Material.COMPASS) {
            val compass = e.player.inventory.getItem(8)?.itemMeta as CompassMeta
            if (trakingplayer != null && player.scoreboard.getEntryTeam(player.name)?.name == "hunter") {
                val x = Bukkit.getPlayer(trakingplayer)?.location?.blockX
                val y = Bukkit.getPlayer(trakingplayer)?.location?.blockY
                val z = Bukkit.getPlayer(trakingplayer)?.location?.blockZ

                val playerx = player.location.blockX
                val playery = player.location.blockY

                val playerz = player.location.blockZ
                val diffarencex = x?.minus(playerx)
                val diffarencez = z?.minus(playerz)
                val distance = diffarencez?.times(diffarencez)
                    ?.let { (diffarencex?.times(diffarencex))!!.plus(it) }
                val distancef = distance?.plus(Math.pow(y?.minus(playery)!!.toDouble(),2.0))
                val result = sqrt(distancef!!.toDouble())
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
        } else {
            blocks[player.uniqueId] = 123456789
            return
        }

        cooltime.add(player.name)
        object : BukkitRunnable() {
            override fun run() {
                cooltime.remove(player.name)
            }
        }.runTaskLater(this, 2)
    }


    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when (command.name) {
            "playerstats" -> {

                if (args.isEmpty()) {
                    sender.sendMessage("${ChatColor.GOLD}-------${ChatColor.RED}${sender.name}${ChatColor.YELLOW}の情報${ChatColor.GOLD}-------")
                    sender.sendMessage("${ChatColor.YELLOW}ランク:${getrank(Bukkit.getPlayer(sender.name)!!)}")
                    sender.sendMessage("${ChatColor.YELLOW}レベルアップまで:${getrankupremaining(getrank(Bukkit.getPlayer(sender.name)!!),getxp(Bukkit.getPlayer(sender.name)!!))}")
                    sender.sendMessage("${ChatColor.YELLOW}キル数:${getkills(Bukkit.getPlayer(sender.name)!!)}")
                    sender.sendMessage("${ChatColor.GOLD}-----------------------")
                    Bukkit.getPlayer(sender.name)?.playSound(Bukkit.getPlayer(sender.name)?.location!!,Sound.ENTITY_ITEM_PICKUP,1.0f,1.0f)
                    return false
                }
                if (args.size == 1){
                    if (Bukkit.getPlayer(args[0])?.isOnline!!) {
                        sender.sendMessage("${ChatColor.GOLD}-------${ChatColor.RED}${args[0]}${ChatColor.YELLOW}の情報${ChatColor.GOLD}-------")
                        sender.sendMessage("${ChatColor.YELLOW}ランク:${getrank(Bukkit.getPlayer(args[0])!!)}")
                        sender.sendMessage(
                            "${ChatColor.YELLOW}レベルアップまで:${
                                getrankupremaining(
                                    getrank(
                                        Bukkit.getPlayer(
                                            args[0]
                                        )!!
                                    ), getxp(Bukkit.getPlayer(args[0])!!)
                                )
                            }"
                        )
                        sender.sendMessage("${ChatColor.YELLOW}キル数:${getkills(Bukkit.getPlayer(args[0])!!)}")
                        sender.sendMessage("${ChatColor.GOLD}-----------------------")

                        Bukkit.getPlayer(sender.name)?.playSound(Bukkit.getPlayer(sender.name)?.location!!,Sound.ENTITY_ITEM_PICKUP,1.0f,1.0f)
                        return false
                    }else{
                        sender.sendMessage("そのプレイヤーはオフラインです。")
                    }
                }else{
                    sender.sendMessage("そのプレイヤーはオフラインです。")
                }

                if (sender.isOp) {
                    if (args[0] == "add") {
                        if (args.size == 3) {
                            val player: OfflinePlayer = Bukkit.getOfflinePlayer(args[1])
                            if (player.isOnline) {
                                setxp(player.player!!, getxp(player.player!!) + Integer.valueOf(args[2]))
                                checkrankup(player.player!!,getrank(player.player!!), getxp(player.player!!))
                                changedisplayname(Bukkit.getPlayer(sender.name)!!)
                            } else {
                                sender.sendMessage("${ChatColor.RED}そのプレイヤーはオフラインです。")
                            }
                        }
                    }
                }
                if (sender.isOp) {
                    if (args[0] == "remove") {
                        if (args.size == 3) {
                            val player: OfflinePlayer = Bukkit.getOfflinePlayer(args[1])
                            if (player.isOnline) {
                                setxp(player.player!!, getxp(player.player!!) - Integer.valueOf(args[2]))
                                checkrankup(player.player!!,getrank(player.player!!), getxp(player.player!!))
                                changedisplayname(Bukkit.getPlayer(sender.name)!!)
                            } else {
                                sender.sendMessage("${ChatColor.RED}そのプレイヤーはオフラインです。")
                            }
                        }
                    }
                }
            }

            "start" -> {
                if (sender.isOp) {
                    when (args[1]) {
                        "/y"->
                        if (Bukkit.getOnlinePlayers().size > 2) {
                            startsec = 10
                            min = 0
                            Bukkit.getWorld("world")?.time = 0
                            autowarifuri()

                        } else {
                            sender.sendMessage("${ChatColor.RED}You must have at least 3 online players to start")
                        }
                        "/n"->{
                            if (Bukkit.getOnlinePlayers().size > 2) {
                                startsec = 10
                                min = 0
                                Bukkit.getWorld("world")?.time = 0

                            } else {
                                sender.sendMessage("${ChatColor.RED}You must have at least 3 online players to start")
                            }
                        }
                        else->{
                            sender.sendMessage("${ChatColor.RED}コマンドが不完全です。\n/start [/y or /n]")
                        }
                    }

                    val location =
                        Bukkit.getPlayer(sender.name)?.world?.locateNearestStructure(
                            Bukkit.getPlayer(sender.name)!!.location,
                            StructureType.STRONGHOLD,
                            90000,
                            false
                        )
                    if (location == null) {
                        sender.sendMessage("${ChatColor.RED}要塞が見つかりませんでした(´・ω・`)")
                    } else {
                        yousaix = location.blockX
                        yousaiz = location.blockZ
                    }
                    Bukkit.getWorld("world")?.worldBorder?.setCenter(0.0, 0.0)

                    Bukkit.getWorld("world")?.setSpawnLocation(0, 200, 0)

                }
            }

            "qs" -> {
                if (sender.isOp) {
                    startsec = 1
                    autowarifuri()
                    Bukkit.getWorld("world")?.time = 0
                    val location =
                        Bukkit.getPlayer(sender.name)?.world?.locateNearestStructure(
                            Bukkit.getPlayer(sender.name)!!.location,
                            StructureType.STRONGHOLD,
                            32,
                            false
                        )
                    if (location == null) {
                        sender.sendMessage("${ChatColor.RED}要塞が見つかりませんでした(´・ω・`)")
                    } else {
                        yousaix = location.blockX
                        yousaiz = location.blockZ
                        Bukkit.getWorld("world")?.worldBorder?.setCenter(0.0, 0.0)
                        Bukkit.getWorld("world")?.worldBorder?.size = 20.0
                    }


                }
            }

            "compass" -> {
                Bukkit.getPlayer(sender.name)?.inventory?.addItem(ItemStack(Material.COMPASS))
                sender.sendMessage("${ChatColor.GRAY}コンパスを渡しました。")
            }

            "return" -> {
                if (sender.isOp) {
                    gamestart = 0
                    survivorclear = false
                }
            }

            "g" -> {
                val player = sender.name
                val message = args[0]

                when (Bukkit.getPlayer(player)?.scoreboard?.getEntryTeam(player)?.name) {
                    "hunter" -> {
                        for (all in Bukkit.getOnlinePlayers()) {
                            Bukkit.getPlayer(all.name)
                                ?.sendMessage("${ChatColor.DARK_PURPLE}[Global] ${ChatColor.RED}${player}${ChatColor.YELLOW} : ${ChatColor.RESET}${message}")
                        }
                    }

                    "survivor" -> {

                        for (all in Bukkit.getOnlinePlayers()) {
                            Bukkit.getPlayer(all.name)
                                ?.sendMessage("${ChatColor.DARK_PURPLE}[Global] ${ChatColor.GREEN}${player}${ChatColor.YELLOW} : ${ChatColor.RESET}${message}")
                        }
                    }

                    else -> {
                        for (all in Bukkit.getOnlinePlayers()) {
                            Bukkit.getPlayer(all.name)
                                ?.sendMessage("${ChatColor.DARK_PURPLE}[Global] ${ChatColor.RESET}${player}${ChatColor.YELLOW} : ${ChatColor.RESET}${message}")
                        }
                    }
                }
            }

            "settimer" -> {
                if (sender.isOp) {


                    hour = args[0].toInt()
                    min = args[1].toInt()
                    sec = args[2].toInt()
                }
            }
        }
        return true
    }

    private fun sec() {
        object : BukkitRunnable() {
            override fun run() {
                for (player in Bukkit.getOnlinePlayers()) {
                    player.playerListHeader = ("${ChatColor.YELLOW}レベルアップまで残り${getrankupremaining(getrank(player),getxp(player))}")
                    changedisplayname(player)
                }



                when (gamestart) {
                    0 -> {
                        prescoreboard()
                        for (player in Bukkit.getOnlinePlayers()) {
                            player.foodLevel = 20
                            player.health = 40.0
                            player.saturation = 20F
                        }
                        if (startsec > 0) {
                            if (startsec in 6..10) {
                                Bukkit.broadcastMessage("${ChatColor.YELLOW}試合開始まで${ChatColor.GOLD} $startsec${ChatColor.GOLD} 秒")
                            } else if (startsec in 0..5) {
                                Bukkit.broadcastMessage("${ChatColor.YELLOW}試合開始まで${ChatColor.RED} $startsec${ChatColor.GOLD} 秒")
                            }
                            startsec -= 1
                            for (player in Bukkit.getOnlinePlayers()) {
                                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1.0F, 1.0F)
                            }
                        } else if (startsec == 0) {
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
                                val smodifier1 = AttributeModifier(
                                    UUID.randomUUID(),
                                    "generic.attackDamage",
                                    5.0,
                                    AttributeModifier.Operation.ADD_NUMBER,
                                    EquipmentSlot.HAND
                                )
                                metadataaxe?.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, smodifier1)
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
                            for (x in -10..10) {
                                for (z in -10..10) {
                                    val block = Bukkit.getWorld("world")?.getBlockAt(x, 148, z)
                                    val setblock = Material.getMaterial("GLASS")!!.createBlockData()
                                    block?.blockData = setblock
                                }
                            }
                            for (player in board?.getTeam("hunter")!!.entries) {
                                Bukkit.getPlayer(player)?.teleport(Location(Bukkit.getWorld("world"), 0.5, 150.0, 0.5))
                            }
                            for (player in Bukkit.getOnlinePlayers()) {
                                player.playSound(player.location, Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F)
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

                    1 -> {

                        sec++
                        if (min == 59 && sec == 60) {
                            min = 0
                            sec = 0
                            hour++
                        } else if (sec == 60) {
                            sec = 0
                            min++
                        }
                        if (hour == 0 && min == 3 && sec == 0) {
                            for (x in -10..10) {
                                for (z in -10..10) {
                                    val block = Bukkit.getWorld("world")?.getBlockAt(x, 148, z)
                                    val setblock = Material.getMaterial("AIR")!!.createBlockData()
                                    block?.blockData = setblock
                                    for (player in board?.getTeam("hunter")!!.entries) {
                                        Bukkit.getPlayer(player)?.addPotionEffect(
                                            PotionEffect(
                                                PotionEffectType.DAMAGE_RESISTANCE,
                                                1200,
                                                6,
                                                true
                                            )
                                        )
                                    }


                                }
                            }
                            for (player in Bukkit.getOnlinePlayers()) {
                                player.playSound(player.location, Sound.ENTITY_WITHER_AMBIENT, 1.0f, 2.0f)
                            }
                            Bukkit.broadcastMessage("${ChatColor.GOLD}----------------------------------")
                            Bukkit.broadcastMessage("${ChatColor.GOLD}   ハンターが放出されました。")
                            Bukkit.broadcastMessage("${ChatColor.GOLD}----------------------------------")
                        }
                        if (hour == 0 && min in 0..9) {
                            for (player in Bukkit.getOnlinePlayers()) {
                                player.saturation = 20f
                                player.foodLevel = 20
                            }
                        } else if (hour == 0 && min == 10 && sec == 0) {
                            for (player in Bukkit.getOnlinePlayers()) {
                                player.sendMessage("${ChatColor.GOLD}----------------------------------")
                                player.sendMessage("${ChatColor.GOLD}  満腹度が減るようになりました。")
                                player.sendMessage("${ChatColor.GOLD}----------------------------------")
                                player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f)
                            }
                        }
                        if (hour == 1 && min == 0 && sec == 0 || hour == 1 && min == 10 && sec == 0 || hour == 1 && min == 20 && sec == 0 ||
                            hour == 1 && min == 25 && sec == 0 || hour == 1 && min == 27 && sec == 0 || hour == 1 && min == 28 && sec == 0 || hour == 1 && min == 29 && sec == 0
                        ) {
                            for (player in Bukkit.getOnlinePlayers()) {
                                player.sendMessage("${ChatColor.GOLD}ゲーム終了まで${ChatColor.RED}${30 - min}${ChatColor.GOLD}分")
                                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1f, 1f)
                            }
                        } else if (hour == 1 && min == 29 && sec in 45..60) {
                            for (player in Bukkit.getOnlinePlayers()) {
                                player.sendMessage("${ChatColor.GOLD}ゲーム終了まで${ChatColor.RED}${60 - sec}${ChatColor.GOLD}秒")
                                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1f, 1f)
                            }
                        }
                        gamescoreboard()
                    }

                    2 -> {
                        gamescoreboard()
                    }

                    3 -> {
                        gamescoreboard()
                        serverre--
                    }
                }
            }
        }.runTaskTimer(this, 0, 20)
    }

    private fun tick() {
        object : BukkitRunnable() {
            override fun run() {
                for (all in Bukkit.getOnlinePlayers()) {
                    if (all.scoreboard.getEntryTeam(all.name)?.name == "hunter" && trackplayer[all.uniqueId] != null) {
                        if (all.world == Bukkit.getPlayer(trackplayer[all.uniqueId].toString())!!.world) {
                            if (blocks[all.uniqueId] == 123456789) {
                                val component = TextComponent()
                                component.text =
                                    "${ChatColor.YELLOW}現在の座標: ${all.location.blockX} ${all.location.blockY} ${all.location.blockZ} ${ChatColor.GREEN}${trackplayer[all.uniqueId]}: 残り?マス"
                                all.spigot().sendMessage(ChatMessageType.ACTION_BAR, component)
                            } else {
                                val component = TextComponent()
                                component.text =
                                    "${ChatColor.YELLOW}現在の座標: ${all.location.blockX} ${all.location.blockY} ${all.location.blockZ} ${ChatColor.GREEN}${trackplayer[all.uniqueId]}: 残り${blocks[all.uniqueId]}マス"
                                all.spigot().sendMessage(ChatMessageType.ACTION_BAR, component)
                            }
                        } else {
                            if (blocks[all.uniqueId] == 123456789) {
                                val component = TextComponent()
                                component.text =
                                    "${ChatColor.YELLOW}現在の座標: ${all.location.blockX} ${all.location.blockY} ${all.location.blockZ} ${ChatColor.GREEN}${trackplayer[all.uniqueId]}: 残り?マス"
                                all.spigot().sendMessage(ChatMessageType.ACTION_BAR, component)
                            }
                            val component = TextComponent()
                            component.text =
                                "${ChatColor.YELLOW}現在の座標: ${all.location.blockX} ${all.location.blockY} ${all.location.blockZ} ${ChatColor.GREEN}${trackplayer[all.uniqueId]}: ${ChatColor.RED}${ChatColor.UNDERLINE}ここにはいません"
                            all.spigot().sendMessage(ChatMessageType.ACTION_BAR, component)
                        }

                    } else if (all.scoreboard.getEntryTeam(all.name)?.name == "survivor") {
                        if (blocks[all.uniqueId] == 123456789) {
                            val component = TextComponent()
                            component.text =
                                "${ChatColor.YELLOW}現在の座標: ${all.location.blockX} ${all.location.blockY} ${all.location.blockZ} ${ChatColor.GREEN} 残り?マス"
                            all.spigot().sendMessage(ChatMessageType.ACTION_BAR, component)
                        } else {
                            val component = TextComponent()
                            component.text =
                                "${ChatColor.YELLOW}現在の座標: ${all.location.blockX} ${all.location.blockY} ${all.location.blockZ} ${ChatColor.GREEN}残り${blocks[all.uniqueId]}マス"
                            all.spigot().sendMessage(ChatMessageType.ACTION_BAR, component)
                        }
                    }
                }
                when (gamestart) {
                    0 -> {
                        for (all in Bukkit.getOnlinePlayers()) {
                            Bukkit.getPlayer(all.name)?.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = 40.0
                        }
                    }

                    1 -> {
                        for (all in Bukkit.getOnlinePlayers()) {
                            Bukkit.getPlayer(all.name)?.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = 40.0
                        }

                        if (survivorplayer == 0) {
                            for (player in Bukkit.getOnlinePlayers()) {
                                player.playSound(player.location, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1.0f)
                                player.playSound(player.location, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1f, 1.0f)
                                player.sendMessage("${ChatColor.GOLD}--------------------------------\n試合終了!\n${ChatColor.YELLOW}生存者がいなくなったため、ハンターの勝利です。${ChatColor.GOLD}--------------------------------\n")
                                cancel()
                                gamestart = 2

                            }
                            for (player1 in board?.getTeam("hunter")!!.entries) {
                                setxp(Bukkit.getPlayer(player1)!!,getxp(Bukkit.getPlayer(player1)!!)+25)
                                Bukkit.getPlayer(player1)?.sendMessage("${ChatColor.GOLD}+25Xp")
                            }
                            end()
                        } else if (hour == 1 && min == 30 && sec == 0) {
                            for (player in Bukkit.getOnlinePlayers()) {
                                player.playSound(player.location, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1.0f)
                                player.playSound(player.location, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1f, 1.0f)
                                player.sendMessage("${ChatColor.GOLD}--------------------------------\n試合終了!\n${ChatColor.YELLOW}制限時間になったため、ハンターの勝利です。${ChatColor.GOLD}--------------------------------\n")
                                cancel()
                                gamestart = 2

                            }
                            for (player1 in board?.getTeam("hunter")!!.entries) {
                                setxp(Bukkit.getPlayer(player1)!!,getxp(Bukkit.getPlayer(player1)!!)+50)
                                Bukkit.getPlayer(player1)?.sendMessage("${ChatColor.GOLD}+50Xp")
                            }
                            for (player2 in board?.getTeam("survivor")!!.entries) {
                                setxp(Bukkit.getPlayer(player2)!!,getxp(Bukkit.getPlayer(player2)!!)+25)
                                Bukkit.getPlayer(player2)?.sendMessage("${ChatColor.GOLD}+25Xp")
                            }
                            end()
                        } else if (survivorclear) {
                            for (player in Bukkit.getOnlinePlayers()) {
                                player.playSound(player.location, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1.0f)
                                player.playSound(player.location, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1f, 1.0f)
                                player.sendMessage("${ChatColor.GOLD}--------------------------------\n試合終了!\n${ChatColor.YELLOW}エンダードラゴンを倒したため、生存者の勝利です。${ChatColor.GOLD}--------------------------------\n")
                                cancel()
                                gamestart = 2

                            }
                            for (player1 in board?.getTeam("survivor")!!.entries) {
                                setxp(Bukkit.getPlayer(player1)!!,getxp(Bukkit.getPlayer(player1)!!)+100)
                                Bukkit.getPlayer(player1)?.sendMessage("${ChatColor.GOLD}+100Xp")
                            }
                            for (player2 in board?.getTeam("hunter")!!.entries) {
                                setxp(Bukkit.getPlayer(player2)!!,getxp(Bukkit.getPlayer(player2)!!)+50)
                                Bukkit.getPlayer(player2)?.sendMessage("${ChatColor.GOLD}+50Xp")
                            }
                            end()
                        }
                    }
                }
            }
        }.runTaskTimer(this, 0, 0)
    }

    private fun prescoreboard() {
        Bukkit.getScoreboardManager()?.mainScoreboard?.getObjective("pre")?.unregister()
        val prescore = Bukkit.getScoreboardManager()?.mainScoreboard?.registerNewObjective("pre", "Dummy", "aaa")
        prescore?.displayName =
            "${ChatColor.DARK_PURPLE}${ChatColor.BOLD}${ChatColor.ITALIC}Manhunt ${ChatColor.GRAY}5.0.0"
        prescore?.displaySlot = DisplaySlot.SIDEBAR
        prescore?.getScore("${ChatColor.GOLD}サーバー人数: ${Bukkit.getOnlinePlayers().size}")?.score = 0
    }

    private fun gamescoreboard() {
        when (gamestart) {
            1 -> {
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
                    "${ChatColor.DARK_PURPLE}${ChatColor.BOLD}${ChatColor.ITALIC}Manhunt ${ChatColor.GRAY}5.0.0"
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
                    prescore?.getScore("${ChatColor.YELLOW}ハンター解放まで: ${3 - min} : 00")?.score = 14
                } else if (hour == 0 && min in 0..2 && sec in 1..50) {
                    prescore?.getScore("${ChatColor.YELLOW}ハンター解放まで: ${2 - min} : ${60 - sec}")?.score = 14
                } else if (hour == 0 && min in 0..2 && sec in 51..59) {
                    prescore?.getScore("${ChatColor.YELLOW}ハンター解放まで: ${2 - min} : 0${60 - sec}")?.score = 14
                }
                if (hour == 0 && min in 0..9 && sec == 0) {
                    prescore?.getScore("${ChatColor.GRAY}満腹度回復終了まで: ${10 - min} : 00")?.score = 13
                } else if (hour == 0 && min in 0..9 && sec in 1..50) {
                    prescore?.getScore("${ChatColor.GRAY}満腹度回復終了まで: ${9 - min} : ${60 - sec}")?.score = 13

                } else if (hour == 0 && min in 0..9 && sec in 51..59) {
                    prescore?.getScore("${ChatColor.GRAY}満腹度回復終了まで: ${9 - min} : 0${60 - sec}")?.score = 13
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

            2 -> {
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
                    "${ChatColor.DARK_PURPLE}${ChatColor.BOLD}${ChatColor.ITALIC}Manhunt ${ChatColor.GRAY}5.0.0"
                prescore?.displaySlot = DisplaySlot.SIDEBAR
                prescore?.getScore("${ChatColor.YELLOW}${ChatColor.UNDERLINE}残り人数: $arplayer")?.score = 0
                for (a in Bukkit.getOnlinePlayers()) {
                    a.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, 999999999, 1, true))

                }
                if (arplayer == 1) {
                    val player = arrive!!.entries //エントリーの全体
                    var winner = ""
                    for (b in player) {
                        for (a in Bukkit.getOfflinePlayers()) { //オンラインプレイヤーの全体
                            for (c in Bukkit.getOnlinePlayers()) {
                                if (!player.contains(a.name)) {
                                    if (b.contains(c.name)) {
                                        winner = b
                                    }
                                }
                            }
                        }
                    }
                    Bukkit.broadcastMessage("${ChatColor.GOLD}${winner}が優勝しました。")
                    Bukkit.getPlayer(winner)?.gameMode = GameMode.CREATIVE
                    Bukkit.getWorld("world")?.worldBorder?.size = 99999999.9

                    for (p in Bukkit.getOnlinePlayers()) {
                        p.playSound(p.location, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1.0f)
                        p.playSound(p.location, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1f, 1.0f)
                    }
                    gamestart = 3
                }
            }

            3 -> {
                Bukkit.getScoreboardManager()?.mainScoreboard?.getObjective("game")?.unregister()
                val prescore =
                    Bukkit.getScoreboardManager()?.mainScoreboard?.registerNewObjective("game", "Dummy", "game")

                prescore?.displayName =
                    "${ChatColor.DARK_PURPLE}${ChatColor.BOLD}${ChatColor.ITALIC}Manhunt ${ChatColor.GRAY}5.0.0"
                prescore?.displaySlot = DisplaySlot.SIDEBAR
                prescore?.getScore("${ChatColor.YELLOW}${ChatColor.UNDERLINE}サーバー停止まで: $serverre")?.score = 0
                if (serverre == 0) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "stop")
                }

            }
        }
    }

    private fun autowarifuri() {
        for (player in Bukkit.getOnlinePlayers()) {
            survivor?.addEntry(player.name)
        }
        val playersize = Bukkit.getOnlinePlayers().size
        val teamlist = ArrayList<String>()
        for (player in Bukkit.getOnlinePlayers()) {
            teamlist.add(player.name)

        }
        teamlist.shuffle()
        val hunters = playersize / 3
        for (i in 0 until hunters) {
            val chooseplayer = teamlist[1]
            teamlist.remove(chooseplayer)
            hunter?.addEntry(chooseplayer)

        }
    }

    private fun end() {
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
                for (player in Bukkit.getOnlinePlayers()) {
                    player.teleport(Location(Bukkit.getWorld("world"), 0.5, 150.0, 0.5))

                }
                for (player in board?.getTeam("hunter")!!.entries) {
                    arrive?.addEntry(player)
                    Bukkit.getPlayer(player)?.health = 40.0
                    Bukkit.getPlayer(player)?.foodLevel = 20
                }
                for (player in board?.getTeam("survivor")!!.entries) {
                    arrive?.addEntry(player)
                    Bukkit.getPlayer(player)?.health = 40.0
                    Bukkit.getPlayer(player)?.foodLevel = 20
                }
                for (player in board?.getTeam("arrive")!!.entries) {
                    Bukkit.getPlayer(player)
                        ?.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 6, false))
                }
                Bukkit.getWorld("world")?.worldBorder?.size = 100.0

            }
        }.runTaskLater(this, 200L)
    }

    private fun existplayerdata(player: Player): Boolean {
        val file = File("" + this.dataFolder + "/player-data/", player.uniqueId.toString() + ".yml")
        return file.exists()
    }

    private fun createplayerdata(player: Player) {
        if (!existplayerdata(player)) {
            val file = File("" + this.dataFolder + "/player-data/", player.uniqueId.toString() + ".yml")
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val config = YamlConfiguration.loadConfiguration(file)
            config.set("playername", player.name)
            config.set("rank", 1)
            config.set("xp", 0)
            config.set("kills", 0)
            config.set("wins", 0)
            try {
                config.save(file)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun getplayerdata(player: Player): File? {
        return if (existplayerdata(player)) {
            File("" + this.dataFolder + "/player-data/", player.uniqueId.toString() + ".yml")
        } else {
            null
        }
    }

    private fun getrank(player: Player): Int {
        val file = getplayerdata(player)
        val config = file?.let { YamlConfiguration.loadConfiguration(it) }

        return Integer.parseInt(config?.get("rank").toString())
    }

    private fun setrank(player: Player, rank: Int) {
        val file = getplayerdata(player)
        val config = file?.let { YamlConfiguration.loadConfiguration(it) }
        config?.set("rank", rank)
        try {
            config?.save(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getxp(player: Player): Int {
        val file = getplayerdata(player)
        val config = file?.let { YamlConfiguration.loadConfiguration(it) }

        return Integer.parseInt(config?.get("xp").toString())
    }

    private fun setxp(player: Player, xp: Int) {
        val file = getplayerdata(player)
        val config = file?.let { YamlConfiguration.loadConfiguration(it) }
        config?.set("xp", xp)
        try {
            config?.save(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        checkrankup(player, getrank(player), getxp(player))
    }
    private fun addxp(player: Player, addxp: Int) {
        val file = getplayerdata(player)
        val config = file?.let { YamlConfiguration.loadConfiguration(it) }
        config?.set("xp", getxp(player)+addxp)
        try {
            config?.save(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        checkrankup(player, getrank(player), getxp(player))
    }

    private fun getkills(player: Player): Int {
        val file = getplayerdata(player)
        val config = file?.let { YamlConfiguration.loadConfiguration(it) }

        return Integer.parseInt(config?.get("kills").toString())
    }

    private fun setkills(player: Player, rank: Int) {
        val file = getplayerdata(player)
        val config = file?.let { YamlConfiguration.loadConfiguration(it) }
        config?.set("kills", rank)
        try {
            config?.save(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun checkrankup(player: Player, rank: Int, xp: Int) {
        when (rank) {
            1 -> {
                if (xp >= 20) {
                    setrank(player, getrank(player) + 1)
                    setxp(player, getxp(player) - 20)
                    player.sendMessage(
                        "${ChatColor.GOLD}${ChatColor.MAGIC} a ${ChatColor.RESET}${ChatColor.YELLOW}${ChatColor.UNDERLINE}★${
                            getrank(
                                player
                            )
                        }${ChatColor.RESET}${ChatColor.YELLOW}にレベルアップしました。${ChatColor.GOLD}${ChatColor.MAGIC} a"
                    )
                    player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)
                    player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)
                    changedisplayname(player)
                    return
                }
            }

            2 -> {
                if (xp >= 30) {
                    setrank(player, getrank(player) + 1)
                    setxp(player, getxp(player) - 30)
                    player.sendMessage(
                        "${ChatColor.GOLD}${ChatColor.MAGIC} a ${ChatColor.RESET}${ChatColor.YELLOW}${ChatColor.UNDERLINE}★${
                            getrank(
                                player
                            )
                        }${ChatColor.RESET}${ChatColor.YELLOW}にレベルアップしました。${ChatColor.GOLD}${ChatColor.MAGIC} a"
                    )
                    player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)
                    changedisplayname(player)
                    return
                }
            }

            3 -> {
                if (xp >= 40) {
                    setrank(player, getrank(player) + 1)
                    setxp(player, getxp(player) - 40)
                    player.sendMessage(
                        "${ChatColor.GOLD}${ChatColor.MAGIC} a ${ChatColor.RESET}${ChatColor.YELLOW}${ChatColor.UNDERLINE}★${
                            getrank(
                                player
                            )
                        }${ChatColor.RESET}${ChatColor.YELLOW}にレベルアップしました。${ChatColor.GOLD}${ChatColor.MAGIC} a"
                    )
                    player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)
                    changedisplayname(player)
                    return
                }
            }

            in 4..9 -> {
                if (xp >= 90) {
                    setrank(player, getrank(player) + 1)
                    setxp(player, getxp(player) - 50)
                    player.sendMessage(
                        "${ChatColor.GOLD}${ChatColor.MAGIC} a ${ChatColor.RESET}${ChatColor.YELLOW}${ChatColor.UNDERLINE}★${
                            getrank(
                                player
                            )
                        }${ChatColor.RESET}${ChatColor.YELLOW}にレベルアップしました。${ChatColor.GOLD}${ChatColor.MAGIC} a"
                    )
                    player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)
                    changedisplayname(player)
                    return
                }
            }

            in 10..14 -> {
                if (xp >= 150) {
                    setrank(player, getrank(player) + 1)
                    setxp(player, getxp(player) - 100)
                    player.sendMessage(
                        "${ChatColor.GOLD}${ChatColor.MAGIC} a ${ChatColor.RESET}${ChatColor.YELLOW}${ChatColor.UNDERLINE}★${
                            getrank(
                                player
                            )
                        }${ChatColor.RESET}${ChatColor.YELLOW}にレベルアップしました。${ChatColor.GOLD}${ChatColor.MAGIC} a"
                    )
                    player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)
                    changedisplayname(player)
                    return
                }
            }

            in 15..19 -> {
                if (xp >= 170) {
                    setrank(player, getrank(player) + 1)
                    setxp(player, getxp(player) - 170)
                    player.sendMessage(
                        "${ChatColor.GOLD}${ChatColor.MAGIC} a ${ChatColor.RESET}${ChatColor.YELLOW}${ChatColor.UNDERLINE}★${
                            getrank(
                                player
                            )
                        }${ChatColor.RESET}${ChatColor.YELLOW}にレベルアップしました。${ChatColor.GOLD}${ChatColor.MAGIC} a"
                    )
                    player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)
                    changedisplayname(player)
                    return
                }
            }

            in 20..30 -> {
                if (xp >= 250) {
                    setrank(player, getrank(player) + 1)
                    setxp(player, getxp(player) - 250)
                    player.sendMessage(
                        "${ChatColor.GOLD}${ChatColor.MAGIC} a ${ChatColor.RESET}${ChatColor.YELLOW}${ChatColor.UNDERLINE}★${
                            getrank(
                                player
                            )
                        }${ChatColor.RESET}${ChatColor.YELLOW}にレベルアップしました。${ChatColor.GOLD}${ChatColor.MAGIC} a"
                    )
                    player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)
                    changedisplayname(player)
                    return
                }
            }

            in 31..39 -> {
                if (xp >= 400) {
                    setrank(player, getrank(player) + 1)
                    setxp(player, getxp(player) - 400)
                    player.sendMessage(
                        "${ChatColor.GOLD}${ChatColor.MAGIC} a ${ChatColor.RESET}${ChatColor.YELLOW}${ChatColor.UNDERLINE}★${
                            getrank(
                                player
                            )
                        }${ChatColor.RESET}${ChatColor.YELLOW}にレベルアップしました。${ChatColor.GOLD}${ChatColor.MAGIC} a"
                    )
                    player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)
                    changedisplayname(player)
                    return
                }
            }

            in 40..50 -> {
                if (xp >= 500) {
                    setrank(player, getrank(player) + 1)
                    setxp(player, getxp(player) - 500)
                    player.sendMessage(
                        "${ChatColor.GOLD}${ChatColor.MAGIC} a ${ChatColor.RESET}${ChatColor.YELLOW}${ChatColor.UNDERLINE}★${
                            getrank(
                                player
                            )
                        }${ChatColor.RESET}${ChatColor.YELLOW}にレベルアップしました。${ChatColor.GOLD}${ChatColor.MAGIC} a"
                    )
                    player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)
                    changedisplayname(player)
                    return
                }
            }

            in 51..99999 -> {
                if (xp >= 750) {
                    setrank(player, getrank(player) + 1)
                    setxp(player, getxp(player) - 750)
                    player.sendMessage(
                        "${ChatColor.GOLD}${ChatColor.MAGIC} a ${ChatColor.RESET}${ChatColor.YELLOW}${ChatColor.UNDERLINE}★${
                            getrank(
                                player
                            )
                        }${ChatColor.RESET}${ChatColor.YELLOW}にレベルアップしました。${ChatColor.GOLD}${ChatColor.MAGIC} a"
                    )
                    player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)
                    changedisplayname(player)
                    return
                }
            }
        }
    }

    private fun changedisplayname(player: Player) {
        if (player.scoreboard.getEntryTeam(player.name)?.name == "hunter") {
            val p = "${ChatColor.RED}${player.name}"
            when (getrank(player)){
                in 0..5->{
                    player.setDisplayName("${ChatColor.GRAY}[★${getrank(player)}] $p")
                    player.setPlayerListName("${ChatColor.GRAY}[★${getrank(player)}] $p")
                }
                in 6..10->{
                    player.setDisplayName("${ChatColor.BLUE}[★${getrank(player)}] $p")
                    player.setPlayerListName("${ChatColor.BLUE}[★${getrank(player)}] $p")
                }
                in 11..20->{
                    player.setDisplayName("${ChatColor.DARK_GREEN}[★${getrank(player)}] $p")
                    player.setPlayerListName("${ChatColor.DARK_GREEN}[★${getrank(player)}] $p")
                }
                in 21..30->{
                    player.setDisplayName("${ChatColor.AQUA}[★${getrank(player)}] $p")
                    player.setPlayerListName("${ChatColor.AQUA}[★${getrank(player)}] $p")
                }
                in 31..40->{
                    player.setDisplayName("${ChatColor.RED}[★${getrank(player)}] $p")
                    player.setPlayerListName("${ChatColor.RED}[★${getrank(player)}] $p")
                }
                in 41..50->{
                    player.setDisplayName("${ChatColor.YELLOW}[★${getrank(player)}] $p")
                    player.setPlayerListName("${ChatColor.YELLOW}[★${getrank(player)}] $p")
                }
                in 51..99999->{
                    player.setDisplayName("${ChatColor.GOLD}[★${getrank(player)}] $p")
                    player.setPlayerListName("${ChatColor.GOLD}[★${getrank(player)}] $p")
                }
            }

        }
        else if (player.scoreboard.getEntryTeam(player.name)?.name == "survivor") {
            val p = "${ChatColor.GREEN}${player.name}"
            when (getrank(player)){
                in 0..5->{
                    player.setDisplayName("${ChatColor.GRAY}[★${getrank(player)}] $p")
                    player.setPlayerListName("${ChatColor.GRAY}[★${getrank(player)}] $p")
                }
                in 6..10->{
                    player.setDisplayName("${ChatColor.BLUE}[★${getrank(player)}] $p")
                    player.setPlayerListName("${ChatColor.BLUE}[★${getrank(player)}] $p")
                }
                in 11..20->{
                    player.setDisplayName("${ChatColor.AQUA}[★${getrank(player)}] $p")
                    player.setPlayerListName("${ChatColor.AQUA}[★${getrank(player)}] $p")
                }
                in 21..30->{
                    player.setDisplayName("${ChatColor.RED}[★${getrank(player)}] $p")
                    player.setPlayerListName("${ChatColor.RED}[★${getrank(player)}] $p")
                }
                in 31..40->{
                    player.setDisplayName("${ChatColor.YELLOW}[★${getrank(player)}] $p")
                    player.setPlayerListName("${ChatColor.YELLOW}[★${getrank(player)}] $p")
                }
                in 41..50->{
                    player.setDisplayName("${ChatColor.GOLD}[★${getrank(player)}] $p")
                    player.setPlayerListName("${ChatColor.GOLD}[★${getrank(player)}] $p")
                }
            }

        }else{
            val p = "${ChatColor.WHITE}${player.name}"
            when (getrank(player)){
                in 0..5->{
                    player.setDisplayName("${ChatColor.GRAY}[★${getrank(player)}] $p")
                    player.setPlayerListName("${ChatColor.GRAY}[★${getrank(player)}] $p")
                }
                in 6..10->{
                    player.setDisplayName("${ChatColor.BLUE}[★${getrank(player)}] $p")
                    player.setPlayerListName("${ChatColor.BLUE}[★${getrank(player)}] $p")
                }
                in 11..20->{
                    player.setDisplayName("${ChatColor.AQUA}[★${getrank(player)}] $p")
                    player.setPlayerListName("${ChatColor.AQUA}[★${getrank(player)}] $p")
                }
                in 21..30->{
                    player.setDisplayName("${ChatColor.RED}[★${getrank(player)}] $p")
                    player.setPlayerListName("${ChatColor.RED}[★${getrank(player)}] $p")
                }
                in 31..40->{
                    player.setDisplayName("${ChatColor.YELLOW}[★${getrank(player)}] $p")
                    player.setPlayerListName("${ChatColor.YELLOW}[★${getrank(player)}] $p")
                }
                in 41..50->{
                    player.setDisplayName("${ChatColor.GOLD}[★${getrank(player)}] $p")
                    player.setPlayerListName("${ChatColor.GOLD}[★${getrank(player)}] $p")
                }
            }
        }
        
    }

    private fun getrankupremaining(rank: Int, xp: Int): Int {
        when (rank) {
            1 -> {
                return Integer.parseInt((20 - xp).toString())
            }

            2 -> {
                return Integer.parseInt((30 - xp).toString())
            }

            3 -> {
                return Integer.parseInt((40 - xp).toString())
            }

            in 4..9 -> {
                return Integer.parseInt((50 - xp).toString())
            }

            in 10..14 -> {
                return Integer.parseInt((100 - xp).toString())
            }

            in 15..19 -> {
                return Integer.parseInt((170 - xp).toString())
            }

            in 20..30 -> {
                return Integer.parseInt((250 - xp).toString())
            }

            in 31..39 -> {
                return Integer.parseInt((400 - xp).toString())
            }

            in 40..50 -> {
                return Integer.parseInt((500 - xp).toString())

            }

            in 51..99999 -> {
                return Integer.parseInt((750 - xp).toString())

            }
            else->{
                return Integer.parseInt("?")
            }
        }
    }



    private fun resetcoins(player:Player){
        val file = getplayerdata(player)
        val config = file?.let { YamlConfiguration.loadConfiguration(it) }
        config?.set("coins",0)
        try {
            config?.save(file)
        }catch (e:IOException){
            e.printStackTrace()
        }

    }
}