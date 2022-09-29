package com.musery.export;

import com.musery.export.transform.Starter;
import com.musery.export.transform.docx.Root;
import com.musery.parse.AST;
import com.musery.parse.MarkDownParser;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class Export2Any {
  private static Starter DOCX_STARTER = new Root();

  public static void export(ExportOption option, String markdown) {
    if (Objects.isNull(option)) {
      throw new IllegalArgumentException("option must not be null");
    }
    if (StringUtils.isBlank(option.getOutput())) {
      throw new IllegalArgumentException("option[output] must not be blank");
    }
    if (StringUtils.isBlank(option.getName())) {
      throw new IllegalArgumentException("option[name] must not be blank");
    }
    if (Objects.isNull(option.getFormat())) {
      throw new IllegalArgumentException("option[format] must not be null");
    }
    if (StringUtils.isBlank(markdown)) {
      throw new IllegalArgumentException("markdown must not be blank");
    }
    MarkDownParser.parse(
        markdown,
        ast -> export2(option, ast),
        eMsg -> log.error("export fail in `parse` step, because of {}", eMsg));
  }

  private static void export2(ExportOption option, AST ast) {
    switch (option.getFormat()) {
      case PDF:
        break;
      case DOCX:
        DOCX_STARTER.start(ast, option);
        break;
      default:
        break;
    }
  }
}
