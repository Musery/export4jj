package com.musery.export.transform;

import com.musery.export.ExportOption;
import com.musery.parse.AST;

public interface Starter {

  void start(AST ast, ExportOption option);
}
