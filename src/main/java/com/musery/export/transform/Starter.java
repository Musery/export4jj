package com.musery.export.transform;

import com.musery.export.ExportOption;
import com.musery.parse.AST;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

public interface Starter {

  WordprocessingMLPackage start(AST ast, ExportOption option);
}
