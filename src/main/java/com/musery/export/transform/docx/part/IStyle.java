package com.musery.export.transform.docx.part;

import cn.hutool.core.collection.CollectionUtil;
import jakarta.xml.bind.JAXBException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart;
import org.docx4j.wml.CTTblStylePr;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.PPr;
import org.docx4j.wml.RPr;
import org.docx4j.wml.Style;
import org.docx4j.wml.Style.BasedOn;
import org.docx4j.wml.Style.Name;
import org.docx4j.wml.TblPr;

@Slf4j
public class IStyle {

  private static StyleDefinitionsPart definitionsPart;

  public static StyleDefinitionsPart init() {
    if (null == definitionsPart) {
      synchronized (IStyle.class) {
        if (null == definitionsPart) {
          try {
            definitionsPart = new StyleDefinitionsPart();
            definitionsPart.unmarshalDefaultStyles();
          } catch (JAXBException | InvalidFormatException e) {
            log.error("Build Style Error", e);
          }
        }
      }
    }
    return definitionsPart;
  }

  public static Style customPStyle(String id, String name, PPr pPr, RPr rPr) {
    return customPStyle(
        id, name, pPr, rPr, definitionsPart.getDefaultParagraphStyle().getStyleId());
  }

  public static Style customPStyle(String id, String name, PPr pPr, RPr rPr, String baseOn) {
    return customStyle("paragraph", id, name, baseOn, pPr, rPr, null, null);
  }

  public static Style customCStyle(String id, String name, RPr rPr) {
    return customCStyle(id, name, rPr, definitionsPart.getDefaultCharacterStyle().getStyleId());
  }

  public static Style customCStyle(String id, String name, RPr rPr, String baseOn) {
    return customStyle("character", id, name, baseOn, null, rPr, null, null);
  }

  public static Style customTStyle(
      String id, String name, TblPr tblPr, List<CTTblStylePr> tblStylePr) {
    return customStyle(
        "table",
        id,
        name,
        definitionsPart.getDefaultTableStyle().getStyleId(),
        null,
        null,
        tblPr,
        tblStylePr);
  }

  public static Style customStyle(
      String type,
      String id,
      String name,
      String baseOn,
      PPr pPr,
      RPr rPr,
      TblPr tblPr,
      List<CTTblStylePr> ctTblStylePrs) {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    Style style = objectFactory.createStyle();
    style.setType(type);
    style.setStyleId(id);
    Name pName = objectFactory.createStyleName();
    pName.setVal(name);
    style.setName(pName);
    BasedOn basedOn = objectFactory.createStyleBasedOn();
    basedOn.setVal(baseOn);
    style.setBasedOn(basedOn);
    style.setPPr(pPr);
    style.setRPr(rPr);
    style.setTblPr(tblPr);
    if (CollectionUtil.isNotEmpty(ctTblStylePrs)) {
      style.getTblStylePr().addAll(ctTblStylePrs);
    }
    definitionsPart.getJaxbElement().getStyle().add(style);
    return style;
  }
}
