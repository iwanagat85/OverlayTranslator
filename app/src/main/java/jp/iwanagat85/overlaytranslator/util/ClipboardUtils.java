package jp.iwanagat85.overlaytranslator.util;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;

public class ClipboardUtils {

    public static void putToClipboard(Context context, String data) {
        String[] mimeType = new String[]{ClipDescription.MIMETYPE_TEXT_URILIST};
        ClipDescription description = new ClipDescription("text_data", mimeType);
        ClipData.Item item = new ClipData.Item(data);
        ClipData clipData = new ClipData(description, item);

        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(clipData);
    }

}
