package com.musery.export.transform;

import cn.hutool.core.collection.CollectionUtil;
import com.musery.export.ExportOption;
import com.musery.export.transform.part.*;
import com.musery.parse.AST;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.FootnotesPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.toc.Toc;
import org.docx4j.toc.TocGenerator;

import java.util.List;

public class Root implements DOCX4TR, Starter {

  public WordprocessingMLPackage start(AST ast, ExportOption option) {
    try {
      // new
      WordprocessingMLPackage docx = WordprocessingMLPackage.createPackage();
      // word/document.xml
      MainDocumentPart mainDocumentPart = docx.getMainDocumentPart();
      // add fontTable
      CFontTable.init(docx);
      // add footnote
      FootnotesPart footnotesPart = CFootnote.init();
      mainDocumentPart.addTargetPart(footnotesPart);
      // add num
      mainDocumentPart.addTargetPart(CNum.init());
      // add styles
      mainDocumentPart.addTargetPart(CStyle.init());
      // init image config
      CImage.initErrorPng(docx);
      if (StringUtils.isNotBlank(option.getHeader())) {
        CHeader.init(docx, option.getHeader());
      }
      CFooter.init(docx);
      option.setDocx(docx);
      ExportOption.prepare(option);
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
      // 首页在目录之后
      if (CollectionUtil.isNotEmpty(option.getFont())) {
        CFont.init(docx, option.getFont());
      }
      return docx;
    } catch (Docx4JException e) {
      throw new RuntimeException("To FILE Error", e);
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
