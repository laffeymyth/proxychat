package net.laffeymyth.proxychat.factory;

import net.laffeymyth.localization.commons.service.ComponentLocalizationService;
import net.laffeymyth.localization.commons.service.LocalizationMessageSource;
import net.laffeymyth.localization.commons.service.MessageParser;
import net.laffeymyth.proxychat.ProxyChat;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class LocalizationFactory {

    public static void initLocalization() {
        LocalizationMessageSource ru = new LocalizationMessageSource();

        MessageParser messageParser = new MessageParser();

        messageParser.parse(ru, readerFromFileName());

        ComponentLocalizationService.lang().getLanguageMap().put("ru", ru);
    }

    private static Reader readerFromFileName() {
        return new InputStreamReader(Objects.requireNonNull(ProxyChat.class.getResourceAsStream("/config.json")), StandardCharsets.UTF_8);
    }
}
