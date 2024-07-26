package me.mastercapexd.auth.messenger.commands;

import java.util.Arrays;
import java.util.Optional;

import com.bivashy.auth.api.AuthPlugin;
import com.bivashy.auth.api.account.Account;
import com.bivashy.auth.api.bucket.LinkConfirmationBucket;
import com.bivashy.auth.api.config.PluginConfig;
import com.bivashy.auth.api.config.link.command.LinkCommandPathSettings;
import com.bivashy.auth.api.database.AccountDatabase;
import com.bivashy.auth.api.hook.ConnectorPluginHook;
import com.bivashy.auth.api.link.LinkType;
import com.bivashy.auth.api.link.user.LinkUser;
import com.bivashy.auth.api.link.user.confirmation.LinkConfirmationUser;
import com.bivashy.auth.api.link.user.info.LinkUserIdentificator;
import com.bivashy.auth.api.model.PlayerIdSupplier;
import com.bivashy.auth.api.shared.commands.MessageableCommandActor;
import com.bivashy.auth.api.type.LinkConfirmationType;

import io.github.revxrsal.eventbus.EventBus;
import me.mastercapexd.auth.link.LinkCommandActorWrapper;
import me.mastercapexd.auth.messenger.commands.exception.MessengerExceptionHandler;
import me.mastercapexd.auth.server.commands.annotations.GoogleUse;
import me.mastercapexd.auth.server.commands.parameters.NewPassword;
import me.mastercapexd.auth.shared.commands.LinkCodeCommand;
import me.mastercapexd.auth.shared.commands.MessengerLinkCommandTemplate;
import me.mastercapexd.auth.shared.commands.parameter.MessengerLinkContext;
import me.mastercapexd.auth.shared.commands.validator.CommandCooldownCondition;
import revxrsal.commands.CommandHandler;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.exception.SendMessageException;
import revxrsal.commands.orphan.OrphanCommand;
import revxrsal.commands.orphan.Orphans;

public abstract class MessengerCommandRegistry {

    private static final AuthPlugin PLUGIN = AuthPlugin.instance();
    private final CommandHandler commandHandler;
    private final LinkType linkType;

    public MessengerCommandRegistry(CommandHandler commandHandler, LinkType linkType) {
        this.commandHandler = commandHandler;
        this.linkType = linkType;
        register();
    }

    private void register() {
        registerContexts();
        registerDependencies();
    }

    private void registerContexts() {
        commandHandler.setExceptionHandler(new MessengerExceptionHandler(linkType));

        commandHandler.registerContextValue(LinkType.class, linkType);

        commandHandler.registerCondition(new CommandCooldownCondition<>(linkType.getLinkMessages(), SendMessageException::new));
        commandHandler.registerCondition((actor, command, arguments) -> {
            if (!command.hasAnnotation(GoogleUse.class))
                return;
            if (!PLUGIN.getConfig().getGoogleAuthenticatorSettings().isEnabled())
                throw new SendMessageException(linkType.getSettings().getMessages().getMessage("google-disabled"));
        });

        commandHandler.registerContextResolver(MessageableCommandActor.class, context -> wrapActor(context.actor()));

        commandHandler.registerValueResolver(PlayerIdSupplier.class, context -> PlayerIdSupplier.of(context.pop()));

        commandHandler.registerContextResolver(LinkUserIdentificator.class, context -> wrapActor(context.actor()).userId());

        commandHandler.registerValueResolver(NewPassword.class, context -> {
            String newRawPassword = context.pop();
            if (newRawPassword.length() < PLUGIN.getConfig().getPasswordMinLength())
                throw new SendMessageException(linkType.getSettings().getMessages().getMessage("changepass-password-too-short"));

            if (newRawPassword.length() > PLUGIN.getConfig().getPasswordMaxLength())
                throw new SendMessageException(linkType.getSettings().getMessages().getMessage("changepass-password-too-long"));
            return new NewPassword(newRawPassword);
        });

        commandHandler.registerValueResolver(MessengerLinkContext.class, (context) -> {
            String code = context.popForParameter();

            Optional<LinkConfirmationUser> confirmationUserOptional = PLUGIN.getLinkConfirmationBucket()
                    .findFirst(user -> user.getConfirmationCode().equals(code) && user.getLinkType().equals(linkType));

            if (!confirmationUserOptional.isPresent())
                throw new SendMessageException(linkType.getSettings().getMessages().getMessage("confirmation-no-code"));

            LinkConfirmationUser confirmationUser = confirmationUserOptional.get();

            if (System.currentTimeMillis() > confirmationUser.getLinkTimeoutTimestamp())
                throw new SendMessageException(linkType.getSettings().getMessages().getMessage("confirmation-timed-out"));

            LinkUser linkUser = confirmationUser.getLinkTarget().findFirstLinkUserOrNew(user -> user.getLinkType().equals(linkType), linkType);

            if (!linkUser.isIdentifierDefaultOrNull())
                throw new SendMessageException(linkType.getSettings()
                        .getMessages()
                        .getMessage("confirmation-already-linked", linkType.newMessageContext(confirmationUser.getLinkTarget())));

            return new MessengerLinkContext(code, confirmationUser);
        });

        commandHandler.registerValueResolver(Account.class, (context) -> {
            String playerName = context.popForParameter();
            LinkUserIdentificator userId = wrapActor(context.actor()).userId();
            Account account = PLUGIN.getAccountDatabase().getAccountFromName(playerName).get();
            if (account == null || !account.isRegistered())
                throw new SendMessageException(linkType.getSettings().getMessages().getMessage("account-not-found"));

            Optional<LinkUser> linkUser = account.findFirstLinkUser(user -> user.getLinkType().equals(linkType));
            if (!linkType.getSettings().isAdministrator(userId)) {
                if (!linkUser.isPresent())
                    throw new SendMessageException(linkType.getSettings().getMessages().getMessage("not-your-account", linkType.newMessageContext(account)));

                if (!linkUser.get().getLinkUserInfo().getIdentificator().equals(userId))
                    throw new SendMessageException(linkType.getSettings().getMessages().getMessage("not-your-account", linkType.newMessageContext(account)));
            }
            return account;
        });
    }

