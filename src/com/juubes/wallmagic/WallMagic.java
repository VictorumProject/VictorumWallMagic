package com.juubes.wallmagic;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.victorum.victorum.Victorum;

public final class WallMagic extends JavaPlugin implements Runnable {

    private final Victorum victorum;

    public WallMagic() {
	this.victorum = (Victorum) Bukkit.getPluginManager().getPlugin("Victorum");
    }

    @Override
    public void onEnable() {
	if (!Bukkit.getPluginManager().isPluginEnabled("Victorum")) {
	    this.setEnabled(false);
	    this.getLogger().warning("Can't enable plugin. Dependency Victorum not found.");
	    return;
	}

	saveDefaultConfig();
	final int SPEED = getConfig().getInt("SPEEEEEEED");
	Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(this), this);
	Bukkit.getScheduler().runTaskTimer(this, this, 0, SPEED);

	getCommand("wallmagic").setExecutor((CommandSender sender, Command cmd, String lbl, String[] args) -> {
	    if (!sender.isOp()) {
		sender.sendMessage("§cEi permejä");
		return true;
	    }
	    if (!(sender instanceof Player))
		return true;

	    Player p = (Player) sender;
	    if (args.length == 0) {
		WallMagicAPI.giveGeneratorItem(p, Material.OBSIDIAN, 64, (byte) 0);
	    } else if (args.length == 1) {
		try {
		    byte b = Byte.parseByte(args[0]);
		    WallMagicAPI.giveGeneratorItem(p, Material.STAINED_CLAY, 64, b);
		} catch (Exception e) {
		    sender.sendMessage("§cNyt kyl kusi joku... koita numeroa.");
		}
	    } else {
		sender.sendMessage("§c/wallmagic <data>");
	    }
	    return true;
	});
    }

    @Override
    public void onDisable() {

    }

    private int tick = 0;

    @Override
    public void run() {
	tick++;
	Set<GeneratorBlock> blocksDead = new HashSet<>(3);
	for (GeneratorBlock block : GENERATORS) {
	    boolean alive = block.generate(tick);
	    if (!alive)
		blocksDead.add(block);
	}
	for (GeneratorBlock generatorBlock : blocksDead) {
	    GENERATORS.remove(generatorBlock);
	}
    }

    private final static Set<GeneratorBlock> GENERATORS = new HashSet<>();

    public void createGenerator(GeneratorBlock generator) {
	GENERATORS.add(generator);
    }

    public Victorum getVictorum() {
	return victorum;
    }
}
