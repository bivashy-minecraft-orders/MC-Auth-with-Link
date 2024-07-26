package me.mastercapexd.auth.messenger.commands;

import com.bivashy.auth.api.config.message.Messages;
import com.bivashy.auth.api.crypto.HashInput;
import com.bivashy.auth.api.database.AccountDatabase;
import com.bivashy.auth.api.link.LinkType;
import com.bivashy.auth.api.link.user.LinkUser;
import me.mastercapexd.auth.config.message.context.account.BaseAccountPlaceholderContext;
import me.mastercapexd.auth.link.LinkCommandActorWrapper;
import me.mastercapexd.auth.shared.commands.annotation.CommandKey;
import me.mastercapexd.auth.messenger.commands.annotation.ConfigurationArgumentError;
import me.mastercapexd.auth.shared.commands.annotation.CommandCooldown;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Dependency;
import revxrsal.commands.orphan.OrphanCommand;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@CommandKey(LinkWithPasswordCommand.CONFIGURATION_KEY)
public class LinkWithPasswordCommand implements OrphanCommand {

    public static final String CONFIGURATION_KEY = "link-with-pass";
    @Dependency
    private AccountDatabase accountDatabase;

    @ConfigurationArgumentError("link-with-pass-not-enough-arguments")
    @DefaultFor("~")
    @CommandCooldown(value = 5, unit = TimeUnit.SECONDS)
    public void onLink(LinkCommandActorWrapper actorWrapper, LinkType linkType, String playerName, String password) {
        Messages<String> linkMessages = linkType.getSettings().getMessages();
        actorWrapper.deleteTriggerMessage();
        accountDatabase.getAccountFromName(playerName).whenComplete(((account, throwable) -> {
            if (account == null || !account.isRegistered()) {
                actorWrapper.reply(linkMessages.getMessage("account-not-found"));
                return;
            }
            Optional<LinkUser> linkUser = account.findFirstLinkUser(user -> user.getLinkType().equals(linkType));
            if (linkUser.isPresent()) {
                actorWrapper.reply(linkMessages.getMessage("not-your-account", linkType.newMessageContext(account)));
                return;
            }
            boolean validPassword = account.getCryptoProvider().matches(HashInput.of(password), account.getPasswordHash());
            if (!validPassword) {
                actorWrapper.reply(linkMessages.getMessage("invalid-link-password", linkType.newMessageContext(account)));
                return;
            }
            LinkUser foundLinkUser = account.findFirstLinkUserOrNew(user -> user.getLinkType().equals(linkType), linkType);
            foundLinkUser.getLinkUserInfo().setIdentificator(actorWrapper.userId());

            accountDatabase.updateAccountLinks(account);
            actorWrapper.replyWithMessage(linkMessages.getMessage("confirmation-success", new BaseAccountPlaceholderContext(account)));
        }));
    }

}
