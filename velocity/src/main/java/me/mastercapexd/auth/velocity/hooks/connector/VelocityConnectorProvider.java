package me.mastercapexd.auth.velocity.hooks.connector;

import com.bivashy.auth.api.server.proxy.ProxyServer;
import com.velocitypowered.api.plugin.PluginContainer;
import de.themoep.connectorplugin.velocity.VelocityConnectorPlugin;

public class VelocityConnectorProvider {

    private final VelocityConnectorPlugin connectorPlugin;

    public VelocityConnectorProvider(PluginContainer pluginContainer) {
        this.connectorPlugin = (VelocityConnectorPlugin) pluginContainer.getInstance().orElseThrow();
    }

    public void executeConsoleCommand(String serverName, String command) {
        connectorPlugin.getBridge().runServerConsoleCommand(serverName, command);
    }

}
