package me.mastercapexd.auth.shared.commands;

import java.util.Collection;
import java.util.function.Predicate;

import com.bivashy.auth.api.AuthPlugin;
import com.bivashy.auth.api.account.Account;
import com.bivashy.auth.api.config.PluginConfig;
import com.bivashy.auth.api.config.message.MessageContext;
import com.bivashy.auth.api.config.message.Messages;
import com.bivashy.auth.api.link.LinkType;
import com.bivashy.auth.api.link.user.LinkUser;
import com.bivashy.auth.api.link.user.confirmation.LinkConfirmationUser;
import com.bivashy.auth.api.link.user.info.LinkUserIdentificator;
import com.bivashy.auth.api.shared.commands.MessageableCommandActor;
import com.bivashy.auth.api.type.LinkConfirmationType;

public class MessengerLinkCommandTemplate {
    private final AuthPlugin plugin = AuthPlugin.instance();
    private final PluginConfig config = plugin.getConfig();
    private final LinkConfirmationType linkConfirmationType;
    private final Messages<?> messages;
    private final LinkType linkType;

    public MessengerLinkCommandTemplate(LinkConfirmationType linkConfirmationType, Messages<?> messages, LinkType linkType) {
        this.linkConfirmationType = linkConfirmationType;
        this.messages = messages;
        this.linkType = linkType;
    }

    public boolean isInvalidAccount(Account account, MessageableCommandActor commandActor, Predicate<LinkUser> linkFilter) {
        if (account == null || !account.isRegistered()) {
            commandActor.replyWithMessage(config.getServerMessages().getMessage("account-not-found"));
            return true;
        }
        LinkUser linkUser = account.findFirstLinkUserOrNew(linkFilter, linkType);
        if (!linkUser.isIdentifierDefaultOrNull()) {
            commandActor.replyWithMessage(messages.getMessage("already-linked"));
            return true;
        }
        return false;
    }

    public boolean isInvalidLinkAccounts(Collection<Account> accounts, MessageableCommandActor commandActor) {
        if (linkType.getSettings().getMaxLinkCount() != 0 && accounts.size() >= linkType.getSettings().getMaxLinkCount()) {
            commandActor.replyWithMessage(messages.getMessage("link-limit-reached"));
            return true;
        }
        return false;
    }

    public void sendLinkConfirmation(LinkUserIdentificator identificator, MessageableCommandActor commandActor, LinkConfirmationUser confirmationUser,
            String accountId) {
        plugin.getLinkConfirmationBucket()
                .removeLinkUsers(linkConfirmationUser -> linkConfirmationUser.getAccount().getPlayerId().equals(accountId) &&
                        linkConfirmationUser.getLinkUserInfo().getIdentificator().equals(identificator) &&
                        linkConfirmationUser.getConfirmationInfo().getLinkConfirmationType() == linkConfirmationType);
        plugin.getLinkConfirmationBucket().addLinkUser(confirmationUser);
        commandActor.replyWithMessage(
                messages.getMessage("confirmation-sent", MessageContext.of("%code%", confirmationUser.getConfirmationInfo().getConfirmationCode())));
    }

    public LinkConfirmationType getLinkConfirmationType() {
        return linkConfirmationType;
    }
}