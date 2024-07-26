package me.mastercapexd.auth.messenger.commands;

import com.bivashy.auth.api.account.Account;
import com.bivashy.auth.api.link.LinkType;
import com.bivashy.messenger.common.keyboard.Keyboard;

import me.mastercapexd.auth.link.LinkCommandActorWrapper;
import me.mastercapexd.auth.shared.commands.annotation.CommandKey;
import me.mastercapexd.auth.shared.commands.annotation.CommandCooldown;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.orphan.OrphanCommand;

@CommandKey(AccountCommand.CONFIGURATION_KEY)
public class AccountCommand implements OrphanCommand {
    public static final String CONFIGURATION_KEY = "account-control";

    @DefaultFor("~")
    @CommandCooldown(CommandCooldown.DEFAULT_VALUE)
    public void accountMenu(LinkCommandActorWrapper actorWrapper, LinkType linkType, Account account) {
        Keyboard accountKeyboard = linkType.getSettings().getKeyboards().createKeyboard("account", "%account_name%", account.getName());
        actorWrapper.send(linkType.newMessageBuilder(linkType.getLinkMessages().getMessage("account-control", linkType.newMessageContext(account)))
                .keyboard(accountKeyboard)
                .build());
    }
}
