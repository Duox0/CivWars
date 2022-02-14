package ru.civwars.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import ru.civwars.CivWars;
import ru.civwars.listeners.block.*;
import ru.lib27.annotation.NotNull;

public class BlockListener extends BasicListener {
    
    private static Listener instance;
    
    public static void init(@NotNull CivWars plugin) {
        if(instance == null) {
            instance = new BlockListener(plugin);
            plugin.getServer().getPluginManager().registerEvents(instance, plugin);
        }
    }
    
    public BlockListener(@NotNull CivWars plugin) {
        super(plugin);
        
        this.registerHandler(new BreakHandler(plugin));
        this.registerHandler(new BurnHandler(plugin));
         //org.bukkit.event.block.BlockCanBuildEvent;
        //org.bukkit.event.block.BlockDamageEvent;
        this.registerHandler(new DispenseHandler(plugin));
        //org.bukkit.event.block.BlockExpEvent;
        this.registerHandler(new ExplodeHandler(plugin));
        this.registerHandler(new FadeHandler(plugin));
        this.registerHandler(new FormHandler(plugin));
        this.registerHandler(new FromToHandler(plugin));
        this.registerHandler(new GrowHandler(plugin));
        //org.bukkit.event.block.BlockIgniteEvent;
        //org.bukkit.event.block.BlockMultiPlaceEvent;
        //org.bukkit.event.block.BlockPhysicsEvent;
        //org.bukkit.event.block.BlockPistonExtendEvent;
        //org.bukkit.event.block.BlockPistonRetractEvent;
        this.registerHandler(new PlaceHandler(plugin));
        //org.bukkit.event.block.BlockRedstoneEvent;
        this.registerHandler(new SpreadHandler(plugin));
        //org.bukkit.event.block.CauldronLevelChangeEvent;
        this.registerHandler(new SpreadHandler(plugin));
        this.registerHandler(new EntityBlockFormHandler(plugin));
        //org.bukkit.event.block.LeavesDecayEvent;
        //org.bukkit.event.block.NotePlayEvent;
        //org.bukkit.event.block.SignChangeEvent;
    }
    
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        this.handle0(event);
    }
    
    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        this.handle0(event);
    }
    
    @EventHandler
    public void onBlockCanBuild(BlockCanBuildEvent event) {
        this.handle0(event);
    }
    
    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        this.handle0(event);
    }
    
    @EventHandler
    public void onBlockDispense(BlockDispenseEvent event) {
        this.handle0(event);
    }
    
    @EventHandler
    public void onBlockExp(BlockExpEvent event) {
        this.handle0(event);
    }
    
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        this.handle0(event);
    }
    
    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        this.handle0(event);
    }
    
    @EventHandler
    public void onBlockForm(BlockFormEvent event) {
        this.handle0(event);
    }
    
    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        this.handle0(event);
    }
    
    @EventHandler
    public void onBlockGrow(BlockGrowEvent event) {
        this.handle0(event);
    }
    
    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        this.handle0(event);
    }
    
    @EventHandler
    public void onBlockMultiPlace(BlockMultiPlaceEvent event) {
        this.handle0(event);
    }
    
    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        this.handle0(event);
    }
    
    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        this.handle0(event);
    }
    
    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        this.handle0(event);
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        this.handle0(event);
    }
    
    @EventHandler
    public void onBlockRedstone(BlockRedstoneEvent event) {
        this.handle0(event);
    }
    
    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        this.handle0(event);
    }
    
    @EventHandler
    public void onCauldronLevelChange(CauldronLevelChangeEvent event) {
        this.handle0(event);
    }
    
    @EventHandler
    public void onEntityBlockForm(EntityBlockFormEvent event) {
        this.handle0(event);
    }
    
    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        this.handle0(event);
    }
    
    @EventHandler
    public void onNotePlay(NotePlayEvent event) {
        this.handle0(event);
    }
    
    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        this.handle0(event);
    }
    
}
