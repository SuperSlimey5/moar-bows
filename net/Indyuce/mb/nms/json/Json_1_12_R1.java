package net.Indyuce.mb.nms.json;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;
import net.minecraft.server.v1_12_R1.PlayerConnection;

public class Json_1_12_R1 implements Json {
	@Override
	public void message(Player p, String msg) {
		PacketPlayOutChat packet = new PacketPlayOutChat(ChatSerializer.a(msg));
		PlayerConnection co = ((CraftPlayer) p).getHandle().playerConnection;
		co.sendPacket(packet);
	}
}