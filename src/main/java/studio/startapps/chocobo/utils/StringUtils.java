package studio.startapps.chocobo.utils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;

public interface StringUtils {

    static String generateRandom() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    static String slugify(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .replaceAll("[^ \\w]", "").trim()
                .replaceAll("\\s+", "-").toLowerCase(Locale.ENGLISH);
    }
}
