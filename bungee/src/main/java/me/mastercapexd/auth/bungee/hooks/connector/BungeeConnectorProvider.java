package me.mastercapexd.auth.bungee.hooks.connector;

import com.bivashy.auth.api.server.proxy.ProxyServer;
import de.themoep.connectorplugin.bungee.BungeeConnectorPlugin;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeConnectorProvider {

    private final BungeeConnectorPlugin connectorPlugin;

    public BungeeConnectorProvider(Plugin connectorPlugin) {
        this.connectorPlugin = (BungeeConnectorPlugin) connectorPlugin;
    }

    public void executeConsoleCommand(String serverName, String command) {
        connectorPlugin.getBridge().runServerConsoleCommand(serverName, command);
    }

}
