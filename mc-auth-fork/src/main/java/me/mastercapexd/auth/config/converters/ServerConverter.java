package me.mastercapexd.auth.config.converters;

import java.util.List;

import com.ubivashka.config.contexts.BungeeConfigurationContext;
import com.ubivashka.config.converters.BungeeConfigurationListConverter;
import com.ubivashka.config.holders.IConfigurationSectionHolder;

import me.mastercapexd.auth.objects.Server;
import net.md_5.bungee.config.Configuration;

public class ServerConverter extends BungeeConfigurationListConverter<String, Server> {

	public ServerConverter() {
		super(Server.class);
	}

	@Override
	protected Server valueToEntity(BungeeConfigurationContext context, String valueObject) {
		return new Server(valueObject);
	}

	@Override
	protected String getConfigurationValue(BungeeConfigurationContext context) {
		IConfigurationSectionHolder<Configuration> sectionHolder = context.getConfigurationSectionHolder();
		if (!sectionHolder.isString(context.getConfigurationPath()) && !sectionHolder.isCollection(context.getConfigurationPath()))
			return null;
		return sectionHolder.getString(context.getConfigurationPath());
	}

	@Override
	protected List<String> getConfigurationValues(BungeeConfigurationContext context) {
		IConfigurationSectionHolder<Configuration> sectionHolder = context.getConfigurationSectionHolder();
		if (!sectionHolder.isCollection(context.getConfigurationPath()))
			return null;
		List<String> stringList = sectionHolder.getList(context.getConfigurationPath());
		return stringList;
	}

}