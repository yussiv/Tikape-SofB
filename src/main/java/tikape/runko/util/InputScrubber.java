
package tikape.runko.util;

import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;

public final class InputScrubber {

    public static String clean(String string) {
        return new HtmlToPlainText().getPlainText(Jsoup.parse(string)).trim();
    }
}
