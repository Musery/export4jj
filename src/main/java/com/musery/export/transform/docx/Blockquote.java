package com.musery.export.transform.docx;

import com.musery.export.transform.docx.part.CStyle;
import com.musery.parse.AST;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.docx4j.jaxb.Context;
import org.docx4j.wml.Color;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase.Ind;
import org.docx4j.wml.PPrBase.PStyle;
import org.docx4j.wml.PPrBase.Spacing;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STThemeColor;

public class Blockquote implements DOCX4TR {

  private static final PPr blockquote;

  static {
    ObjectFactory objectFactory = Context.getWmlObjectFactory();
    PPr pPr = objectFactory.createPPr();
    Ind ind = objectFactory.createPPrBaseInd();
    ind.setLeft(BigInteger.valueOf(1440L));
    ind.setRight(BigInteger.valueOf(1440L));
    ind.setLeftChars(BigInteger.valueOf(700L));
    ind.setRightChars(BigInteger.valueOf(700L));
    pPr.setInd(ind);
    Spacing spacing = objectFactory.createPPrBaseSpacing();
    spacing.setAfter(BigInteger.valueOf(120L));
    pPr.setSpacing(spacing);

    RPr rPr = objectFactory.createRPr();
    rPr.setSmallCaps(objectFactory.createBooleanDefaultTrue());
    Color color = objectFactory.createColor();
    color.setVal("5A5A5A");
    color.setThemeColor(STThemeColor.TEXT_1);
    color.setThemeTint("A5");
    rPr.setColor(color);
    CStyle.customPStyle("blockquote", "Block Quote", pPr, rPr);

    blockquote = objectFactory.createPPr();
    PStyle pStyle = objectFactory.createPPrBasePStyle();
    pStyle.setVal("blockquote");
    blockquote.setPStyle(pStyle);
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
            p.setPPr(blockquote);
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
