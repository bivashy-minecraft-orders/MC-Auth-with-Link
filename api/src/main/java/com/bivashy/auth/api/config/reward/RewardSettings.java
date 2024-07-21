package com.bivashy.auth.api.config.reward;

import com.bivashy.auth.api.account.Account;
import com.bivashy.auth.api.hook.ConnectorPluginHook;

import java.util.List;

public interface RewardSettings {

    String getExecuteServer();

    List<String> getCommands();

    void execute(ConnectorPluginHook hook, Account account);

}
