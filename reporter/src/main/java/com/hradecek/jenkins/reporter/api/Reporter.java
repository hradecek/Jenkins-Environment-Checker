package com.hradecek.jenkins.reporter.api;

/**
 * Creates report based on JSON string representation.
 *
 * @author <a href="mailto:ivohradek@gmail.com">Ivo Hradek</a>
 */
public interface Reporter {

    /**
     * Create final report.
     *
     * @param json report
     */
    void report(String json);

    /**
     * Close all opened streams.
     */
    default void close() { }
}
