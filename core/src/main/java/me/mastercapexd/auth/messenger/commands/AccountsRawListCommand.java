package me.mastercapexd.auth.messenger.commands;

import com.bivashy.auth.api.account.Account;
import com.bivashy.auth.api.database.AccountDatabase;
import com.bivashy.auth.api.link.LinkType;
import me.mastercapexd.auth.link.LinkCommandActorWrapper;
import me.mastercapexd.auth.messenger.commands.annotation.CommandKey;
import me.mastercapexd.auth.shared.commands.annotation.CommandCooldown;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Dependency;
import revxrsal.commands.orphan.OrphanCommand;

import java.util.stream.Collectors;

@CommandKey(AccountsRawListCommand.CONFIGURATION_KEY)
public class AccountsRawListCommand implements OrphanCommand {

    public static final String CONFIGURATION_KEY = "accounts-raw";
    @Dependency
    private AccountDatabase database;
    @Dependency
    private LinkType linkType;

    @DefaultFor("~")
    @CommandCooldown(CommandCooldown.DEFAULT_VALUE)
    public void onAccountRawList(LinkCommandActorWrapper actorWrapper, LinkType linkType) {
        database.getAccountsFromLinkIdentificator(actorWrapper.userId()).whenComplete(((accounts, throwable) -> {
            if (throwable != null) {
                actorWrapper.reply("error");
                throwable.printStackTrace();
                return;
            }
            if (accounts.isEmpty()) {
                actorWrapper.reply(linkType.getLinkMessages().getMessage("no-accounts"));
                return;
            }
            String rawAccounts = accounts.stream().map(Account::getName).collect(Collectors.joining(", "));
            actorWrapper.reply(linkType.getLinkMessages().getMessage("raw-accounts").replace("%accounts%", rawAccounts));
        }));
    }

}
