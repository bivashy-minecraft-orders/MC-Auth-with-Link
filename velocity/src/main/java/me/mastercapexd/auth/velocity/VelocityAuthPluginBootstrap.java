package me.mastercapexd.auth.velocity;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

import com.bivashy.auth.api.hook.ConnectorPluginHook;
import me.mastercapexd.auth.velocity.hooks.connector.VelocityConnectorPluginHook;
import org.slf4j.Logger;

import com.alessiodp.libby.VelocityLibraryManager;
import com.bivashy.auth.api.AuthPlugin;
import com.bivashy.auth.api.hook.LimboPluginHook;
import com.bivashy.auth.api.server.ServerCore;
import com.bivashy.messenger.vk.message.VkMessage;
import com.bivashy.messenger.vk.provider.VkApiProvider;
import com.google.inject.Inject;
import com.ubivashka.vk.velocity.VelocityVkApiPlugin;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import me.mastercapexd.auth.BaseAuthPlugin;
import me.mastercapexd.auth.hooks.VkPluginHook;
import me.mastercapexd.auth.management.BaseLibraryManagement;
import me.mastercapexd.auth.velocity.adventure.VelocityAudienceProvider;
import me.mastercapexd.auth.velocity.commands.VelocityCommandRegistry;
import me.mastercapexd.auth.velocity.hooks.VelocityVkPluginHook;
import me.mastercapexd.auth.velocity.hooks.nanolimbo.VelocityNanoLimboPluginHook;
import me.mastercapexd.auth.velocity.listener.AuthenticationListener;
import me.mastercapexd.auth.velocity.listener.VkDispatchListener;
import me.mastercapexd.auth.vk.command.VKCommandRegistry;
import net.kyori.adventure.platform.AudienceProvider;

public class VelocityAuthPluginBootstrap {

    private static VelocityAuthPluginBootstrap instance;
    private final AudienceProvider audienceProvider;
    private final ServerCore core;
    private final File dataFolder;
    private final ProxyServer proxyServer;
    private final Logger logger;
    private BaseAuthPlugin authPlugin;

    @Inject
    public VelocityAuthPluginBootstrap(ProxyServer proxyServer, Logger logger, @DataDirectory Path dataDirectory) {
        instance = this;
        this.logger = logger;
        this.proxyServer = proxyServer;
        this.dataFolder = dataDirectory.toFile();
        this.core = new VelocityProxyCore(proxyServer);
        this.audienceProvider = new VelocityAudienceProvider(proxyServer);
    }

    public static VelocityAuthPluginBootstrap getInstance() {
        return instance;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        this.authPlugin = new BaseAuthPlugin(audienceProvider,
                proxyServer.getPluginManager().fromInstance(this).map(PluginContainer::getDescription).flatMap(PluginDescription::getVersion).orElse("unknown"),
                dataFolder, core, new BaseLibraryManagement(new VelocityLibraryManager<>(this, logger, dataFolder.toPath(), proxyServer.getPluginManager())));
        initializeListener();
        initializeCommand();
        initializeLimbo();
        initializeConnectorPlugin();
        if (authPlugin.getConfig().getVKSettings().isEnabled())
            initializeVk();
    }

    private void initializeVk() {
        authPlugin.putHook(VkPluginHook.class, new VelocityVkPluginHook());
        VkMessage.setDefaultApiProvider(VkApiProvider.of(VelocityVkApiPlugin.getInstance().getVkApiProvider().getActor(),
                VelocityVkApiPlugin.getInstance().getVkApiProvider().getVkApiClient()));

        proxyServer.getEventManager().register(this, new VkDispatchListener());
        new VKCommandRegistry();
    }

    private void initializeListener() {
        proxyServer.getEventManager().register(this, new AuthenticationListener(authPlugin));
    }

    private void initializeCommand() {
        new VelocityCommandRegistry(this, authPlugin);
    }

    private void initializeLimbo() {
        Collection<LimboPluginHook> limboPluginHooks = Collections.singleton(new VelocityNanoLimboPluginHook(authPlugin.getConfig().getLimboPortRange(), proxyServer));
        limboPluginHooks.stream()
                .filter(LimboPluginHook::canHook)
                .forEach(limboPluginHook -> authPlugin.putHook(LimboPluginHook.class, limboPluginHook));
    }

    private void initializeConnectorPlugin() {
        authPlugin.putHook(ConnectorPluginHook.class, new VelocityConnectorPluginHook(proxyServer));
    }

    public ProxyServer getProxyServer() {
        return proxyServer;
    }

    public AuthPlugin getAuthPlugin() {
        return authPlugin;
    }

}
