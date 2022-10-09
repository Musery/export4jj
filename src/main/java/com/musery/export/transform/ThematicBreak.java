package com.musery.export.transform;

import cn.hutool.core.collection.CollectionUtil;
import com.musery.parse.AST;
import jakarta.xml.bind.JAXBElement;
import java.util.List;
import org.docx4j.jaxb.Context;
import org.docx4j.vml.CTRect;
import org.docx4j.vml.officedrawing.STHrAlign;
import org.docx4j.vml.officedrawing.STTrueFalse;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.Pict;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;

public class ThematicBreak implements DOCX4TR {

  private static final P tb;

  static {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    tb = objectFactory.createP();

    R r = objectFactory.createR();

    RPr rPr = objectFactory.createRPr();
    rPr.setNoProof(objectFactory.createBooleanDefaultTrue());
    r.setRPr(rPr);

    Pict pict = objectFactory.createPict();
    org.docx4j.vml.ObjectFactory vmlObjectFactory = new org.docx4j.vml.ObjectFactory();
    CTRect ctRect = vmlObjectFactory.createCTRect();
    ctRect.setVmlId("_x0000_i1025");
    ctRect.setAlt("");
    ctRect.setStyle(
        "width:414.05pt;height:.05pt;mso-width-percent:0;mso-height-percent:0;mso-width-percent:0;mso-height-percent:0");
    ctRect.setHrpct(Float.valueOf(997f));
    ctRect.setHralign(STHrAlign.CENTER);
    ctRect.setHrstd(STTrueFalse.T);
    ctRect.setHr(STTrueFalse.T);
    ctRect.setStroked(org.docx4j.vml.STTrueFalse.F);
    ctRect.setFillcolor("#a0a0a0");
    JAXBElement<CTRect> rect = vmlObjectFactory.createRect(ctRect);
    pict.getAnyAndAny().add(rect);

    r.getContent().add(pict);
    tb.getContent().add(r);
  }

  @Override
  public List transform(AST ast) {
    return CollectionUtil.newArrayList(tb);
  }

  @Override
  public boolean adapt(AST ast) {
    return ast.getType().equals("thematicBreak");
  }
}
