package com.gearpc.media.util;

import java.util.Map;
import java.util.Set;

public final class ImageFilePolicy {
    private ImageFilePolicy() {}

    public static final Map<String, Set<String>> ALLOWED_CONTENT_TYPES = Map.of(
            "image/jpeg", Set.of("jpg", "jpeg"),
            "image/png", Set.of("png")
    );

    public static boolean isAllowedContentType(String contentType) {
        return contentType != null
                && ALLOWED_CONTENT_TYPES.containsKey(contentType);
    }
}
