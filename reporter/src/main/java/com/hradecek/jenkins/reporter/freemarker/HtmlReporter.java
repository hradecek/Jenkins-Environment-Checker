package com.hradecek.jenkins.reporter.freemarker;

import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import com.hradecek.jenkins.reporter.api.Reporter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Generates report in HTML format based on master template using freemarker.
 *
 * @author <a href="mailto:ivohradek@gmail.com">Ivo Hradek</a>
 */
public class HtmlReporter implements Reporter {

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(HtmlReporter.class.getName());

    /**
     * Master template
     */
    private static final String masterTemplate = "master.ftl";

    /**
     * Classpath templates' path
     */
    private static final String basePackagePath = "META-INF/templates/";

    /**
     * Initial configuration containing encoding, template-loader etc.
     */
    private final Configuration config = initConfiguration();

    /**
     * Template used for report
     */
    private final File template;

    /**
     * Writer, which creates final report
     */
    private final Writer writer;

    /**
     * Constructor
     *
     * @param writer - writer
     * @param template - path to report freemarker template
     */
    public HtmlReporter(FileWriter writer, File template) {
        this.template = template;
        this.writer = writer;
    }

    private Configuration initConfiguration() {
        Configuration config = new Configuration(Configuration.getVersion());
        config.setDefaultEncoding("UTF-8");
        config.setClassLoaderForTemplateLoading(this.getClass().getClassLoader(), basePackagePath);

        return config;
    }

    /**
     * {@inheritDoc}
     */
    public void report(String json) {
        Map<String, String> values;
        try {
            values = new ObjectMapper().readValue(json, Map.class);
            values.put("reportTemplate", template.toString());
            System.out.println(values);

            config.getTemplate(masterTemplate).process(values, writer);
            log.info("Report (HTML) generated");
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

