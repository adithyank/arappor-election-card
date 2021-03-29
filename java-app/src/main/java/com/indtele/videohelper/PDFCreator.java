package com.indtele.videohelper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.indtele.FTLUtil;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import groovy.json.JsonOutput;
import org.codehaus.groovy.runtime.IOGroovyMethods;
import org.codehaus.groovy.runtime.ProcessGroovyMethods;

import javax.imageio.ImageIO;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieStore;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

public class PDFCreator
{
    public static final long PHOTO_DOWNLOAD_DELAY = 10;

    public static final String APP_HOME = System.getProperty("user.dir");

    public static final String CONSTITUENCY_LIST        = APP_HOME + "/assets/data/constituency-list.txt";
    public static final String CONSTITUENCY_BASE_URL    = "http://election.arappor.org/api/candidates-by-constituency";
    public static final String JSON_STORE_PATH          = APP_HOME + "/assets/data/retrieved-json";
    public static final String CLIENT_JSON_STORE_PATH   = APP_HOME + "/assets/data/clientjson";
    public static final String OUTPUTS_PATH             = APP_HOME + "/outputs";
    public static final String PDF_STORE_PATH           = OUTPUTS_PATH + "/pdf";

    public static final String candidatePhotoDir        = APP_HOME + "/assets/data/retrieved-imgs/candidates";

    public static final String candidateSymbolDir       = APP_HOME + "/assets/data/retrieved-imgs/symbol";

