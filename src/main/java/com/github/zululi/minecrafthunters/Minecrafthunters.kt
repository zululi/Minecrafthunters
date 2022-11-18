package com.github.zululi.minecrafthunters

import com.github.zululi.minecrafthunters.Minecrafthunters.Main.board
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.gamestart
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.hunter
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.startsec
import com.github.zululi.minecrafthunters.Minecrafthunters.Main.survivor
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.*
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.DisplaySlot
import java.util.*
import kotlin.collections.ArrayList


class Minecrafthunters : JavaPlugin() , Listener, CommandExecutor {
    object Main {
        var gamestart = 0
        var startsec = -999
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
        if (player.scoreboard.getEntryTeam(player.name)?.name == "hunter"){

        }else if (player.scoreboard.getEntryTeam(player.name)?.name == "survivor"){
            hunter?.addEntry(player.name)
            for (hunter in board?.getTeam("survivor")!!.entries) {

            }
        }




        object : BukkitRunnable() {
            override fun run() {
                e.entity.spigot().respawn()
            }
        }.runTaskLater(this, 1L)
    }

    @EventHandler
    fun playermoveevent(e: PlayerMoveEvent) {
        for (player in Bukkit.getOnlinePlayers()) {
            val team2 = board?.getTeam("survivor")?.entries
            for (survivor in team2.toString()) {
            }

        }
    }

    @EventHandler
    fun chatevent(e: AsyncPlayerChatEvent) {
        val player = e.player
        if (player.scoreboard.getEntryTeam(player.name)?.name == "hunter"){

            for (hunter in board?.getTeam("hunter")!!.entries) {
                Bukkit.getPlayer(hunter)?.sendMessage("${ChatColor.RED}${player.name}${ChatColor.GREEN} : ${ChatColor.RESET}${e.message}")
                e.isCancelled = true
            }
        }else if (player.scoreboard.getEntryTeam(player.name)?.name == "survivor"){

            for (hunter in board?.getTeam("survivor")!!.entries) {
                Bukkit.getPlayer(hunter)?.sendMessage("${ChatColor.GREEN}${player.name}${ChatColor.GREEN} : ${ChatColor.RESET}${e.message}")
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
                if (player.scoreboard.getEntryTeam(player.name)?.name == "hunter"){
                    for (survivor2 in board?.getTeam("survivor")!!.entries) {
                        playerlist.add(survivor2.toString())
                    }
                }
            }
            playerlist.shuffle();
            val chengeplayer = playerlist[1]
            if (chengeplayer != null) {
                player.sendMessage("change target to $chengeplayer")
                player.compassTarget = Bukkit.getPlayer(chengeplayer)?.location!!
            }else {
                player.sendMessage("...")
            }


        }

    }









    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when(command.name){
            "start"->{
                if (sender.isOp) {
                    startsec = 10
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
                                Bukkit.broadcastMessage("${ChatColor.YELLOW}試合開始まで${ChatColor.GOLD}$startsec 秒")
                            }else if (startsec in 0..5){
                                Bukkit.broadcastMessage("${ChatColor.YELLOW}試合開始まで${ChatColor.RED}$startsec 秒")
                            }
                            startsec -= 1
                            for (player in Bukkit.getOnlinePlayers()){
                                player.playSound(player.location,Sound.UI_BUTTON_CLICK,1.0F,1.0F)
                            }
                        }else if (startsec==0){
                            gamestart = 1
                            Bukkit.broadcastMessage("${ChatColor.GOLD}試合開始!")
                            for (player in Bukkit.getOnlinePlayers()){
                                player.playSound(player.location,Sound.ENTITY_WITHER_SPAWN,1.0F,1.0F)
                            }
                        }


                    }
                    1->{
                        gamescoreboard()
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
                    }
                }
            }
        }.runTaskTimer(this, 0, 20)
    }

    private fun prescoreboard(){
        Bukkit.getScoreboardManager()?.mainScoreboard?.getObjective("pre")?.unregister()
        val prescore = Bukkit.getScoreboardManager()?.mainScoreboard?.registerNewObjective("pre","Dummy")
        prescore?.displayName = "${ChatColor.DARK_PURPLE}Minecraft hunters ${ChatColor.GRAY}0.0.2"
        prescore?.displaySlot = DisplaySlot.SIDEBAR
        prescore?.getScore("${ChatColor.GOLD}     サーバー人数: ${Bukkit.getOnlinePlayers().size}")?.score = 0
    }

    private fun gamescoreboard() {
        Bukkit.getScoreboardManager()?.mainScoreboard?.getObjective("game")?.unregister()
        val prescore = Bukkit.getScoreboardManager()?.mainScoreboard?.registerNewObjective("game","Dummy")
        prescore?.displayName = "${ChatColor.DARK_PURPLE}Minecraft hunters ${ChatColor.GRAY}0.0.2"
        prescore?.displaySlot = DisplaySlot.SIDEBAR
        prescore?.getScore("${ChatColor.GREEN}     残り人数: ${survivor?.size}")?.score = 1
        prescore?.getScore("${ChatColor.RED}     ハンター: ${hunter?.size}")?.score = 0
    }



}