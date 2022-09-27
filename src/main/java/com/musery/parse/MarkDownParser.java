package com.musery.parse;

import com.musery.NodeJSEnvironment;
import com.musery.util.JacksonUtils;
import java.util.Objects;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

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

  public static void parse(String markdown, Consumer<AST> afterParse, Consumer<String> err) {
    if (Objects.isNull(markdown)) {
      throw new IllegalArgumentException("markdown must not be null");
    }
    getInstance()
        .start(
            "MarkdownParser.js",
            astString -> {
              if (null != afterParse) {
                AST ast = JacksonUtils.toObject(astString, AST.class);
                afterParse.accept(ast);
              }
            },
            errMsg -> {
              if (null != err) {
                err.accept(errMsg);
              }
            },
            "--markdown",
            markdown);
  }
}
