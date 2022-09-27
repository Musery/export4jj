package com.musery.export.transform.docx;

import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart;

@Slf4j
public class IStyle {

  private static StyleDefinitionsPart definitionsPart;

  public static StyleDefinitionsPart init() {
    if (null == definitionsPart) {
      synchronized (IStyle.class) {
        if (null == definitionsPart) {
          try (InputStream is = IStyle.class.getClassLoader().getResourceAsStream("styles.xml")) {
            definitionsPart = new StyleDefinitionsPart();
            definitionsPart.unmarshal(is);
          } catch (IOException | JAXBException | InvalidFormatException e) {
            log.error("读取样式文件失败");
          }
        }
      }
    }
    return definitionsPart;
  }
}
