package com.musery.export.transform.docx;

import com.musery.parse.AST;
import java.util.List;

public class Image implements DOCX4TR {

  @Override
  public List transform(AST ast) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean adapt(AST ast) {
    return ast.getType().equals("image");
  }
}
