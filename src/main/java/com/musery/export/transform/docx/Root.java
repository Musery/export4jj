package com.musery.export.transform.docx;

import com.musery.export.ExportOption;
import com.musery.export.transform.Starter;
import com.musery.export.transform.docx.part.IFooter;
import com.musery.export.transform.docx.part.IFootnote;
import com.musery.export.transform.docx.part.IHeader;
import com.musery.export.transform.docx.part.INum;
import com.musery.export.transform.docx.part.IStyle;
import com.musery.parse.AST;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.FootnotesPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.toc.Toc;
import org.docx4j.toc.TocGenerator;

public class Root implements DOCX4TR, Starter {

  public void start(AST ast, ExportOption option) {
    try {
      // new
      WordprocessingMLPackage docx = WordprocessingMLPackage.createPackage();
      // word/document.xml
      MainDocumentPart mainDocumentPart = docx.getMainDocumentPart();

      // add footnote
      FootnotesPart footnotesPart = IFootnote.init();
      mainDocumentPart.addTargetPart(footnotesPart);
      // add num
      mainDocumentPart.addTargetPart(INum.init());
      // add styles
      mainDocumentPart.addTargetPart(IStyle.init());
      //  prepare 线程变量
      ExportOption.prepare(docx);

      //  todo 首页
      if (StringUtils.isNotBlank(option.getHeader())) {
        IHeader.init(docx, option.getHeader());
      }
      IFooter.init(docx);
      // 递归解析
      for (Object obj : transform(ast)) {
        mainDocumentPart.addObject(obj);
      }
      Toc.setTocHeadingText("目录");
      // add toc 1-3 style
      mainDocumentPart.getPropertyResolver().activateStyle("TOC1");
      mainDocumentPart.getPropertyResolver().activateStyle("TOC2");
      mainDocumentPart.getPropertyResolver().activateStyle("TOC3");
      // toc build
      TocGenerator tocGenerator = new TocGenerator(docx);
      tocGenerator.generateToc(0, " TOC \\o \"1-3\" \\h \\z \\u ", false);
      // 保存
      docx.save(option.output());
    } catch (Docx4JException e) {
      throw new RuntimeException("To Word Error", e);
    } finally {
      ExportOption.finished();
    }
  }

  @Override
  public List transform(AST ast) {
    return traverseChildren(ast);
  }

  @Override
  public boolean adapt(AST ast) {
    return ast.getType().equals("root");
  }
}
