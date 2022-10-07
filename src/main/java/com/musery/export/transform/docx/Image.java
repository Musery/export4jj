package com.musery.export.transform.docx;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import com.musery.parse.AST;
import com.musery.util.HttpUtils;
import com.musery.util.HttpUtils.BaseHttpClient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.R;

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
    R run = buildWithError(ast);
    if (null == run) {
      return CollectionUtil.newArrayList();
    } else {
      return CollectionUtil.newArrayList(run);
    }
  }

  private R buildWithError(AST ast) {
    BinaryPartAbstractImage image = build(ast);
    if (null == image) {
      // 增加占位图
      try {
        image =
            BinaryPartAbstractImage.createImagePart(
                getWMLPackage(),
                FileUtil.file(Image.class.getClassLoader().getResource("error.png")));
      } catch (Exception e) {
        log.error("占位图片加载失败", e);
      }
    }
    if (null != image) {
      try {
        Inline inline =
            image.createImageInline(
                UUID.fastUUID().toString(true), ast.getAlt(), getId(), getId(), false);
        ObjectFactory objectFactory = Context.getWmlObjectFactory();
        R run = objectFactory.createR();
        Drawing drawing = objectFactory.createDrawing();
        drawing.getAnchorOrInline().add(inline);
        run.getContent().add(drawing);
        return run;
      } catch (Exception e) {
        log.error("insert image into document error", e);
      }
    }
    return null;
  }

  protected BinaryPartAbstractImage build(AST ast) {
    if (StringUtils.isNotBlank(ast.getUrl())) {
      try {
        HttpUtils.BaseHttpClient baseHttpClient = new BaseHttpClient(ast.getUrl(), header, null);
        return BinaryPartAbstractImage.createImagePart(
            getWMLPackage(),
            HttpUtils.doMethodWithCode(baseHttpClient.buildRequest("", null, null, null))
                .body()
                .bytes());
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

  private static final AtomicInteger atomicInteger = new AtomicInteger(0);

  protected static int getId() {
    atomicInteger.compareAndSet(10000, 0);
    return atomicInteger.getAndIncrement();
  }
}
