package me.mastercapexd.auth.link.user.info;

import me.mastercapexd.auth.link.user.info.identificator.LinkUserIdentificator;

public class LinkUserInfoTemplate implements LinkUserInfo {
    protected LinkUserIdentificator userIdentificator;
    protected boolean confirmationEnabled;

    public LinkUserInfoTemplate(LinkUserIdentificator userIdentificator, boolean confirmationEnabled) {
        this.userIdentificator = userIdentificator;
        this.confirmationEnabled = confirmationEnabled;
    }

    public LinkUserInfoTemplate(LinkUserIdentificator userIdentificator) {
        this(userIdentificator, true);
    }

    @Override
    public LinkUserIdentificator getIdentificator() {
        return userIdentificator;
    }

    @Override
    public LinkUserInfo setIdentificator(LinkUserIdentificator userIdentificator) {
        this.userIdentificator = userIdentificator;
        return this;
    }

    @Override
    public boolean isConfirmationEnabled() {
        return confirmationEnabled;
    }

    @Override
    public LinkUserInfo setConfirmationEnabled(boolean confirmationEnabled) {
        this.confirmationEnabled = confirmationEnabled;
        return this;
    }
}