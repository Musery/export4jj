package com.musery.export.transform.part;

import lombok.extern.slf4j.Slf4j;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.FontTablePart;

@Slf4j
public class CFontTable {

  public static void init(WordprocessingMLPackage docx) {
    try {
      FontTablePart fontTablePart = new FontTablePart();
      docx.getMainDocumentPart().addTargetPart(fontTablePart);
      fontTablePart.unmarshalDefaultFonts();
    } catch (Exception e) {
      log.error("init font table error ", e);
    }
  }
}
