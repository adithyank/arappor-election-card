package com.indtele.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Util
{
    public static final String URL_TN2016 = "https://myneta.info/tamilnadu2016/";

    public static String fullUrlForRelative(String relativeUrl)
    {
        return URL_TN2016 + relativeUrl;
    }

    public static Document getResponse(String... urls) throws Exception
    {
        String url = String.join("", urls);
        return Jsoup.connect(url).timeout(10000).get();
    }

    public static String getResponseText(String... urls) throws Exception
    {
        return getResponse(urls).outerHtml();
    }

    public static String escapeHtml(String raw)
    {
        return raw.replaceAll("&nbsp;", "");
    }
}
