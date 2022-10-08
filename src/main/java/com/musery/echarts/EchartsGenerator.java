package com.musery.echarts;

import com.musery.NodeJSEnvironment;
import java.util.Objects;
import java.util.function.Consumer;
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

  public static void generator(String option, Consumer<String> afterBuild, Consumer<String> err) {
    if (Objects.isNull(option)) {
      throw new IllegalArgumentException("option must not be null");
    }
    getInstance()
        .start(
            "EchartsGenerator.js",
            str -> {
              if (null != afterBuild) {
                afterBuild.accept(str);
              }
            },
            errMsg -> {
              if (null != err) {
                err.accept(errMsg);
              }
            },
            "--option",
            option);
  }
}