package com.bivashy.auth.api.hook;

public interface ConnectorPluginHook extends PluginHook {

    void executeConsoleCommand(String serverName, String command);

}
