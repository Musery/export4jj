package com.musery.echarts;

import com.musery.NodeJSEnvironment;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
    if (Objects.isNull(option)) {
      throw new IllegalArgumentException("option must not be null");
    }
    getInstance()
        .start(
            "EchartsGenerator.js",
            r -> {},
            e -> log.error("generator [{}] with ", option, e),
            "--option",
            option);
  }
}
