package com.musery.util;

import com.musery.ExportOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

/** export docx with docx4j */
@Slf4j
public class Export2Docx {

  public static void export(ExportOption option) {
    if (StringUtils.isBlank(option.getOutput())) {
      // path is current workspace resource
      option.setOutput(Export2Docx.class.getResource("/").getPath());
    }
    if (StringUtils.isBlank(option.getName())) {
      option.setName(
          String.format(
              "Export2Docx-%s",
              LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHH:mm:ss.SSS"))));
    }
    try {
      WordprocessingMLPackage wordprocessingMLPackage = WordprocessingMLPackage.createPackage();

      // save
      wordprocessingMLPackage.save(option.output());
    } catch (InvalidFormatException exception) {
      log.error("Create DOCX with exception about", exception);
    } catch (Docx4JException exception) {
      log.error("Save DOCX with exception about", exception);
    }
  }
}
