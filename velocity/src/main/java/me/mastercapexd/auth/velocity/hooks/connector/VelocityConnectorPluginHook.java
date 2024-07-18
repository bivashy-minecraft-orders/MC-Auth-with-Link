package me.mastercapexd.auth.velocity.hooks.connector;

import com.bivashy.auth.api.hook.ConnectorPluginHook;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;

import java.util.Optional;

public class VelocityConnectorPluginHook implements ConnectorPluginHook {

    private ProxyServer proxyServer;
    private VelocityConnectorProvider provider;

    public VelocityConnectorPluginHook(ProxyServer proxyServer) {
        if (!canHook())
            return;
        this.proxyServer = proxyServer;
        PluginContainer pluginContainer = connectorPlugin().orElseThrow();
        this.provider = new VelocityConnectorProvider(pluginContainer);
    }

    @Override
    public void executeConsoleCommand(com.bivashy.auth.api.server.proxy.ProxyServer server, String command) {
        if (provider == null)
            throw new IllegalStateException("ConnectorPlugin is missing!");
        provider.executeConsoleCommand(server, command);
    }

    @Override
    public boolean canHook() {
        return proxyServer.getPluginManager().isLoaded("connectorplugin");
    }

    private Optional<PluginContainer> connectorPlugin() {
        return proxyServer.getPluginManager().getPlugin("connectorplugin");
    }

}
