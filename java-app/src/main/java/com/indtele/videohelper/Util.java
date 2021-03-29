package com.indtele.videohelper;

import org.apache.commons.lang.StringEscapeUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Util
{
    public static String getFriendlyAmount(String amount, String lang)
    {
        amount = amount.replace(",", "");
        return getFriendlyAmount(Double.valueOf(amount).longValue(), lang);
    }

    public static String getFriendlyAmount(long amount, String lang)
    {
        if (amount == 0)
        {
            return lang.equals("en") ? "No" : "\u0B87\u0BB2\u0BCD\u0BB2\u0BC8";
        }

        String str = "" + amount;

        if (str.length() <= 5)
            return str;
        else if (str.length() == 6) //456789
            return getFormatted(str, 1, false, lang);
        else if (str.length() == 7) //1234567
            return getFormatted(str, 2, false, lang);
        else // > 7 : 1234,56,78,909
            return getFormatted(str, str.length() - 7, true, lang);
    }

    private static String getFormatted(String chars, int leftChars, boolean crore, String lang)
    {
        String croreString = lang.equals("en") ? "crore" : "\u0B95\u0BCB\u0B9F\u0BBF";
        String lacString   = lang.equals("en") ? "lacs" : "\u0BB2\u0B9F\u0BCD\u0B9A\u0BAE\u0BCD";

        String left = chars.substring(0, leftChars);
        String right = chars.substring(leftChars, leftChars + 2);
        String res = left + "." + right;

        if (!crore)
            return "" + ((int) Math.round(Double.parseDouble(res))) + " " + lacString;
        else
            return left + " " + croreString + " " + right + " " + lacString;
    }

    public static boolean writeUrlToFile(String urlString, String parentDir, boolean downloadOnlyIfTargetFileDoesNotExist) throws Exception
    {
        if (urlString == null || urlString.isEmpty() || urlString.equals("null"))
            return false;

        URL url = new URL(urlString);

        Path fileName = Paths.get(url.getFile()).getFileName();

        File target = new File(parentDir, fileName.toString());

        if (downloadOnlyIfTargetFileDoesNotExist)
        {
            if (target.exists())
                return false;
        }

        writeInputStreamToFile(url.openStream(), target);
        return true;
    }

    public static void writeInputStreamToFile(InputStream is, File target) throws IOException
    {
        java.nio.file.Files.copy(
                is,
                target.toPath(),
                StandardCopyOption.REPLACE_EXISTING);

        try {
            is.close();
        }
        catch (Exception ex)
        {
        }
    }
}
