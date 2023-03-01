package com.bivashy.auth.api.config;

import java.util.List;
import java.util.regex.Pattern;

import com.bivashy.auth.api.config.bossbar.BossBarSettings;
import com.bivashy.auth.api.config.database.DatabaseSettings;
import com.bivashy.auth.api.config.database.LegacyStorageDataSettings;
import com.bivashy.auth.api.config.link.GoogleAuthenticatorSettings;
import com.bivashy.auth.api.config.link.TelegramSettings;
import com.bivashy.auth.api.config.link.VKSettings;
import com.bivashy.auth.api.config.message.server.ServerMessages;
import com.bivashy.auth.api.config.server.ConfigurationServer;
import com.bivashy.auth.api.type.HashType;
import com.bivashy.auth.api.type.IdentifierType;
import com.bivashy.auth.api.type.StorageType;

public interface PluginConfig {
    @Deprecated
    LegacyStorageDataSettings getStorageDataSettings();

    DatabaseSettings getDatabaseConfiguration();

    IdentifierType getActiveIdentifierType();

    boolean isNameCaseCheckEnabled();

    HashType getActiveHashType();

    StorageType getStorageType();

    Pattern getNamePattern();

    Pattern getPasswordPattern();

    List<ConfigurationServer> getAuthServers();

    List<ConfigurationServer> getGameServers();

    List<ConfigurationServer> getBlockedServers();

    List<Pattern> getAllowedCommands();

    List<String> getAuthenticationSteps();

    String getAuthenticationStepName(int index);

    boolean isPasswordConfirmationEnabled();

    int getPasswordMinLength();

    int getPasswordMaxLength();

    int getPasswordAttempts();

    int getMaxLoginPerIP();

    int getMessagesDelay();

    long getSessionDurability();

    long getJoinDelay();

    long getAuthTime();

    boolean shouldBlockChat();

    ServerMessages getServerMessages();

    BossBarSettings getBossBarSettings();

    ConfigurationServer findServerInfo(List<ConfigurationServer> servers);

    void reload();

    GoogleAuthenticatorSettings getGoogleAuthenticatorSettings();

    TelegramSettings getTelegramSettings();

    VKSettings getVKSettings();
}