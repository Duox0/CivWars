package ru.civwars.bukkit;

import java.util.Set;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import ru.lib27.annotation.NotNull;

public class BukkitBlockCommandSender implements AbstractCommandSender, BlockCommandSender {
    
    private final BlockCommandSender sender;

    public BukkitBlockCommandSender(@NotNull BlockCommandSender sender) {
        this.sender = sender;
    }

    @NotNull
    public BlockCommandSender getBukkitSender() {
        return (BlockCommandSender) this.sender;
    }
    
    public void sendRawMessage(@NotNull String message) {
        this.sender.sendMessage(message);
    }

    public void sendRawMessage(@NotNull String[] messages) {
        this.sender.sendMessage(messages);
    }
    
    public boolean isOp() {
        return this.sender.isOp();
    }

    public void setOp(boolean flag) {
        this.sender.setOp(flag);
    }

    public boolean isPermissionSet(String permission) {
        return this.sender.isPermissionSet(permission);
    }

    public boolean isPermissionSet(Permission permission) {
        return this.sender.isPermissionSet(permission);
    }

    public boolean hasPermission(String permission) {
        return this.sender.hasPermission(permission);
    }

    public boolean hasPermission(Permission permission) {
        return this.sender.hasPermission(permission);
    }

    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String permission, boolean flag) {
        return this.sender.addAttachment(plugin, permission, flag);
    }

    public PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return this.sender.addAttachment(plugin);
    }

    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String permission, boolean flag, int i) {
        return this.sender.addAttachment(plugin, permission, flag, i);
    }

    public PermissionAttachment addAttachment(@NotNull Plugin plugin, int i) {
        return this.sender.addAttachment(plugin, i);
    }

    public void removeAttachment(PermissionAttachment permission) {
        this.sender.removeAttachment(permission);
    }

    public void recalculatePermissions() {
        this.sender.recalculatePermissions();
    }

    @NotNull
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return this.sender.getEffectivePermissions();
    }

    public void sendMessage(@NotNull String message) {
        this.sender.sendMessage(message);
    }

    public void sendMessage(@NotNull String[] messages) {
        this.sender.sendMessage(messages);
    }
    
    public Server getServer() {
        return this.sender.getServer();
    }

    public String getName() {
        return this.sender.getName();
    }

    public CommandSender.Spigot spigot() {
        return this.sender.spigot();
    }

    @Override
    public Block getBlock() {
        return this.getBukkitSender().getBlock();
    }

}
