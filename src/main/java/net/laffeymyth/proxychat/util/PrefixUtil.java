package net.laffeymyth.proxychat.util;

import net.kyori.adventure.text.minimessage.tag.TagPattern;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.laffeymyth.localization.commons.service.ComponentLocalizationService;
import net.laffeymyth.localization.commons.util.ComponentResolver;

import java.util.function.Function;

public class PrefixUtil {
    public static final Function<String, TagResolver> PREFIX_RESOLVER = PrefixUtil::prefixResolver;

    private static TagResolver prefixResolver(@TagPattern String prefixKey) {
        return ComponentResolver.tag("prefix", (argumentQueue, context) -> ComponentLocalizationService.lang().getMessage(prefixKey + "_prefix", "ru"));
    }
}
