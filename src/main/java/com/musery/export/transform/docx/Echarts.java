package com.musery.export.transform.docx;

import com.musery.echarts.EchartsGenerator;
import com.musery.parse.AST;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;

@Slf4j
public class Echarts extends Image {

  @Override
  protected BinaryPartAbstractImage build(AST ast) {
    if (StringUtils.isNotBlank(ast.getUrl())) {
      AtomicReference<BinaryPartAbstractImage> image = new AtomicReference<>();
      EchartsGenerator.generator(
          ast.getUrl(),
          svg -> {
            try {
              image.set(
                  BinaryPartAbstractImage.createImagePart(
                      getWMLPackage(), svg.getBytes(StandardCharsets.UTF_8)));
            } catch (Exception e) {
              log.error("图片加载失败", e);
            }
          },
          null);
      return image.get();
    }
    return null;
  }

  @Override
  public boolean adapt(AST ast) {
    return ast.getType().equals("image") && ast.getAlt().equals("Echarts");
  }
}
