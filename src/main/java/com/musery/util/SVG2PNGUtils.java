package com.musery.util;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SVG2PNGUtils {

  public static void convertToPng(
      String svgCode, OutputStream outputStream, Float width, Float height)
      throws TranscoderException, IOException {
    byte[] bytes = svgCode.getBytes("utf-8");
    PNGTranscoder t = new PNGTranscoder();
    TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(bytes));
    TranscoderOutput output = new TranscoderOutput(outputStream);
    // 增加图片的属性设置(单位是像素)---下面是写死了，实际应该是根据SVG的大小动态设置，默认宽高都是400
    t.addTranscodingHint(ImageTranscoder.KEY_WIDTH, width);
    t.addTranscodingHint(ImageTranscoder.KEY_HEIGHT, height);
    t.transcode(input, output);
    outputStream.flush();
  }
}
