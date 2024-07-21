package me.mastercapexd.auth.bungee.hooks.connector;

import com.bivashy.auth.api.hook.ConnectorPluginHook;
import com.bivashy.auth.api.server.proxy.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeConnectorPluginHook implements ConnectorPluginHook {

    private BungeeConnectorProvider provider;

    public BungeeConnectorPluginHook() {
        if (!canHook())
            return;
        this.provider = new BungeeConnectorProvider(connectorPlugin());
    }

    @Override
    public void executeConsoleCommand(String serverName, String command) {
        if (provider == null)
            throw new IllegalStateException("ConnectorPlugin is missing!");
        provider.executeConsoleCommand(serverName, command);
    }

    @Override
    public boolean canHook() {
        return connectorPlugin() != null;
    }

    private Plugin connectorPlugin() {
        return net.md_5.bungee.api.ProxyServer.getInstance().getPluginManager().getPlugin("ConnectorPlugin");
    }

}
