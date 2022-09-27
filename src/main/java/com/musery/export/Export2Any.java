package com.musery.export;

import com.musery.export.transform.docx.DOCX4TR;
import com.musery.export.transform.docx.IStyle;
import com.musery.parse.AST;
import com.musery.parse.MarkDownParser;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;

@Slf4j
public class Export2Any {

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
        ast -> {
          if (option.getFormat().equals(Format.PDF)) {
            export2PDF(option, ast);
          } else {
            export2DOCX(option, ast);
          }
        },
        eMsg -> log.error("export fail in `parse` step, because of {}", eMsg));
  }

  private static void export2DOCX(ExportOption option, AST ast) {
    try {
      WordprocessingMLPackage docx = WordprocessingMLPackage.createPackage();
      // word/document.xml
      MainDocumentPart document = new MainDocumentPart();
      // add relationship
      docx.addTargetPart(document);
      // add styles
      docx.addTargetPart(IStyle.init());
      //  prepare
      ExportOption.prepare(docx);
      // 开始解析成WORD
      DOCX4TR.start(ast);
      // 保存到文档
      docx.save(option.output());
    } catch (Docx4JException e) {
      throw new RuntimeException("To Word Error", e);
    } finally {
      ExportOption.finished();
    }
  }

  private static void export2PDF(ExportOption option, AST ast) {}
}