    private static Font TITLEFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static Font redFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.RED);
    private static Font SUBTITLE_FONT = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
    private static Font BOLDFONT_14 = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
    private static Font TAMIL_FONT = SUBTITLE_FONT;

    public PDFCreator() throws Exception
    {
        System.out.println("APP_HOME : "  + APP_HOME);
        BaseFont base = BaseFont.createFont("/usr/share/fonts/truetype/noto/NotoSansTamilUI-Regular.ttf", BaseFont.IDENTITY_H, true);
        //TAMIL_FONT = new Font(base, 14f, Font.NORMAL);
    }

    private Gson gson = new Gson();

    private List<Constituency> resp(boolean remoteCall) throws Exception
    {
        List<String> constituencies = readFile();

        List<Constituency> ret = new ArrayList<>();

        for (int i = 0; i < constituencies.size(); i++)
        {
            String constituency = constituencies.get(i);
            System.out.println((i + 1) + " : Preparing for constituency : " + constituency);

            File target = new File(JSON_STORE_PATH, constituency + ".json");

            InputStream is = null;

            if (remoteCall)
            {
                Thread.sleep(200);

                URL fullUrl = new URL(CONSTITUENCY_BASE_URL + "/" + constituency);
                try
                {
                    while (true)
                    {
                        try
                        {
                            is = fullUrl.openConnection().getInputStream();
                            break;
                        }
                        catch (Exception ex)
                        {
                            if (ex.toString().contains("code: 429")) //too many requests
                            {
                                System.out.println("Sleeping for : " + constituency);
                                Thread.sleep(3000);
                            }
                        }
                    }

                    String text = IOGroovyMethods.getText(is);

                    try (FileWriter fw = new FileWriter(target))
                    {
                        fw.write(JsonOutput.prettyPrint(text));
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    continue;
                }
            }
            else
            {
                //no need to do anything... file already exists
            }

            HashMap map = gson.fromJson(new FileReader(target), HashMap.class);

            List list = (List) map.get("data");
            Constituency cobj = new Constituency(constituency);

            for (int cc = 0; cc < list.size(); cc++)
                cobj.candidates.add(new Candidate(constituency, list.get(cc), cc));

            File clientJsonTarget = new File(CLIENT_JSON_STORE_PATH, constituency + ".json");
            FileWriter fw = new FileWriter(clientJsonTarget);

            fw.write(new GsonBuilder().setPrettyPrinting().create().toJson(cobj));
            fw.close();

            ret.add(cobj);
            //create(cobj);
        }

        return ret;
    }

    private List<String> readFile() throws Exception
    {
        BufferedReader br = new BufferedReader(new FileReader(CONSTITUENCY_LIST));

        List<String> ret = new ArrayList<>();

        br.lines().forEach(ret::add);
        return ret;
    }

    private java.awt.Image getImage(String url) throws Exception
    {
        if (url == null || url.isEmpty())
            return null;

        try
        {
            return ImageIO.read(new URL(url));
        }
        catch (Exception ex)
        {
            System.out.println("Unable to retrieve image from url : " + url + ". " + ex.toString());
        }

        return null;
    }

    private void createText(Constituency constituency) throws Exception
    {
        Map<String, Object> map = new HashMap<>();

        map.put("summary", summaryProps(constituency));

        List<Map> propsList = constituency.candidates.stream().map(this::buildProps).collect(Collectors.toList());

        map.put("candidates", propsList);

        File dir = new File(APP_HOME + "/outputs/txt");

        File txt = new File(dir, constituency.name + ".txt");
        txt.getParentFile().mkdirs();

        FTLUtil.resolveFile("/templates/txthelper.ftl", map, txt);
    }

    private void createHtml(Constituency constituency) throws Exception
    {
        Map<String, Object> map = new HashMap<>();

        map.put("summary", summaryProps(constituency));

        List<Map> propsList = constituency.candidates.stream().map(this::buildProps).collect(Collectors.toList());

        map.put("candidates", propsList);

        File dir = new File(OUTPUTS_PATH + "/pdf/");

        File html = new File(new File(dir, "/html"), constituency.name + ".html");
        File pdf = new File(new File(dir, "/pdf"), constituency.name + ".pdf");

        html.getParentFile().mkdirs();
        pdf.getParentFile().mkdirs();

        FTLUtil.resolveFile("/templates/videoreader.ftl", map, html);

        String cmd = "/softwares/wkhtmltox/bin/wkhtmltopdf " + html.getAbsolutePath() + " " + pdf.getAbsolutePath();
        ProcessGroovyMethods.execute(cmd).waitFor();
    }

    private Map buildProps(Candidate c)
    {
        Map m = new LinkedHashMap();
//            m.put("Photo", getImage(c.photoUrl));
        m.put("Name", c.name);
        m.put("Age", c.age);
        m.put("Party Name", c.partyName);
        m.put("Education", c.education);
        m.put("Occupation", c.occupation);
        m.put("Total Assets", c.totalAsset);
        m.put("Liability", c.liability);
        m.put("Net Asset", c.netAsset);
        m.put("Criminal Cases", c.criminalCase);
        m.put("Case Details", c.caseDetailTamil);
        //m.put("Case Details (English)", c.caseDetailEnglish);
//            m.put("Symbol", getImage(c.symbolUrl));
        m.put("Symbol Name", c.symbolName);
//            m.put("Symbol URL", c.symbolUrl);
//            m.put("Candidate Photo URL", c.photoUrl);

        //String str = StringEscapeUtils.unescapeJava("\u0b9a\u0b9f\u0bcd\u0b9f\u0ba4\u0bcd\u0ba4\u0bbf\u0bb1\u0bcd\u0b95\u0bc1");
        //String str = new String("\u0b9a\u0b9f\u0bcd\u0b9f\u0ba4\u0bcd\u0ba4\u0bbf\u0bb1\u0bcd\u0b95\u0bc1 \u0baa\u0bc1\u0bb1\u0bae".getBytes(StandardCharsets.UTF_8));
        //m.put("Sample Tamil UNICODE", str);

        return m;
    }

    private void create(Constituency constituency) throws Exception
    {
        System.out.println("Starting PDF for constituency [" + constituency.name + "]. candidates : " + constituency.candidates.size());
        Document doc = new Document();

        PdfWriter.getInstance(doc, new FileOutputStream(PDF_STORE_PATH + "/" + constituency.name + ".pdf"));

        doc.open();

        addSummaryPage(constituency, doc);

        for (Candidate c : constituency.candidates)
        {
            Map m = buildProps(c);
            //doc.newPage();
            addEmptyLine(doc, 4);
            addTable(doc, m, TAMIL_FONT);
        }

        doc.close();
    }

    void addSummaryPage(Constituency c, Document doc) throws Exception
    {
        Paragraph title = new Paragraph("Constituency Document", TITLEFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);

        doc.add(title);
        addEmptyLine(doc, 2);

        addTable(doc, summaryProps(c), SUBTITLE_FONT);
    }

    private Map<String, String> summaryProps(Constituency c)
    {
        Map m = new LinkedHashMap();
        m.put("Constituency Name", c.name);
        m.put("No of Candidates", c.noOfCandidates());
        m.put("No of Independent Candidates", c.noOfIndependentCandidates());

        return m;
    }

    void addTable(Document doc, Map map, Font font) throws Exception
    {
        PdfPTable table = new PdfPTable(2);
        for (Object o : map.entrySet())
        {
            Entry e = (Entry) o;

            if (e.getValue() == null)
                continue;

            table.addCell(String.valueOf(e.getKey()));
            //table.addCell(String.valueOf(e.getValue()));
            table.addCell(new Paragraph(String.valueOf(e.getValue()), font));
            //table.addCell(new Paragraph(new String(String.valueOf(e.getValue()).getBytes(StandardCharsets.ISO_8859_1))));
        }

        doc.add(table);
    }

    private static void addEmptyLine(Document doc, int number) throws Exception
    {
        for (int i = 0; i < number; i++)
            doc.add(new Paragraph(" "));
    }

    public void downloadAll() throws Exception
    {
        List<Constituency> cs = resp(true);
        downloadImages(cs);
    }

    void printCounts(List<Constituency> cs)
    {
        long count = cs.stream().mapToInt(Constituency::noOfCandidates).sum();

        System.out.println("Total candidates : " + count);
    }

    protected void downloadImages(List<Constituency> cs) throws Exception
    {
        Set<String> photoUrls = new HashSet<>();
        Set<String> symbolUrls = new HashSet<>();

        for (Constituency c : cs)
        {
            c.candidates.stream().map(o -> o.symbolUrl).forEach(symbolUrls::add);
            c.candidates.stream().map(o -> o.photoUrl).forEach(photoUrls::add);
        }

        System.out.println("Download : photo count : " + photoUrls.size());
        System.out.println("Download : symbol count : " + symbolUrls.size());
        System.out.println("Download : Total count : " + (photoUrls.size() + symbolUrls.size()));

        int i = 0;
        for (String photoUrl : photoUrls)
        {
            try
            {
                Thread.sleep(PHOTO_DOWNLOAD_DELAY);

                boolean downloaded = Util.writeUrlToFile(photoUrl, candidatePhotoDir, true);
                System.out.println(++i + "/" + photoUrls.size() + " : downloaded :" + downloaded + ", from photo url... " + photoUrl);
            }
            catch (Exception ex)
            {
                System.err.println(ex.toString());
            }
        }

        i = 0;
        for (String symbolUrl : symbolUrls)
        {
            try
            {
                Thread.sleep(PHOTO_DOWNLOAD_DELAY);
                boolean downloaded = Util.writeUrlToFile(symbolUrl, candidateSymbolDir, true);
                System.out.println(++i + "/" + symbolUrls.size() + " : downloaded :" + downloaded + ", from photo url... " + symbolUrl);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        System.out.println("Download : photo count : " + photoUrls.size());
        System.out.println("Download : symbol count : " + symbolUrls.size());
        System.out.println("Download : Total count : " + (photoUrls.size() + symbolUrls.size()));

        System.out.println("DONE DONE DONE. ALL DOWNLOADS OVER");
    }

    public static class Constituency
    {
        public String name;
        public List<Candidate> candidates = new ArrayList<>();

        public Constituency(String name)
        {
            this.name = name;
        }

        public int noOfCandidates()
        {
            return candidates.size();
        }

        public int noOfIndependentCandidates()
        {
            return (int) candidates.stream().filter(Candidate::isIndependent).count();
        }
    }

    public static class Candidate
    {
        public String recid;
        public String name;
        public String nameTamil;
        public String constituency;
        public String constituencyTamil;
        public String district;
        public String districtTamil;
        public String age;
        public String partyName;
        public String partyNameTamil;
        public String education;
        public String educationTamil;
        public String occupation;
        public String occupationTamil;
        public String totalAsset;
        public String totalAssetTamil;
        public String liability;
        public String liabilityTamil;
        public String netAsset;
        public String netAssetTamil;
        public String criminalCase;
        public String caseDetailEnglish;
        public String caseDetailTamil;
        public String symbolName; //name portion
        public String symbolUrl;
        public String photoUrl;
        public String photoName;
        public String soSymbolUrl;
        public String soPhotoUrl;

        Candidate(String constituency, Object o, int i) throws Exception
        {
            Map<String, Object> map = (Map<String, Object>) o;
            Map<String, Object> en = (Map<String, Object>) map.get("en");
            Map<String, Object> ta = (Map<String, Object>) map.get("ta");

            //en.entrySet().forEach(System.out::println);

            this.constituency = constituency;
            this.constituencyTamil = str(ta, "thokuthi", "constituency_ta_label");
            this.district = str(en, "thokuthi", "district_en_label");
            this.districtTamil = str(en, "thokuthi", "district_ta_label");
            recid        = str(en, "candidate_id");
            name         = str(en, "name");
            nameTamil    = str(ta, "name");
            age          = str(en, "age");
            partyName    = str(en, "party", "en_label");
            partyNameTamil    = str(ta, "party", "ta_label");
            education    = str(en, "education");
            educationTamil    = str(ta, "education");
            occupation   = str(en, "occupation");
            occupationTamil   = str(ta, "occupation");
            totalAsset   = Util.getFriendlyAmount(str(en, "total_assets"), "en");
            totalAssetTamil   = Util.getFriendlyAmount(str(ta, "total_assets"), "ta");
            liability    = Util.getFriendlyAmount(str(en, "liabilities"), "en");
            liabilityTamil    = Util.getFriendlyAmount(str(ta, "liabilities"), "ta");
            netAsset     = Util.getFriendlyAmount(str(en, "net_assets"), "en");
            netAssetTamil     = Util.getFriendlyAmount(str(ta, "net_assets"), "ta");
            criminalCase = "" + Double.valueOf(str(en, "no_of_criminal_cases")).intValue();
            caseDetailEnglish   = str(en, "cases_description");
            caseDetailTamil   = str(ta, "cases_description");
            //System.out.println("case tamil : " + caseDetailTamil);
            symbolUrl         = str(en, "party", "party_image");
            photoUrl          = str(en, "candidate_image");


            if (photoUrl != null && !photoUrl.isEmpty() && !photoUrl.equals("null"))
            {
                String fn = new File(new URL(photoUrl).getFile()).getName();
                soPhotoUrl        = "assets/data/retrieved-imgs/candidates/" + fn;
                int dotIndex = fn.indexOf(".");

                if (dotIndex != -1)
                    photoName = fn.substring(0, dotIndex);
            }

            if (symbolUrl != null && !symbolUrl.isEmpty() && !symbolUrl.equals("null"))
            {
                try
                {
                    String f = new File(new URL(symbolUrl).getFile()).getName();
                    symbolName = f.substring(0, f.indexOf("."));
                    soSymbolUrl       = "assets/data/retrieved-imgs/symbol/" + new File(new URL(symbolUrl).getFile()).getName();
                }
                catch (Exception ex)
                {
                    System.out.println("Exception: invalid symbolUrl : " + symbolUrl + " for candidate name : " + name);
                }
            }

        }

        String str(Map map, String... keys)
        {
            Object val = map;

            for (String key : keys)
                val = ((Map) val).get(key);

            return String.valueOf(val);
        }

        boolean isIndependent()
        {
            return "Independent".equals(partyName);
        }
    }

    private void preparePDF() throws Exception
    {
        List<Constituency> cs = resp(false);

        for (int i = 0; i < cs.size(); i++)
        {
            Constituency c = cs.get(i);
            System.out.println(i + "/" + cs.size() + " : Preparing PDF for : " + c.name);
//            createHtml(c);
            //create(c);
            createText(c);
//            break;
        }

    }

    public static void main(String[] args) throws Exception
    {
        long s = System.currentTimeMillis();
        PDFCreator creator = new PDFCreator();
//        List<Constituency> resp = creator.resp(false);
//        creator.downloadImages(creator.resp(false));
//        System.out.println(resp.stream().mapToInt(Constituency::noOfCandidates).sum());
//        creator.downloadAll();
//            creator.preparePDF();

//        creator.printCounts(creator.resp(false));

        long e = System.currentTimeMillis();
        System.out.println("Time taken = " + (e - s) / 1000 + " sec");
        System.out.println("DONE");
    }
}
