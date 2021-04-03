package com.indtele.videohelper;

import java.util.Arrays;

public class AMain
{
    public static void main(String[] args) throws Exception
    {
        System.out.println("Main Entry!");
        System.out.println("args = " + Arrays.toString(args));

        if (args.length == 0)
        {
            System.out.println("args not given. Exiting...");
            System.exit(0);
        }

        String cmd = args[0];

        if (cmd.equals("downloadSourceData"))
        {
            PDFCreator c = new PDFCreator();
            c.downloadAll();
        }
        else if (cmd.equals("createTextFiles"))
        {
            PDFCreator c = new PDFCreator();
            c.prepareVideoHelperDoc();
        }
        else if (cmd.equals("printCounts"))
        {
            PDFCreator c = new PDFCreator();
            c.printCounts(c.resp(false));
        }
    }
}

