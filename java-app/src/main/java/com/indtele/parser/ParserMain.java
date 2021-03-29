package com.indtele.parser;

import java.util.List;

public class ParserMain
{
    public static void main(String[] args) throws Exception
    {
        YearDataBulider b = new YearDataBulider(Util.URL_TN2016);
        List<YearDataBulider.Constituency> constituencies = b.listAllConstituencies();

        for (YearDataBulider.Constituency c : constituencies)
        {
            b.candidateForConstituency(c);
            if (true)
                return;
        }

        //constituencies.forEach(System.out::println);
        //b.listCandidates();
    }

}
