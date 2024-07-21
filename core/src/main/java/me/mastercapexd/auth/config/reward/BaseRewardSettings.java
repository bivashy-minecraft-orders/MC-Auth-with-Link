package me.mastercapexd.auth.config.reward;

import com.bivashy.auth.api.AuthPlugin;
import com.bivashy.auth.api.account.Account;
import com.bivashy.auth.api.config.reward.RewardSettings;
import com.bivashy.auth.api.hook.ConnectorPluginHook;
import com.bivashy.configuration.ConfigurationHolder;
import com.bivashy.configuration.annotation.ConfigField;
import com.bivashy.configuration.holder.ConfigurationSectionHolder;

import java.util.List;

public class BaseRewardSettings implements ConfigurationHolder, RewardSettings {

    @ConfigField("execute-server")
    private String executeServer;
    @ConfigField("commands")
    private List<String> commands;

    public BaseRewardSettings(ConfigurationSectionHolder sectionHolder) {
        AuthPlugin.instance().getConfigurationProcessor().resolve(sectionHolder, this);
    }

    @Override
    public String getExecuteServer() {
        return executeServer;
    }

    @Override
    public List<String> getCommands() {
        return commands;
    }

    @Override
    public void execute(ConnectorPluginHook hook, Account account) {
        commands.forEach(command -> hook.executeConsoleCommand(executeServer, command.replace("%name%", account.getName())));
    }

}
