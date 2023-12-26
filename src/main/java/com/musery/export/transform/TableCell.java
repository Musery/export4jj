package com.musery.export.transform;

import cn.hutool.core.collection.CollectionUtil;
import com.musery.parse.AST;
import java.util.List;
import org.docx4j.jaxb.Context;
import org.docx4j.wml.*;

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
    ParaRPr rPr = objectFactory.createParaRPr();
    rPr.setB(objectFactory.createBooleanDefaultTrue());
    rPr.setBCs(objectFactory.createBooleanDefaultTrue());
    Color color = objectFactory.createColor();
    color.setVal("000000");
    color.setThemeColor(STThemeColor.BACKGROUND_1);
    rPr.setColor(color);
    pPr.setRPr(rPr);
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
