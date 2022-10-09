package com.musery.export.transform;

import cn.hutool.core.collection.CollectionUtil;
import com.musery.parse.AST;
import java.util.List;
import org.docx4j.jaxb.Context;
import org.docx4j.wml.CTVerticalJc;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.STVerticalJc;
import org.docx4j.wml.Tc;
import org.docx4j.wml.TcPr;

public class TableCell implements DOCX4TR {

  @Override
  public List transform(AST ast) {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    Tc tc = objectFactory.createTc();
    TcPr tcPr = objectFactory.createTcPr();
    CTVerticalJc ctVerticalJc = objectFactory.createCTVerticalJc();
    ctVerticalJc.setVal(STVerticalJc.CENTER);
    tcPr.setVAlign(ctVerticalJc);
    tc.setTcPr(tcPr);
    P paragraph = objectFactory.createP();
    PPr pPr = objectFactory.createPPr();
    Jc jc = objectFactory.createJc();
    jc.setVal(JcEnumeration.CENTER);
    pPr.setJc(jc);
    paragraph.setPPr(pPr);
    paragraph.getContent().addAll(traverseChildren(ast));
    tc.getContent().add(paragraph);
    return CollectionUtil.newArrayList(tc);
  }

  @Override
  public boolean adapt(AST ast) {
    return ast.getType().equals("tableCell");
  }
}
