package com.musery.util;

import org.apache.hc.core5.util.Asserts;

public class EchartsGenerator extends NodeJSEnvironment {

  private static volatile EchartsGenerator generator;

  private EchartsGenerator() {}

  public static EchartsGenerator getInstance() {
    if (null == generator) {
      synchronized (EchartsGenerator.class) {
        if (null == generator) {
          EchartsGenerator echartsGenerator = new EchartsGenerator();
          if (!echartsGenerator.isSupport()) {
            throw new RuntimeException("there is not Runtime Environment for Node");
          }
          generator = echartsGenerator;
          generator.getPb().environment().put("DEFAULT_WIDTH", "1024");
          generator.getPb().environment().put("DEFAULT_HEIGHT", "512");
        }
      }
    }
    return generator;
  }

  public static void generator(String option) {
    Asserts.notNull(option, "option");
    getInstance().start("EchartsGenerator.js", r -> {}, e -> {}, "--option", option);
  }
}
