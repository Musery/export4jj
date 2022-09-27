package com.musery.export.transform.docx;

import com.musery.export.transform.docx.part.CStyle;
import com.musery.parse.AST;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.docx4j.jaxb.Context;
import org.docx4j.wml.CTBorder;
import org.docx4j.wml.CTShd;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase.PBdr;
import org.docx4j.wml.PPrBase.PStyle;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STBorder;
import org.docx4j.wml.STShd;
import org.docx4j.wml.Text;

public class Code implements DOCX4TR {

  private static final PPr code;

  static {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    PPr pPr = objectFactory.createPPr();
    PBdr pBdr = objectFactory.createPPrBasePBdr();
    CTBorder border = objectFactory.createCTBorder();
    border.setVal(STBorder.SINGLE);
    border.setSz(BigInteger.valueOf(6L));
    border.setSpace(BigInteger.ONE);
    border.setColor("auto");
    pBdr.setTop(border);
    pBdr.setBottom(border);
    pBdr.setLeft(border);
    pBdr.setRight(border);
    pPr.setPBdr(pBdr);
    CTShd ctShd = objectFactory.createCTShd();
    ctShd.setVal(STShd.PCT_20);
    ctShd.setColor("auto");
    ctShd.setFill("auto");
    pPr.setShd(ctShd);
    RPr rPr = objectFactory.createRPr();
    //    RFonts rFonts = objectFactory.createRFonts();
    //    // 可能字体没有导致显示不一样
    //    rFonts.setAscii("Courier New");
    //    rFonts.setHAnsi("Courier New");
    //    rFonts.setCs("Courier New");
    //    rPr.setRFonts(rFonts);
    HpsMeasure hpsMeasure = objectFactory.createHpsMeasure();
    hpsMeasure.setVal(BigInteger.valueOf(20L));
    rPr.setSz(hpsMeasure);
    rPr.setSzCs(hpsMeasure);
    CStyle.customPStyle("code", "Code", pPr, rPr);

    code = objectFactory.createPPr();
    PStyle pStyle = objectFactory.createPPrBasePStyle();
    pStyle.setVal("code");
    code.setPStyle(pStyle);
  }

  @Override
  public List transform(AST ast) {
    List<P> list = new ArrayList<>();
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    for (String line : ast.getValue().split("\n")) {
      Text text = objectFactory.createText();
      text.setValue(line);
      R r = objectFactory.createR();
      r.getContent().add(text);
      P p = objectFactory.createP();
      p.setPPr(code);
      p.getContent().add(r);
      list.add(p);
    }
    return list;
  }

  @Override
  public boolean adapt(AST ast) {
    return ast.getType().equals("code");
  }
}
