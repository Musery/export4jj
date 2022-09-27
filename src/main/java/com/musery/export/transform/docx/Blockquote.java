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
import org.docx4j.wml.RStyle;

public class Blockquote implements DOCX4TR {

  private static final PPr blockquote;

  static {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    blockquote = objectFactory.createPPr();
    ParaRPr blockquoteRPr = objectFactory.createParaRPr();

    PStyle pStyle = objectFactory.createPPrBasePStyle();
    pStyle.setVal("af");

    Ind ind = objectFactory.createPPrBaseInd();
    ind.setLeft(BigInteger.valueOf(1470L));
    ind.setRight(BigInteger.valueOf(1470L));

    RStyle rStyle = objectFactory.createRStyle();
    rStyle.setVal("a8");
    blockquoteRPr.setRStyle(rStyle);

    blockquote.setPStyle(pStyle);
    blockquote.setInd(ind);
    blockquote.setRPr(blockquoteRPr);
  }

  /**
   * 此处忽略 blockquote 与其他(list, blockquote) 层级嵌套
   *
   * @param ast
   * @return
   */
  @Override
  public List transform(AST ast) {
    List<P> list = new ArrayList<>();
    for (int i = 0; i < ast.getChildren().size(); i++) {
      AST child = ast.getChildren().get(i);
      switch (child.getType()) {
        case "paragraph":
          for (P p : (List<P>) traverseChildren(ast, i)) {
            p.getContent().add(0, blockquote);
            list.add(p);
          }
          break;
        default:
          for (P p : (List<P>) traverseChildren(ast, i)) {
            list.add(p);
          }
          break;
      }
    }
    return list;
  }

  @Override
  public boolean adapt(AST ast) {
    return ast.getType().equals("blockquote");
  }
}
