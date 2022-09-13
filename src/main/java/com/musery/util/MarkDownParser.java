package com.musery.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.util.Asserts;

@Slf4j
public class MarkDownParser extends NodeJSEnvironment {

  private static volatile MarkDownParser parser;

  private MarkDownParser() {
    super();
  }

  public static MarkDownParser getInstance() {
    if (null == parser) {
      synchronized (MarkDownParser.class) {
        if (null == parser) {
          MarkDownParser markDownParser = new MarkDownParser();
          if (!markDownParser.isSupport()) {
            throw new RuntimeException("there is not Runtime Environment for Node");
          }
          parser = markDownParser;
        }
      }
    }
    return parser;
  }

  public static void parse(String markdown) {
    Asserts.notNull(markdown, "markdown");
    getInstance().start("MarkdownParser.js", r -> {}, e -> {}, "--markdown", markdown);
  }
}
