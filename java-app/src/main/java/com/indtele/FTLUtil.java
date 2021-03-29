package com.indtele;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;

public class FTLUtil
{
    private static Configuration FTLCFG;

    static
    {
        init();
    }

    public static void resolveFile(String templatePath, Map<String, Object> model, File target) throws Exception
    {
        Template template = FTLCFG.getTemplate(templatePath);

        try(Writer fileWriter = new FileWriter(target))
        {
            template.process(model, fileWriter);
        }
    }

    private static void init()
    {
        // 1. Configure FreeMarker
        //
        // You should do this ONLY ONCE, when your application starts,
        // then reuse the same Configuration object elsewhere.

        Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);

        // Where do we load the templates from:
        //cfg.setClassForTemplateLoading(FTLUtil.class, "templates");
        cfg.setTemplateLoader(new ClassTemplateLoader(FTLUtil.class.getClassLoader(), ""));
        // Some other recommended settings:
        cfg.setIncompatibleImprovements(new Version(2, 3, 20));
        //cfg.setDefaultEncoding("UTF-8");
        //cfg.setLocale(Locale.US);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        FTLCFG = cfg;
    }
}
