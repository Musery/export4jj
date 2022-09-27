package com.musery.export.transform.docx;

import cn.hutool.core.collection.CollectionUtil;
import com.musery.export.transform.docx.part.CStyle;
import com.musery.parse.AST;
import java.math.BigInteger;
import java.util.List;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.parts.WordprocessingML.FootnotesPart;
import org.docx4j.wml.CTFtnEdn;
import org.docx4j.wml.CTFtnEdnRef;
import org.docx4j.wml.CTVerticalAlignRun;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase.PStyle;
import org.docx4j.wml.R;
import org.docx4j.wml.R.FootnoteRef;
import org.docx4j.wml.RPr;
import org.docx4j.wml.RStyle;
import org.docx4j.wml.STVerticalAlignRun;
import org.docx4j.wml.Text;

public class Footnote implements DOCX4TR {

  private static final RPr ref;

  private static final PPr def;

  static {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();

    RPr rPr = objectFactory.createRPr();
    CTVerticalAlignRun run = objectFactory.createCTVerticalAlignRun();
    run.setVal(STVerticalAlignRun.SUPERSCRIPT);
    rPr.setVertAlign(run);
    CStyle.customCStyle("ref", "Ref", rPr);

    ref = objectFactory.createRPr();
    RStyle rStyle = objectFactory.createRStyle();
    rStyle.setVal("ref");
    ref.setRStyle(rStyle);

    PPr pPr = objectFactory.createPPr();
    Jc jc = objectFactory.createJc();
    jc.setVal(JcEnumeration.LEFT);
    pPr.setJc(jc);
    pPr.setSnapToGrid(objectFactory.createBooleanDefaultTrue());

    RPr paraRPr = objectFactory.createRPr();
    HpsMeasure hpsMeasure = objectFactory.createHpsMeasure();
    hpsMeasure.setVal(BigInteger.valueOf(18L));
    paraRPr.setSz(hpsMeasure);
    paraRPr.setSzCs(hpsMeasure);

    CStyle.customPStyle("def", "Def", pPr, paraRPr);

    def = objectFactory.createPPr();
    PStyle pStyle = objectFactory.createPPrBasePStyle();
    pStyle.setVal("def");
    def.setPStyle(pStyle);
  }

  @Override
  public List transform(AST ast) {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    if (ast.getType().equals("footnoteReference")) {
      R r = objectFactory.createR();
      r.setRPr(ref);
      CTFtnEdnRef ftnEdnRef = objectFactory.createCTFtnEdnRef();
      ftnEdnRef.setId(BigInteger.valueOf(Integer.valueOf(ast.getIdentifier())));
      r.getContent().add(objectFactory.createRFootnoteReference(ftnEdnRef));
      return CollectionUtil.newArrayList(r);
    } else {
      FootnotesPart footnotesPart = getWMLPackage().getMainDocumentPart().getFootnotesPart();
      CTFtnEdn ctFtnEdn = objectFactory.createCTFtnEdn();
      ctFtnEdn.setId(BigInteger.valueOf(Integer.valueOf(ast.getIdentifier())));
      for (P p : (List<P>) traverseChildren(ast)) {
        p.setPPr(def);
        R r = objectFactory.createR();
        r.setRPr(ref);
        FootnoteRef b = objectFactory.createRFootnoteRef();
        r.getContent().add(b);
        p.getContent().add(0, r);
        R space = objectFactory.createR();
        Text t = objectFactory.createText();
        t.setSpace("preserve");
        space.getContent().add(t);
        p.getContent().add(1, space);
        ctFtnEdn.getContent().add(p);
      }
      footnotesPart.getJaxbElement().getFootnote().add(ctFtnEdn);
      return CollectionUtil.newArrayList();
    }
  }

  @Override
  public boolean adapt(AST ast) {
    return ast.getType().equals("footnoteReference") || ast.getType().equals("footnoteDefinition");
  }
}
