package me.mastercapexd.auth.link.user;

import java.util.Optional;

import me.mastercapexd.auth.account.Account;
import me.mastercapexd.auth.link.LinkType;
import me.mastercapexd.auth.link.user.info.LinkUserInfo;
import me.mastercapexd.auth.link.user.info.LinkUserInfoTemplate;
import me.mastercapexd.auth.link.user.info.identificator.LinkUserIdentificator;
import me.mastercapexd.auth.link.user.info.identificator.UserNumberIdentificator;
import me.mastercapexd.auth.link.user.info.identificator.UserStringIdentificator;
import me.mastercapexd.auth.proxy.ProxyPlugin;
import me.mastercapexd.auth.storage.model.AccountLink;

public class AccountLinkAdapter extends LinkUserTemplate {
    private final LinkUserInfo linkUserInfo;
    private AccountLink accountLink;

    public AccountLinkAdapter(LinkType linkType, Account account, LinkUserInfo linkUserInfo) {
        super(linkType, account);
        this.linkUserInfo = linkUserInfo;
    }

    public AccountLinkAdapter(LinkUser linkUser) {
        this(linkUser.getLinkType(), linkUser.getAccount(), linkUser.getLinkUserInfo());
    }

    public AccountLinkAdapter(AccountLink accountLink, Account account) {
        this(ProxyPlugin.instance()
                        .getLinkTypeProvider()
                        .getLinkType(accountLink.getLinkType())
                        .orElseThrow(() -> new IllegalArgumentException("Link type " + accountLink.getLinkType() + " not exists!")), account,
                new AccountLinkUserInfo(accountLink.getLinkUserId(), accountLink.isLinkEnabled()));
        this.accountLink = accountLink;
    }

    @Override
    public LinkUserInfo getLinkUserInfo() {
        return linkUserInfo;
    }

    public Optional<AccountLink> getAccountLink() {
        return Optional.ofNullable(accountLink);
    }

    public static class AccountLinkUserInfo extends LinkUserInfoTemplate {
        public AccountLinkUserInfo(LinkUserIdentificator userIdentificator, boolean confirmationEnabled) {
            super(userIdentificator, confirmationEnabled);
        }

        public AccountLinkUserInfo(String userId, boolean confirmationEnabled) {
            this(isLong(userId) ? new UserNumberIdentificator(Long.parseLong(userId)) : new UserStringIdentificator(userId), confirmationEnabled);
        }

        private static boolean isLong(String input) {
            try {
                Long.parseLong(input);
                return true;
            } catch(NumberFormatException e) {
                return false;
            }
        }
    }
}