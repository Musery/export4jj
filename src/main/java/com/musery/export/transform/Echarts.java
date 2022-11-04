package com.musery.export.transform;

import cn.hutool.core.collection.CollectionUtil;
import com.musery.echarts.EchartsGenerator;
import com.musery.export.ExportOption;
import com.musery.export.transform.part.CImage;
import com.musery.parse.AST;
import com.musery.util.SVG2PNGUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.wml.R;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class Echarts implements DOCX4TR {

  @Override
  public List transform(AST ast) {
    if (StringUtils.isNotBlank(ast.getUrl())) {
      AtomicReference<R> image = new AtomicReference<>();
      EchartsGenerator.generator(
          ast.getUrl(),
          ExportOption.getThreadLocal().get().tmp(),
          svg -> {
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
              // png
              SVG2PNGUtils.convertToPng(
                  svg, byteArrayOutputStream, Float.valueOf(1024F), Float.valueOf(512F));
              image.set(
                  CImage.buildWithPICT(
                      BinaryPartAbstractImage.createImagePart(
                          getWMLPackage(), byteArrayOutputStream.toByteArray()),
                      ast.getAlt()));
              // svg
              // image.set(CImage.buildWithSvg(getWMLPackage(), svg, ast.getAlt()));
            } catch (Exception e) {
              log.error("图片加载失败", e);
            }
          },
          null);
      if (null != image.get()) {
        return CollectionUtil.newArrayList(image.get());
      }
    }
    return CollectionUtil.newArrayList();
  }

  @Override
  public boolean adapt(AST ast) {
    return ast.getType().equals("image") && ast.getAlt().equals("Echarts");
  }
}
