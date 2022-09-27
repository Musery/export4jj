package com.musery.export.transform.docx;

import com.musery.parse.AST;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.docx4j.jaxb.Context;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase.Ind;
import org.docx4j.wml.PPrBase.PStyle;
import org.docx4j.wml.ParaRPr;
import org.docx4j.wml.R;
import org.docx4j.wml.RStyle;
import org.docx4j.wml.Text;

public class Code implements DOCX4TR {

  private static final PPr code;

  static {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    code = objectFactory.createPPr();
    ParaRPr blockquoteRPr = objectFactory.createParaRPr();

    PStyle pStyle = objectFactory.createPPrBasePStyle();
    pStyle.setVal("af3");

    Ind ind = objectFactory.createPPrBaseInd();
    ind.setHanging(BigInteger.valueOf(1000L));

    RStyle rStyle = objectFactory.createRStyle();
    rStyle.setVal("HTML");
    blockquoteRPr.setRStyle(rStyle);

    code.setPStyle(pStyle);
    code.setInd(ind);
    code.setRPr(blockquoteRPr);
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
      p.getContent().add(code);
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
