package com.indtele.videohelper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExcelWriter
{
    public static void write(List<Map> cs)
    {

    }

    private static Map<String, String> mapForOneCandidate(Map input)
    {
        Map<String, String> map = new HashMap<>();




        return map;
    }

    private static Map prepareLangMap(Map langMap)
    {
        Map ret = new LinkedHashMap();

        ret.putAll(langMap);

        Map partyMap = (Map) ret.remove("party");
        for (Object o : partyMap.entrySet())
        {
            Map.Entry e = (Map.Entry) o;
            ret.put("party." + e.getKey(), e.getValue());
        }

        Map<String, String> thokuthiMap = (Map<String, String>) ret.remove("thokuthi");
        for (Map.Entry<String, String> e : thokuthiMap.entrySet())
            ret.put("thokuthi." + e.getKey(), e.getValue());

        List<Map<String, String>> holdings = (List<Map<String, String>>) ret.remove("holdings");

        for (Map<String, String> holding : holdings)
        {
            String k = "holdings-" + holding.get("y_from") + "-" + holding.get("y_to");

            String col = k + "-" ;
        }



        return ret;

    }

}
