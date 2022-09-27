package com.musery.export.transform.docx;

import com.musery.export.ExportOption;
import com.musery.export.transform.TR;
import com.musery.parse.AST;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

public interface DOCX4TR extends TR {

  @Override
  default List traverseOne(AST ast) {
    for (DOCX4TR tr : Config.list) {
      if (tr.adapt(ast)) {
        return tr.transform(ast);
      }
    }
    return Collections.EMPTY_LIST;
  }

  default WordprocessingMLPackage getWMLPackage() {
    return (WordprocessingMLPackage) ExportOption.getThreadLocal().get();
  }

  class Config {

    protected static List<DOCX4TR> list = new LinkedList<>();
    // 默认加载包
    static {
      tail(new Text());
      tail(new Strong());
      tail(new Emphasis());
      tail(new InlineCode());
      tail(new Delete());
      tail(new Echarts());
      tail(new Image());
      tail(new Link());
      tail(new Break());
      tail(new Paragraph());
      tail(new Heading());
      tail(new Code());
      tail(new Blockquote());
      tail(new IList());
      tail(new ThematicBreak());
      tail(new Table());
      tail(new TableRow());
      tail(new TableCell());
      tail(new Footnote());
    }

    public static void tail(DOCX4TR tr) {
      list.add(tr);
    }

    public static void head(DOCX4TR tr) {
      list.add(0, tr);
    }
  }
}
