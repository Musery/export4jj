package com.musery.export.transform.docx;

import cn.hutool.core.collection.CollectionUtil;
import com.musery.export.transform.docx.part.CStyle;
import com.musery.parse.AST;
import java.math.BigInteger;
import java.util.List;
import org.docx4j.jaxb.Context;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase.OutlineLvl;
import org.docx4j.wml.PPrBase.PStyle;
import org.docx4j.wml.PPrBase.Spacing;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STLineSpacingRule;

public class Heading implements DOCX4TR {

  private static final PPr[] heading = new PPr[3];

  static {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    for (int i = 0; i < 3; i++) {
      PPr pPr = objectFactory.createPPr();
      pPr.setKeepNext(objectFactory.createBooleanDefaultTrue());
      pPr.setKeepLines(objectFactory.createBooleanDefaultTrue());
      Spacing spacing = objectFactory.createPPrBaseSpacing();
      spacing.setBefore(BigInteger.valueOf(330L - i * 70L));
      spacing.setAfter(BigInteger.valueOf(330L - i * 70L));
      spacing.setLine(BigInteger.valueOf(600L - i * 150L));
      spacing.setLineRule(STLineSpacingRule.EXACT);
      OutlineLvl outlineLvl = objectFactory.createPPrBaseOutlineLvl();
      outlineLvl.setVal(BigInteger.valueOf(i));
      pPr.setOutlineLvl(outlineLvl);
      pPr.setSpacing(spacing);

      RPr rPr = objectFactory.createRPr();
      rPr.setB(objectFactory.createBooleanDefaultTrue());
      rPr.setBCs(objectFactory.createBooleanDefaultTrue());
      HpsMeasure hpsMeasure = objectFactory.createHpsMeasure();
      hpsMeasure.setVal(BigInteger.valueOf(44L - i * 12L));
      rPr.setKern(hpsMeasure);
      rPr.setSz(hpsMeasure);
      rPr.setSzCs(hpsMeasure);
      CStyle.customPStyle("IHeading" + i, "IHeading " + i, pPr, rPr);

      heading[i] = objectFactory.createPPr();
      PStyle pStyle = objectFactory.createPPrBasePStyle();
      pStyle.setVal("IHeading" + i);
      heading[i].setPStyle(pStyle);
    }
  }

  @Override
  public List transform(AST ast) {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    P p = objectFactory.createP();
    p.setPPr(heading[(ast.getDepth() - 1) >> 1]);
    p.getContent().addAll(traverseChildren(ast));
    return CollectionUtil.newArrayList(p);
  }

  @Override
  public boolean adapt(AST ast) {
    return ast.getType().equals("heading");
  }
}
