package com.musery.export.transform.part;

import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;
import org.docx4j.wml.*;
import org.docx4j.wml.Lvl.LvlText;
import org.docx4j.wml.Lvl.Start;
import org.docx4j.wml.Numbering.AbstractNum;
import org.docx4j.wml.Numbering.AbstractNum.MultiLevelType;
import org.docx4j.wml.Numbering.Num;
import org.docx4j.wml.Numbering.Num.AbstractNumId;
import org.docx4j.wml.PPrBase.Ind;
import org.docx4j.wml.PPrBase.NumPr;
import org.docx4j.wml.PPrBase.NumPr.Ilvl;
import org.docx4j.wml.PPrBase.NumPr.NumId;

import java.math.BigInteger;

/** 不建议自定义 够用 */
@Slf4j
public class CNum {

  private static NumberingDefinitionsPart definitionsPart;

  private static BigInteger orderId = BigInteger.valueOf(3);
  private static BigInteger bulletId = BigInteger.valueOf(4);

  static {
    try {
      definitionsPart = new NumberingDefinitionsPart();
      definitionsPart.unmarshalDefaultNumbering();
      // 增加默认的自定义序列
      withNum(orderId, NumberFormat.DECIMAL);
      withNum(bulletId, NumberFormat.BULLET);
    } catch (JAXBException | InvalidFormatException e) {
      log.error("Build Numbering Error", e);
    }
  }

  public static NumberingDefinitionsPart init() {
    return definitionsPart;
  }

  private static void withNum(BigInteger numId, NumberFormat numberFormat) {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    BigInteger abstractId = numId.subtract(BigInteger.ONE);
    AbstractNum abstractNum = objectFactory.createNumberingAbstractNum();
    abstractNum.setAbstractNumId(abstractId);
    MultiLevelType multiLevelType = objectFactory.createNumberingAbstractNumMultiLevelType();
    multiLevelType.setVal("hybridMultilevel");
    abstractNum.setMultiLevelType(multiLevelType);
    for (int i = 0; i < 6; i++) {
      Lvl lvl = objectFactory.createLvl();
      lvl.setIlvl(BigInteger.valueOf(i));
      Start start = objectFactory.createLvlStart();
      start.setVal(BigInteger.ONE);
      lvl.setStart(start);
      NumFmt numFmt = objectFactory.createNumFmt();
      numFmt.setVal(numberFormat);
      lvl.setNumFmt(numFmt);
      LvlText text = objectFactory.createLvlLvlText();
      if (numberFormat.equals(NumberFormat.BULLET)) {
        switch (i % 3) {
          case 1:
            text.setVal("");
            break;
          case 2:
            text.setVal("");
            break;
          default:
            text.setVal("");
            break;
        }
        RPr rPr = objectFactory.createRPr();
        RFonts rFonts = objectFactory.createRFonts();
        rFonts.setAscii("Wingdings");
        rFonts.setHAnsi("Wingdings");
        rFonts.setHint(STHint.DEFAULT);
        rPr.setRFonts(rFonts);
        lvl.setRPr(rPr);
      } else {
        text.setVal("%" + (i + 1) + ".");
      }
      lvl.setLvlText(text);

      Jc jc = objectFactory.createJc();
      jc.setVal(JcEnumeration.LEFT);
      lvl.setLvlJc(jc);

      PPr pPr = objectFactory.createPPr();
      Ind ind = objectFactory.createPPrBaseInd();
      ind.setLeft(BigInteger.valueOf(360L * (i + 1)));
      ind.setHanging(BigInteger.valueOf(360L));
      pPr.setInd(ind);
      lvl.setPPr(pPr);

      abstractNum.getLvl().add(lvl);
    }
    definitionsPart.getJaxbElement().getAbstractNum().add(abstractNum);
    Num num = objectFactory.createNumberingNum();
    num.setNumId(numId);
    AbstractNumId abstractNumId = objectFactory.createNumberingNumAbstractNumId();
    abstractNumId.setVal(abstractId);
    num.setAbstractNumId(abstractNumId);
    definitionsPart.getJaxbElement().getNum().add(num);
  }

  public static NumPr getList(boolean order, int il) {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    NumPr numPr = objectFactory.createPPrBaseNumPr();

    NumId numId = objectFactory.createPPrBaseNumPrNumId();
    // 1 order 0 bullet
    numId.setVal(order ? orderId : bulletId);
    numPr.setNumId(numId);
    Ilvl ilvl = objectFactory.createPPrBaseNumPrIlvl();
    ilvl.setVal(BigInteger.valueOf(il));
    numPr.setIlvl(ilvl);
    return numPr;
  }
}
