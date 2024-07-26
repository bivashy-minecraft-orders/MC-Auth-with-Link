package me.mastercapexd.auth.messenger.commands;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.bivashy.auth.api.AuthPlugin;
import com.bivashy.auth.api.account.Account;
import com.bivashy.auth.api.config.PluginConfig;
import com.bivashy.auth.api.database.AccountDatabase;
import com.bivashy.auth.api.link.LinkType;
import com.bivashy.auth.api.link.user.LinkUser;
import com.bivashy.messenger.common.file.MessengerFile;
import com.bivashy.messenger.common.message.Message;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import me.mastercapexd.auth.link.LinkCommandActorWrapper;
import me.mastercapexd.auth.link.google.GoogleLinkType;
import me.mastercapexd.auth.shared.commands.annotation.CommandKey;
import me.mastercapexd.auth.messenger.commands.annotation.ConfigurationArgumentError;
import me.mastercapexd.auth.server.commands.annotations.GoogleUse;
import me.mastercapexd.auth.shared.commands.annotation.CommandCooldown;
import me.mastercapexd.auth.util.GoogleAuthenticatorQRGenerator;
import me.mastercapexd.auth.util.RandomCodeFactory;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Dependency;
import revxrsal.commands.orphan.OrphanCommand;

@CommandKey(GoogleCommand.CONFIGURATION_KEY)
public class GoogleCommand implements OrphanCommand {
    public static final String CONFIGURATION_KEY = "google";
    @Dependency
    private AuthPlugin plugin;
    @Dependency
    private PluginConfig config;
    @Dependency
    private AccountDatabase accountStorage;

    @GoogleUse
    @ConfigurationArgumentError("google-not-enough-arguments")
    @DefaultFor("~")
    @CommandCooldown(CommandCooldown.DEFAULT_VALUE)
    public void linkGoogle(LinkCommandActorWrapper actorWrapper, LinkType linkType, Account account) {
        String rawKey = plugin.getGoogleAuthenticator().createCredentials().getKey();
        String nickname = account.getName();
        String randomCode = "MINECRAFT_" + RandomCodeFactory.generateCode(2);

        String totpKey = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(nickname, randomCode, rawKey);

        LinkUser linkUser = account.findFirstLinkUserOrNew(GoogleLinkType.LINK_USER_FILTER, GoogleLinkType.getInstance());

        if (linkUser.isIdentifierDefaultOrNull()) {
            String rawContent = linkType.getLinkMessages()
                    .getStringMessage("google-generated", linkType.newMessageContext(account))
                    .replaceAll("(?i)%google_key%", rawKey);
            Message googleQRMessage = buildGoogleQRMessage(totpKey, rawContent, linkType);
            actorWrapper.send(googleQRMessage);
        } else {
            String rawContent = linkType.getLinkMessages()
                    .getStringMessage("google-regenerated", linkType.newMessageContext(account))
                    .replaceAll("(?i)%google_key%", rawKey);
            Message googleQRMessage = buildGoogleQRMessage(totpKey, rawContent, linkType);
            actorWrapper.send(googleQRMessage);
        }

        linkUser.getLinkUserInfo().getIdentificator().setString(rawKey);
        accountStorage.updateAccountLinks(account);
    }

    private Message buildGoogleQRMessage(String key, String messageRawContent, LinkType linkType) {
        File temporaryImageFile;
        try {
            temporaryImageFile = File.createTempFile("google-qr-image", ".png");

            BitMatrix matrix = new MultiFormatWriter().encode(key, BarcodeFormat.QR_CODE, 200, 200);

            ImageIO.write(MatrixToImageWriter.toBufferedImage(matrix), "PNG", temporaryImageFile);
        } catch(WriterException | IOException e) {
            e.printStackTrace();
            return null;
        }
        Message message = linkType.newMessageBuilder(messageRawContent).attachFiles(MessengerFile.of(temporaryImageFile)).build();

        temporaryImageFile.deleteOnExit();
        return message;
    }
}