    private void registerDependencies() {
        commandHandler.registerDependency(LinkConfirmationBucket.class, PLUGIN.getLinkConfirmationBucket());
        commandHandler.registerDependency(EventBus.class, PLUGIN.getEventBus());
        commandHandler.registerDependency(AccountDatabase.class, PLUGIN.getAccountDatabase());
        commandHandler.registerDependency(PluginConfig.class, PLUGIN.getConfig());
        commandHandler.registerDependency(AuthPlugin.class, PLUGIN);
        commandHandler.registerDependency(LinkType.class, linkType);
        commandHandler.registerDependency(ConnectorPluginHook.class, PLUGIN.getHook(ConnectorPluginHook.class));
    }

    protected void registerCommands() {
        if (confirmationTypeEnabled(LinkConfirmationType.FROM_LINK))
            registerCommand(LinkCodeCommand.CONFIGURATION_KEY, new LinkCodeCommand());
        if (confirmationTypeEnabled(LinkConfirmationType.FROM_GAME))
            registerCommand(MessengerLinkCommandTemplate.CONFIGURATION_KEY, createLinkCommand());

        registerCommand(LinkWithPasswordCommand.CONFIGURATION_KEY, new LinkWithPasswordCommand());
        registerCommand(ConfirmationToggleCommand.CONFIGURATION_KEY, new ConfirmationToggleCommand());
        registerCommand(AccountsListCommand.CONFIGURATION_KEY, new AccountsListCommand());
        registerCommand(AccountsRawListCommand.CONFIGURATION_KEY, new AccountsRawListCommand());
        registerCommand(AccountCommand.CONFIGURATION_KEY, new AccountCommand());
        registerCommand(AccountEnterAcceptCommand.CONFIGURATION_KEY, new AccountEnterAcceptCommand());
        registerCommand(AccountEnterDeclineCommand.CONFIGURATION_KEY, new AccountEnterDeclineCommand());
        registerCommand(KickCommand.CONFIGURATION_KEY, new KickCommand());
        registerCommand(RestoreCommand.CONFIGURATION_KEY, new RestoreCommand());
        registerCommand(UnlinkCommand.CONFIGURATION_KEY, new UnlinkCommand());
        registerCommand(ChangePasswordCommand.CONFIGURATION_KEY, new ChangePasswordCommand());
        registerCommand(GoogleUnlinkCommand.CONFIGURATION_KEY, new GoogleUnlinkCommand());
        registerCommand(GoogleCommand.CONFIGURATION_KEY, new GoogleCommand());
        registerCommand(GoogleCodeCommand.CONFIGURATION_KEY, new GoogleCodeCommand());
        registerCommand(AdminPanelCommand.CONFIGURATION_KEY, new AdminPanelCommand());

        registerCommand(RewardCommand.CONFIGURATION_KEY, new RewardCommand());
    }

    private void registerCommand(String key, OrphanCommand commandInstance) {
        String[] path = commandPath(key);
        if (path.length == 0 || Arrays.stream(path).allMatch(String::isEmpty))
            return;
        Orphans orphans = Orphans.path(path);
        commandHandler.register(orphans.handler(commandInstance));
    }

    private boolean confirmationTypeEnabled(LinkConfirmationType confirmationType) {
        return linkType.getSettings().getLinkConfirmationTypes().contains(confirmationType);
    }

    private String[] commandPath(String key) {
        LinkCommandPathSettings pathSettings = linkType.getSettings().getCommandPaths().getCommandPath(key);
        if (pathSettings == null)
            return new String[0];
        return pathSettings.getCommandPaths();
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    protected LinkCommandActorWrapper wrapActor(CommandActor actor) {
        return actor.as(LinkCommandActorWrapper.class);
    }

    protected abstract MessengerLinkCommandTemplate createLinkCommand();

}
