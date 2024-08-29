package studio.startapps.chocobo.utils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface StringUtils {

    static String generateRandom() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    static String slugify(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .replaceAll("[^ \\-\\w]", "").trim()
                .replaceAll("\\s+", "-").toLowerCase(Locale.ENGLISH);
    }

    /**
     * Appends an incrementable number at the end of the string
     * @param input
     * @param maxDigits
     * @return
     */
    static String appendNumberedSuffix(String input, int maxDigits) {
        Pattern pattern = Pattern.compile("-[0-9]+$");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            String suffix = matcher.group(0); // There is an existing suffix like "-10" or "-3"
            if (suffix.length() > maxDigits - 1) {
                // if the suffix is longer than the max number of digits, we start again from -1
                return String.format("%s-1", input);
            }
            int number = Integer.parseInt(suffix.substring(1));
            return matcher.replaceFirst(String.format("-%s", number + 1));
        }
        else {
            return String.format("%s-1", input);
        }
    }
}
