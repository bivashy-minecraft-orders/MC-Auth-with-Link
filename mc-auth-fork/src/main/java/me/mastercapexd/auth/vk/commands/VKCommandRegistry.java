package me.mastercapexd.auth.vk.commands;

import com.ubivashka.lamp.commands.vk.core.VkHandler;
import com.ubivashka.vk.api.providers.VkApiProvider;
import com.ubivashka.vk.bungee.BungeeVkApiPlugin;

import me.mastercapexd.auth.bungee.AuthPlugin;
import me.mastercapexd.auth.link.LinkCommandActorWrapper;
import me.mastercapexd.auth.link.LinkType;
import me.mastercapexd.auth.link.vk.VKCommandActorWrapper;
import me.mastercapexd.auth.link.vk.VKLinkType;
import net.md_5.bungee.api.ProxyServer;
import revxrsal.commands.CommandHandler;

public class VKCommandRegistry {
	private static final VkApiProvider VK_API_PROVIDER = BungeeVkApiPlugin.getInstance().getVkApiProvider();
	private final CommandHandler commandHandler = new VkHandler(VK_API_PROVIDER.getVkApiClient(),
			VK_API_PROVIDER.getActor()).disableStackTraceSanitizing();

	static {
		ProxyServer.getInstance().getPluginManager().registerListener(AuthPlugin.getInstance(), new MessageListener());
	}

	public VKCommandRegistry() {
		register();
	}

	private void register() {
		registerContexts();
		registerCommands();
	}

	private void registerContexts() {
		commandHandler.registerContextValue(LinkType.class, VKLinkType.getInstance());

		commandHandler.registerContextResolver(LinkCommandActorWrapper.class,
				context -> new VKCommandActorWrapper(context.actor()));

		commandHandler.registerContextResolver(VKCommandActorWrapper.class,
				context -> new VKCommandActorWrapper(context.actor()));

	}

	private void registerCommands() {
	}
}