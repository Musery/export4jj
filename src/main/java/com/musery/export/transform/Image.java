package com.musery.export.transform;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpUtil;
import com.musery.export.transform.part.CImage;
import com.musery.parse.AST;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.wml.R;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 只支持图片(PNG JEPG)类型 */
@Slf4j
public class Image implements DOCX4TR {

  private static final Map<String, String> header =
      new HashMap<>() {
        {
          put("timeout", "true");
        }
      };

  @Override
  public List transform(AST ast) {
    R run = CImage.buildWithPICT(build(ast), ast.getAlt());
    if (null == run) {
      return CollectionUtil.newArrayList();
    } else {
      return CollectionUtil.newArrayList(run);
    }
  }

  protected BinaryPartAbstractImage build(AST ast) {
    if (StringUtils.isNotBlank(ast.getUrl())) {
      try {
        return BinaryPartAbstractImage.createImagePart(
            getWMLPackage(), HttpUtil.get(ast.getUrl()).getBytes(StandardCharsets.UTF_8));
      } catch (Exception e) {
        log.error("图片加载失败", e);
      }
    }
    return null;
  }

  @Override
  public boolean adapt(AST ast) {
    return ast.getType().equals("image");
  }
}
