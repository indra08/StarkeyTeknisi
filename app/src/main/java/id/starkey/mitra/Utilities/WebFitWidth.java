package id.starkey.mitra.Utilities;

/**
 * Created by Dani on 8/16/2018.
 */

public class WebFitWidth {
    public WebFitWidth() {
    }

    public final String changedHeaderHtml(String htmlText) {
        String head = "<head><meta name=\"viewport\" content=\"width=device-width, user-scalable=yes\" /><style>img {width: 100%;height: auto;}</style></head><body style='margin:0;padding:0;color:#636363;font-size:1em'>";
        String closedTag = "</body></html>";
        String changeFontHtml = head + htmlText + closedTag;
        return changeFontHtml;
    }
}
