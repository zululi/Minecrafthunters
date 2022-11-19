package com.github.zululi.minecrafthunters
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.board
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.gamestart
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.hunter
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.startsec
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.survivor
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.min
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.sec
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.survivorclear
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.EnderDragon
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.player.*
import org.bukkit.generator.structure.StructureType
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
        var sec = 0
        var board = Bukkit.getScoreboardManager()?.mainScoreboard
        var hunter = board?.registerNewTeam("hunter")
        var survivor = board?.registerNewTeam("survivor")

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
        hunter?.prefix = "${ChatColor.RED}[H] "
        survivor?.prefix = "${ChatColor.GREEN}[S] "
        Bukkit.getWorld("world")?.setGameRule(GameRule.SPAWN_RADIUS, 0)
        object : BukkitRunnable() {
            override fun run() {
                for (x1 in -10..10) {
                    val z1 = 10
                    val x2 = 10
                    for (z2 in -10..10) {
                        val x3 = -10
                        val z4 = -10
                        var block = Bukkit.getWorld("world")?.getBlockAt(x1, 150, z1)
                        var block2 = Bukkit.getWorld("world")?.getBlockAt(x2, 150, z2)
                        var block3 = Bukkit.getWorld("world")?.getBlockAt(x3, 150, z2)
                        var block4 = Bukkit.getWorld("world")?.getBlockAt(x1, 150, z4)
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
        Bukkit.getWorld("world")?.worldBorder?.setCenter(0.0 , 0.0)
        Bukkit.getWorld("world")?.worldBorder?.size = 20.0
        Bukkit.getWorld("world")?.setSpawnLocation(0, 200, 0)

    }
    override fun onDisable() {
        // Plugin shutdown logic
        hunter?.unregister()
        survivor?.unregister()
    }
    @EventHandler
    fun onjoinplayer(e: PlayerJoinEvent) {
        val player = e.player
        e.joinMessage = "${ChatColor.YELLOW}${player.name} joined."
        if(gamestart == 1 && player.scoreboard.getEntryTeam(player.name) == null){
            hunter?.addEntry(player.name)
        }
    }
    @EventHandler
    fun killeddragon(e:EntityDeathEvent) {
        val entity = e.entity
        if(entity is EnderDragon){
            survivorclear = true
        }

    }
    @EventHandler
    fun item(e: PlayerDropItemEvent) {
        val player = e.player
        val item = e.itemDrop
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
    }
    @EventHandler
    fun damage(e: EntityDamageEvent) {
        val player = e.entity
        if (player is Player) {
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
    fun breakblock(e: BlockBreakEvent) {
        when (gamestart) {
            0 -> {
                e.isCancelled = true
            }

            1 -> {
                e.isCancelled = false
                val player = e.player
                if (player.scoreboard.getEntryTeam(player.name)?.name == "hunter"&&min>42){
                    e.isCancelled = true
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
    fun itempickup(e: PlayerPickupItemEvent) {
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
    fun playerdeathed(e: PlayerDeathEvent) {
        val player = e.entity
        val death = e.deathMessage
        if(player.world.name == "world_the_end" && player.scoreboard.getEntryTeam(player.name)?.name == "survivor" && death?.contains("place") == true){
            player.sendMessage("${ChatColor.GREEN}エンドでの落下死のためリスポーンしました。")
        } else{
            hunter?.addEntry(player.name)
            player.sendMessage("${ChatColor.RED}死亡したため、ハンターになりました。")
        }
        object : BukkitRunnable() {
            override fun run() {
                e.entity.spigot().respawn()
                player.inventory.setItem(8, ItemStack(Material.COMPASS))
            }
        }.runTaskLater(this, 1L)
    }
    @EventHandler
    fun chatevent(e: AsyncPlayerChatEvent) {
        val player = e.player
        if (player.scoreboard.getEntryTeam(player.name)?.name == "hunter"){
            for (hunter in board?.getTeam("hunter")!!.entries) {
                Bukkit.getPlayer(hunter)?.sendMessage("${ChatColor.RED}[Teamchat]${player.name}${ChatColor.YELLOW} : ${ChatColor.RESET}${e.message}")
                e.isCancelled = true
            }
        }else if (player.scoreboard.getEntryTeam(player.name)?.name == "survivor"){
            for (hunter in board?.getTeam("survivor")!!.entries) {
                Bukkit.getPlayer(hunter)?.sendMessage("${ChatColor.GREEN}[Teamchat]${player.name}${ChatColor.YELLOW} : ${ChatColor.RESET}${e.message}")
                e.isCancelled = true
            }
        }else{
            for (hunter in Bukkit.getOnlinePlayers()) {
                Bukkit.getPlayer(hunter.name)?.sendMessage("${player.name}${ChatColor.YELLOW} : ${ChatColor.RESET}${e.message}")
                e.isCancelled = true
            }
        }
    }
    @EventHandler
    fun clickitem(e:PlayerInteractEvent){
        val playerlist = ArrayList<String>()
        val player = e.player
        val item = e.item?.type
        val click = e.action
        if (item == Material.COMPASS && click == Action.RIGHT_CLICK_AIR) {
            for (all in Bukkit.getOnlinePlayers()) {
                if (player.scoreboard.getEntryTeam(player.name)?.name == "hunter") {
                    for (survivor2 in board?.getTeam("survivor")!!.entries) {
                        playerlist.add(survivor2.toString())

                    }
                }
            }
            if (player.scoreboard.getEntryTeam(player.name)?.name == "survivor") {
                player.playSound(player.location,Sound.UI_BUTTON_CLICK,1f,1.0f)
                if (e.player.world.name == "world") {
                    val location = player.world.locateNearestStructure(player.location, StructureType.STRONGHOLD, 32, false)
                    if(location == null) {
                        player.sendMessage("${ChatColor.RED}要塞が見つかりませんでした(´・ω・`)")
                    } else {
                        val x = location.location.blockX
                        val z = location.location.blockZ
                        val playerx = player.location.blockX
                        val playerz = player.location.blockZ
                        val diffarencex  = x - playerx
                        val diffarencez  = z - playerz
                        val distance = diffarencex * diffarencex + diffarencez * diffarencez
                        val result = sqrt(distance.toDouble())
                        player.compassTarget = location.location
                        player.sendMessage("${ChatColor.YELLOW}要塞: " + location.location.blockX + " ~ " +location.location.blockZ+"  (残り${result.toInt()}ブロック)")
                    }
                }else if (e.player.world.name == "world_nether") {
                    player.playSound(player.location,Sound.UI_BUTTON_CLICK,1f,1.0f)
                    val location = player.world.locateNearestStructure(player.location, StructureType.FORTRESS, 32, false)
                    if(location == null) {
                        player.sendMessage("${ChatColor.RED}ネザー要塞が見つかりませんでした(´・ω・`)")
                    } else {
                        val x = location.location.blockX
                        val z = location.location.blockZ
                        val playerx = player.location.blockX
                        val playerz = player.location.blockZ
                        val diffarencex  = x - playerx
                        val diffarencez  = z - playerz
                        val distance = diffarencex * diffarencex + diffarencez * diffarencez
                        val result = sqrt(distance.toDouble())
                        player.compassTarget = location.location
                        player.sendMessage("${ChatColor.YELLOW}ネザー要塞: " + location.location.blockX + " ~ " +location.location.blockZ+"  (残り${result.toInt()}ブロック)")
                    }
                }
            }

            playerlist.shuffle()
            val chengeplayer = playerlist[1]
//            val x = Bukkit.getPlayer(chengeplayer)?.location?.blockX
//            val z = Bukkit.getPlayer(chengeplayer)?.location?.blockZ
//            val playerx = player.location.blockX
//            val playerz = player.location.blockZ
//            val diffarencex  = x?.minus(playerx)
//            val diffarencez  = z?.minus(playerz)
//            val distance = (diffarencex?.times(diffarencex) ?: 1) + (diffarencez?.times(diffarencez) ?: 1)
//            val result = sqrt(distance.toDouble())
            when (Bukkit.getPlayer(chengeplayer)?.world?.name) {
                "world" -> {
                    val plworld2 = "${ChatColor.GREEN}overworld"
                    player.sendMessage("${ChatColor.YELLOW}${chengeplayer}の現在位置にターゲットしました。\n${ChatColor.GRAY}($plworld2${ChatColor.GRAY})")
                }
                "world_nether" -> {
                    val plworld2 = "${ChatColor.RED}nether"
                    player.sendMessage("${ChatColor.YELLOW}${chengeplayer}の現在位置にターゲットしました。\n${ChatColor.GRAY}($plworld2${ChatColor.GRAY})")
                    val compass = player.inventory.getItem(8)?.itemMeta as CompassMeta
                    compass.lodestone = Bukkit.getPlayer(chengeplayer)?.location

                }
                "the_end"-> {
                    val plworld2 = "${ChatColor.DARK_PURPLE}end"
                    player.sendMessage("${ChatColor.YELLOW}${chengeplayer}の現在位置にターゲットしました。\n${ChatColor.GRAY}($plworld2${ChatColor.GRAY})")
                }
            }
            player.compassTarget = Bukkit.getPlayer(chengeplayer)?.location!!
            player.playSound(player.location,Sound.UI_BUTTON_CLICK,1f,1.0f)
        }
    }
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when(command.name){
            "start"->{
                if (sender.isOp) {
                    if (Bukkit.getOnlinePlayers().size > 2) {
                        startsec = 10
                        min = 45
                        autowarifuri()
                    }else{
                        sender.sendMessage("${ChatColor.RED}You must have at least 3 online players to start")
                    }

                }
            }
            "g"->{
                val player = sender.name
                val message = args[0]
                if (Bukkit.getPlayer(player)?.scoreboard?.getEntryTeam(player)?.name == "hunter"){

                    for (all in Bukkit.getOnlinePlayers()) {
                        Bukkit.getPlayer(all.name)?.sendMessage("${ChatColor.DARK_PURPLE}[Global] ${ChatColor.RED}${player}${ChatColor.YELLOW} : ${ChatColor.RESET}${message}")
                    }
                }else if (Bukkit.getPlayer(player)?.scoreboard?.getEntryTeam(player)?.name == "survivor"){

                    for (all in Bukkit.getOnlinePlayers()) {
                        Bukkit.getPlayer(all.name)?.sendMessage("${ChatColor.DARK_PURPLE}[Global] ${ChatColor.GREEN}${player}${ChatColor.YELLOW} : ${ChatColor.RESET}${message}")
                    }
                }else{
                    for (all in Bukkit.getOnlinePlayers()) {
                        Bukkit.getPlayer(all.name)?.sendMessage("${ChatColor.DARK_PURPLE}[Global] ${ChatColor.RESET}${player}${ChatColor.YELLOW} : ${ChatColor.RESET}${message}")
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
                            player.health = 20.0
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
                            for (player in board?.getTeam("survivor")!!.entries) {
                                val all = Bukkit.getPlayer(player)
                                val woodensword = ItemStack(Material.WOODEN_SWORD)
                                val metadatasword = woodensword.itemMeta
                                metadatasword?.isUnbreakable = true
                                val l0: MutableList<String> = ArrayList()
                                l0.add("${ChatColor.GOLD}SoulBound")
                                metadatasword?.lore = (l0)
                                woodensword.itemMeta = metadatasword
                                all?.inventory?.setItem(0, woodensword)

                                val woodenpickaxe = ItemStack(Material.WOODEN_PICKAXE)
                                val metadatapickaxe = woodenpickaxe.itemMeta
                                metadatapickaxe?.isUnbreakable = true
                                val l1: MutableList<String> = ArrayList()
                                l1.add("${ChatColor.GOLD}SoulBound")
                                metadatapickaxe?.lore = (l1)
                                woodenpickaxe.itemMeta = metadatapickaxe
                                all?.inventory?.setItem(1, woodenpickaxe)

                                val woodenaxe = ItemStack(Material.WOODEN_AXE)
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


                                val woodenshovel = ItemStack(Material.WOODEN_SHOVEL)
                                val metadatashovel = woodenshovel.itemMeta
                                metadatashovel?.isUnbreakable = true
                                val l3: MutableList<String> = ArrayList()
                                l3.add("${ChatColor.GOLD}SoulBound")
                                metadatashovel?.lore = (l3)
                                woodenshovel.itemMeta = metadatashovel
                                all?.inventory?.setItem(3, woodenshovel)

                                all?.inventory?.setItem(4, ItemStack(Material.BREAD, 10))
                                all?.sendMessage("")
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
                        gamescoreboard()
                        if (min>0&&sec>0) {
                            sec--
                        }else if (min>0&&sec==0) {
                            sec = 59
                            min--
                        }
                        if (min == 42&&sec == 59){
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
                        if (min>35){
                            for (player in board?.getTeam("survivor")!!.entries){
                                Bukkit.getPlayer(player)?.saturation = 20f
                                Bukkit.getPlayer(player)?.foodLevel = 20
                            }
                        }else if (min==34&&sec==59){
                            for (player in Bukkit.getOnlinePlayers()){
                                player.sendMessage("${ChatColor.GOLD}----------------------------------")
                                player.sendMessage("${ChatColor.GOLD}  満腹度が減るようになりました。")
                                player.sendMessage("${ChatColor.GOLD}----------------------------------")
                                player.playSound(player.location,Sound.ENTITY_WITHER_AMBIENT,1.0f,2.0f)
                            }
                        }

                    }
                }
            }
        }.runTaskTimer(this, 0, 20)
    }
    private fun tick(){
        object : BukkitRunnable() {
            override fun run() {
                when(gamestart){
                    0->{
                    }
                    1->{
                        if (survivor?.size == 0){
                            for (player in Bukkit.getOnlinePlayers()){
                                player.playSound(player.location,Sound.ENTITY_GENERIC_EXPLODE,1f,1.0f)
                                player.playSound(player.location,Sound.ENTITY_FIREWORK_ROCKET_LAUNCH,1f,1.0f)
                                player.sendMessage("${ChatColor.GOLD}--------------------------------\n試合終了!\n${ChatColor.YELLOW}生存者がいなくなったため、ハンターの勝利です。${ChatColor.GOLD}--------------------------------\n")
                                cancel()
                                gamestart =2
                            }
                        }else if (min == 0 && sec == 0){
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
        }.runTaskTimer(this, 0, 20)
    }
    private fun prescoreboard(){
        Bukkit.getScoreboardManager()?.mainScoreboard?.getObjective("pre")?.unregister()
        val prescore = Bukkit.getScoreboardManager()?.mainScoreboard?.registerNewObjective("pre","Dummy" , "aaa")
        prescore?.displayName = "${ChatColor.DARK_PURPLE}${ChatColor.BOLD}${ChatColor.ITALIC}Manhunt ${ChatColor.GRAY}1.1.0"
        prescore?.displaySlot = DisplaySlot.SIDEBAR
        prescore?.getScore("${ChatColor.GOLD}サーバー人数: ${Bukkit.getOnlinePlayers().size}")?.score = 0
    }
    private fun gamescoreboard() {
        Bukkit.getScoreboardManager()?.mainScoreboard?.getObjective("game")?.unregister()
        val prescore = Bukkit.getScoreboardManager()?.mainScoreboard?.registerNewObjective("game","Dummy" , "aaa")
        prescore?.displayName = "${ChatColor.DARK_PURPLE}${ChatColor.BOLD}${ChatColor.ITALIC}Manhunt ${ChatColor.GRAY}1.1.0"
        prescore?.displaySlot = DisplaySlot.SIDEBAR
        prescore?.getScore("${ChatColor.GREEN}残り人数: ${survivor?.size}")?.score = 1
        prescore?.getScore("${ChatColor.RED}ハンター: ${hunter?.size}")?.score = 0
        if (min>0&&sec in 10 .. 59 ){
             prescore?.getScore("${ChatColor.GOLD}残り時間: $min : $sec")?.score = 3
        }else if (min>0&&sec in 0..9){
             prescore?.getScore("${ChatColor.GOLD}残り時間: $min : 0$sec")?.score = 3
        }
        if (min>42&&sec in 10..59){
            prescore?.getScore("${ChatColor.GOLD}ハンター解放まで: ${min-42} : $sec")?.score = 2
        }else if (min>42&&sec in 0..9){
            prescore?.getScore("${ChatColor.GOLD}ハンター解放まで: ${min-42} : 0$sec")?.score = 2
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
    fun Clickevent(e: InventoryMoveItemEvent){
        val initiator = e.initiator
        if(initiator == ItemStack(Material.COMPASS)){
            e.isCancelled = true
        }
    }
}