package com.musery.export.transform.part;

import cn.hutool.core.collection.CollectionUtil;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart;
import org.docx4j.wml.*;
import org.docx4j.wml.Style.BasedOn;
import org.docx4j.wml.Style.Name;

import java.math.BigInteger;
import java.util.List;

@Slf4j
public class CStyle {

  private static StyleDefinitionsPart definitionsPart;

  private static RFonts simsum;

  static {
    try {
      definitionsPart = new StyleDefinitionsPart();
      definitionsPart.unmarshalDefaultStyles();
      ObjectFactory objectFactory = Context.getWmlObjectFactory();
      simsum = objectFactory.createRFonts();
      simsum.setEastAsia("SimSun");
      simsum.setAscii("SimSun");
      simsum.setHAnsi("SimSun");
      RPr rPr = objectFactory.createRPr();
      rPr.setRFonts(simsum);

      Style characterStyle = definitionsPart.getDefaultCharacterStyle();
      characterStyle.setRPr(rPr);

      Style paragraphStyle = definitionsPart.getDefaultParagraphStyle();
      paragraphStyle.setRPr(rPr);

      // 设置目录
      PPr pPr = objectFactory.createPPr();
      pPr.setKeepNext(objectFactory.createBooleanDefaultTrue());
      pPr.setKeepLines(objectFactory.createBooleanDefaultTrue());
      PPrBase.NumPr numPr = objectFactory.createPPrBaseNumPr();
      PPrBase.NumPr.NumId numId = objectFactory.createPPrBaseNumPrNumId();
      numId.setVal(BigInteger.ZERO);
      numPr.setNumId(numId);
      pPr.setNumPr(numPr);
      PPrBase.Spacing spacing = objectFactory.createPPrBaseSpacing();
      spacing.setBefore(BigInteger.valueOf(480L));
      spacing.setAfter(BigInteger.ZERO);
      pPr.setSpacing(spacing);
      PPrBase.OutlineLvl lvl = objectFactory.createPPrBaseOutlineLvl();
      lvl.setVal(BigInteger.valueOf(9L));
      pPr.setOutlineLvl(lvl);

      RPr mRpr = objectFactory.createRPr();
      mRpr.setRFonts(simsum);
      mRpr.setB(objectFactory.createBooleanDefaultTrue());
      mRpr.setBCs(objectFactory.createBooleanDefaultTrue());
      HpsMeasure hpsMeasure = objectFactory.createHpsMeasure();
      hpsMeasure.setVal(BigInteger.valueOf(28L));
      mRpr.setSz(hpsMeasure);
      mRpr.setSzCs(hpsMeasure);
      customPStyle("TOCHeading", "TOC Heading", pPr, mRpr);

    } catch (JAXBException | InvalidFormatException e) {
      log.error("Build Style Error", e);
    }
  }

  public static StyleDefinitionsPart init() {
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
      String id, String name, PPr pPr, TblPr tblPr, List<CTTblStylePr> tblStylePr) {
    return customStyle(
        "table",
        id,
        name,
        definitionsPart.getDefaultTableStyle().getStyleId(),
        pPr,
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
