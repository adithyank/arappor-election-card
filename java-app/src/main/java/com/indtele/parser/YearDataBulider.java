package com.indtele.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class YearDataBulider
{
    private String baseURL;

    public YearDataBulider(String baseURL)
    {
        this.baseURL = baseURL;
    }

    public void listCandidates() throws Exception
    {
        String url = "index.php?action=summary&subAction=candidates_analyzed&sort=candidate";
        Document resp = Util.getResponse(Util.URL_TN2016, url);

        //System.out.println(resp);


        //if (true)
          //  return;

        Elements trs = resp.select("table").get(2).select("tr");

        for (int r = 1; r < trs.size(); r++)
        {
            CandidateTableRow row = new CandidateTableRow();

            Element tr = trs.get(r);
            Elements tds = tr.select("td");
            System.out.println("r = " + r + ", tds = " + tds.size());

            if (tds.size() > 0)
                System.out.println(tds.get(0).text() + " --- " + tds.get(1).text());

            System.out.println("---------------------------");

            if (true)
                continue;

            //System.out.println(r + " : " + tr.text() + "--------" + trs.get(r).child(1).text());
            //System.out.println(r + " : " + tr);


            row.put("candidateName", tds.get(1).text());
            row.put("constituency", tds.get(2).text());
            row.put("party", tds.get(3).text());
            row.put("criminalCase", tds.get(4).text());
            row.put("education", tds.get(5).text());
            row.put("totalAssets", tds.get(6).text());
            row.put("liabilities", tds.get(7).text());

            System.out.println(r + " : " + row);
        }

    }

    public List<Constituency> listAllConstituencies() throws Exception
    {
        Document doc = Jsoup.connect(baseURL).get();

        Elements tables = doc.select("table");

        Elements items = tables.get(2).select(".items a");

        List<Constituency> ret = new ArrayList<>();

        for (Element item : items)
        {
            String href = item.attr("href");
            if (!href.startsWith("http"))
                href = baseURL + href;

            Constituency c = new Constituency(item.text(), href);
            ret.add(c);
        }

        return ret;
    }

    public void candidateForConstituency(Constituency constituency) throws Exception
    {
        Document resp = Util.getResponse(constituency.href);

        Elements trs = resp.select("table").get(2).select("tr");

        for (int r = 0; r < trs.size(); r++)
        {
            Element row = trs.get(r);
            Elements tds = row.select("td");

            if (tds.isEmpty())
                continue;

            ConstituencyCandidate c = new ConstituencyCandidate();
            constituency.candidates.add(c);

            c.put("name", tds.get(0).select("a").get(0).text());
            c.put("party", tds.get(1).text());
            c.put("criminalCases", tds.get(2).text());
            c.put("education", tds.get(3).text());
            c.put("age", tds.get(4).text());

            c.put("totalAssetsFull", Util.escapeHtml(tds.get(5).childNode(0).toString()));
            c.put("totalAssetsApprox", Util.escapeHtml(tds.get(5).childNode(2).childNode(0).toString()));
            c.put("liabilitiesFull", Util.escapeHtml(tds.get(6).childNode(0).toString()));
            c.put("liabilitiesApprox", Util.escapeHtml(tds.get(6).childNode(2).childNode(0).toString()));

            //System.out.println(String.join("|", c.values()));
            System.out.println(c);
        }
    }

    public static class ConstituencyCandidate extends LinkedHashMap<String, String>
    {

    }

    public static class Constituency
    {
        public String label;
        public String href;

        public List<ConstituencyCandidate> candidates = new ArrayList<>();

        public Constituency(String label, String href)
        {
            this.label = label;
            this.href = href;
        }

        @Override
        public String toString()
        {
            return "Constituency{label='" + label + "', href='" + href + "'}";
        }
    }

    public static class Candidate
    {
        public CandidateTableRow tableRow;
    }

    public static class CandidateTableRow extends HashMap<String, String>
    {

    }


}
