package com.hradecek.jenkins.reporter.freemarker;

import freemarker.cache.TemplateLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * <p>Make a freemarker template loader works with absolute paths.
 *
 * <p>You can use this in case of there's need to load template outside working directory, given on absolute path
 *
 * @author <a href="mailto:ivohradek@gmail.com">Ivo Hradek</a>
 */
public class AbsolutePathTemplateLoader implements TemplateLoader {

    @Override
    public Object findTemplateSource(String templatePath) throws IOException {
        File template = new File("/" + templatePath);

        return template.isFile() ? template : null;
    }

    @Override
    public long getLastModified(Object template) {
        return ((File) template).lastModified();
    }

    @Override
    public Reader getReader(Object template, String encoding) throws IOException {
        if (!(template instanceof File)) {
            throw new IllegalArgumentException("Template is a " + template.getClass().getName());
        }

        return new InputStreamReader(new FileInputStream((File) template), encoding);
    }

    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {
        // Nothing to do
    }
}
