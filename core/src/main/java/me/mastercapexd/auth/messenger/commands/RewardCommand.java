package me.mastercapexd.auth.messenger.commands;

import com.bivashy.auth.api.AuthPlugin;
import com.bivashy.auth.api.account.Account;
import com.bivashy.auth.api.config.PluginConfig;
import com.bivashy.auth.api.database.RewardDatabase;
import com.bivashy.auth.api.hook.ConnectorPluginHook;
import com.bivashy.auth.api.link.LinkType;
import me.mastercapexd.auth.database.adapter.AccountAdapter;
import me.mastercapexd.auth.database.model.DReward;
import me.mastercapexd.auth.link.LinkCommandActorWrapper;
import me.mastercapexd.auth.messenger.commands.annotation.CommandKey;
import me.mastercapexd.auth.messenger.commands.annotation.ConfigurationArgumentError;
import me.mastercapexd.auth.shared.commands.annotation.CommandCooldown;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Dependency;
import revxrsal.commands.orphan.OrphanCommand;

@CommandKey(RewardCommand.CONFIGURATION_KEY)
public class RewardCommand implements OrphanCommand {

    public static final String CONFIGURATION_KEY = "reward";
    @Dependency
    private PluginConfig config;
    @Dependency
    private AuthPlugin authPlugin;
    @Dependency
    private ConnectorPluginHook connectorPluginHook;

    @ConfigurationArgumentError("tokens-not-enough-arguments")
    @DefaultFor("~")
    @CommandCooldown(CommandCooldown.DEFAULT_VALUE)
    public void onTokens(LinkCommandActorWrapper actorWrapper, LinkType linkType, Account account) {
        RewardDatabase rewardDatabase = authPlugin.getRewardDatabase();
        rewardDatabase.exists(account).whenComplete((exists, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }
            if (exists) {
                actorWrapper.reply(linkType.getLinkMessages().getMessage("reward-already-given"));
                return;
            }
            linkType.getSettings().getRewardSettings().execute(connectorPluginHook, account);
            rewardDatabase.create(new DReward(new AccountAdapter(account)));
            actorWrapper.reply(linkType.getLinkMessages().getStringMessage("reward-given"));
        });
    }

}
