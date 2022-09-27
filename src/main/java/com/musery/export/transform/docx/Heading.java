package com.musery.export.transform.docx;

import cn.hutool.core.collection.CollectionUtil;
import com.musery.parse.AST;
import java.util.List;
import org.docx4j.jaxb.Context;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase.PStyle;

public class Heading implements DOCX4TR {

  private static final PPr[] heading = new PPr[3];
  private static final String[] styleID = new String[] {"1", "21", "31"};

  static {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    for (int i = 0; i < 3; i++) {
      heading[i] = objectFactory.createPPr();
      PStyle pStyle = objectFactory.createPPrBasePStyle();
      pStyle.setVal(styleID[i]);
      heading[i].setPStyle(pStyle);
    }
  }

  @Override
  public List transform(AST ast) {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    P p = objectFactory.createP();
    p.setPPr(heading[(ast.getDepth() - 1) >> 1]);
    p.getContent().add(traverseChildren(ast));
    return CollectionUtil.newArrayList(p);
  }

  @Override
  public boolean adapt(AST ast) {
    return ast.getType().equals("heading");
  }
}
