import cn.hutool.core.io.FileUtil;
import com.musery.export.Export2Any;
import com.musery.export.ExportOption;
import com.musery.export.FontElement;
import com.musery.export.Format;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MyTest {

  public static void main(String[] args) throws Exception {
    //
    ExportOption exportOption =
        new ExportOption()
            .setHeader("罗贯中选集")
            .setOutput("/Users/jonathan/Downloads")
            .setName(
                "Test "
                    + LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            .setFormat(Format.PDF)
            .setFont(
                FontElement.build(
                    3,
                    FileUtil.file(MyTest.class.getResource("/export-runtime/logo.png")),
                    "罗贯中选集大赏",
                    "作者： 罗贯中",
                    "时间: 明朝"));

    Export2Any.export(
        exportOption,
        FileUtil.readString(
            MyTest.class.getResource("/export-runtime/Test.md"), StandardCharsets.UTF_8));
  }
}
