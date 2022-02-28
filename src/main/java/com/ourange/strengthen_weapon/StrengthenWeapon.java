package com.ourange.strengthen_weapon;

import com.ourange.strengthen_weapon.domain.enumeration.WeaponsIndex;
import com.ourange.strengthen_weapon.handler.CommandHandler;
import com.ourange.strengthen_weapon.listener.StrengthenMenuListener;
import com.ourange.strengthen_weapon.listener.SwBowListener;
import com.ourange.strengthen_weapon.utils.ConfigFactory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ourange
 */
public class StrengthenWeapon extends JavaPlugin {
    private static final String DEFAULT_COMMAND = "sw";
    private ConsoleCommandSender sender;
    private ConfigFactory factory;
    private CommandHandler handler;
    private SwBowListener swBowListener;
    private StrengthenMenuListener strengthenMenuListener;

    @Override
    public void onLoad() {
        this.saveDefaultConfig();
        sender = getServer().getConsoleSender();
        //MenuUtils.authorMenu(this);
    }

    @Override
    public void onEnable() {
        factory = new ConfigFactory(this);
        //初始化并绑定handler
        handler = new CommandHandler(this, factory);
        Objects.requireNonNull(Bukkit.getPluginCommand(DEFAULT_COMMAND)).setExecutor(handler);
        handler.setFactory(factory);
        //设置tab联想
        Objects.requireNonNull(Bukkit.getPluginCommand(DEFAULT_COMMAND)).setTabCompleter(this);
        //初始化并绑定监听器
        swBowListener = new SwBowListener();
        swBowListener.setStrengthenBow(factory.getStrengthenWeapons().get(WeaponsIndex.BOW.ordinal()));
        getServer().getPluginManager().registerEvents(swBowListener, this);
        strengthenMenuListener = new StrengthenMenuListener();
        strengthenMenuListener.setPlugin(this);
        strengthenMenuListener.setStrengthenWeapons(factory.getStrengthenWeapons());
        strengthenMenuListener.setStrengthenStones(factory.getStrengthenStones());
        strengthenMenuListener.setService(handler.getStrengthService());
        getServer().getPluginManager().registerEvents(strengthenMenuListener, this);
        /*damageListener = new OnDamageListener();
        damageListener.setPlugin(this);
        damageListener.setDamageExtra(factory.getStrengthExtra().getDamageExtra());
        getServer().getPluginManager().registerEvents(damageListener,this);*/
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        consoleMsg("§c§l正在重新读取§a[§bconfig.yml§a]§c§l文件...");
        factory.initFile();
        factory.initItems();
        handler.reloadHandlerMethod(factory.getStrengthenWeapons(), factory.getStrengthenStones());
        swBowListener.setStrengthenBow(factory.getStrengthenWeapons().get(WeaponsIndex.BOW.ordinal()));
        strengthenMenuListener.setStrengthenWeapons(factory.getStrengthenWeapons());
        /*damageListener.setDamageExtra(factory.getStrengthExtra().getDamageExtra());*/
    }

    /**
     * 子命令联想
     */
    private final String[] subUserCommands = {"sw_bow", "sw_stone"};

    @Override
    public @Nullable
    List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length > 1) {
            return new ArrayList<>();
        }
        if (args.length == 0) {
            return Arrays.asList(subUserCommands);
        }
        return Arrays.stream(subUserCommands).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
    }

    /**
     * 发送控制台信息
     *
     * @param msg 消息字符串
     */
    public void consoleMsg(String msg) {
        sender.sendMessage("§a[strengthWeapon] " + msg);
    }
}
