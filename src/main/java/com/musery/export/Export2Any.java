package com.musery.export;

import com.musery.export.transform.Root;
import com.musery.export.transform.Starter;
import com.musery.parse.AST;
import com.musery.parse.MarkDownParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.io.FileOutputStream;
import java.util.Objects;

@Slf4j
public class Export2Any {
  private static Starter STARTER = new Root();

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
        option.tmp(),
        ast -> export2(option, ast),
        eMsg -> log.error("export fail in `parse` step, because of {}", eMsg));
  }

  private static void export2(ExportOption option, AST ast) {
    WordprocessingMLPackage docx = STARTER.start(ast, option);
    switch (option.getFormat()) {
      case PDF:
        try {
          Docx4J.toPDF(docx, new FileOutputStream(option.output()));
        } catch (Exception e) {
          throw new RuntimeException("To PDF Error", e);
        }
        break;
      case HTML:
        try {
          HTMLSettings htmlSettings = Docx4J.createHTMLSettings();
          htmlSettings.setOpcPackage(docx);
          Docx4J.toHTML(
              htmlSettings,
              new FileOutputStream(option.output()),
              Docx4J.FLAG_EXPORT_PREFER_NONXSL);
        } catch (Exception e) {
          throw new RuntimeException("To HTML Error", e);
        }
        break;
      default:
        try {
          docx.save(option.output());
        } catch (Docx4JException e) {
          throw new RuntimeException("To Word Error", e);
        }
        break;
    }
  }
}
