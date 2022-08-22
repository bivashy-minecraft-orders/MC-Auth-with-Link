package me.mastercapexd.auth.proxy.player;

import java.util.UUID;

import me.mastercapexd.auth.function.Castable;
import me.mastercapexd.auth.proxy.ProxyPlugin;
import me.mastercapexd.auth.proxy.adventure.AdventureProxyComponent;
import me.mastercapexd.auth.proxy.message.ProxyComponent;
import me.mastercapexd.auth.proxy.server.Server;

public interface ProxyPlayer extends Castable<ProxyPlayer> {
    default void disconnect(String reason) {
        disconnect(ProxyPlugin.instance().getCore().componentJson(new AdventureProxyComponent(
                ProxyPlugin.instance().getConfig().getProxyMessages().getDeserializer().deserialize(reason)).jsonText()));
    }

    void disconnect(ProxyComponent component);

    default void sendMessage(String message) {
        if (message.isEmpty())
            return;
        new AdventureProxyComponent(ProxyPlugin.instance().getConfig().getProxyMessages().getDeserializer().deserialize(message)).send(this);
    }

    void sendMessage(ProxyComponent component);

    String getNickname();

    UUID getUniqueId();

    String getPlayerIp();

    <T> T getRealPlayer();

    default void sendTo(Server server) {
        server.sendPlayer(this);
    }
}
