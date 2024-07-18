package com.bivashy.auth.api.hook;

import com.bivashy.auth.api.server.proxy.ProxyServer;

public interface ConnectorPluginHook extends PluginHook {

    void executeConsoleCommand(ProxyServer server, String command);

}
