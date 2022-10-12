package com.musery;

import cn.hutool.core.io.IoUtil;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/** NodeJS 进程运行环境 */
@Slf4j
@Getter
public class NodeJSEnvironment {

  private ProcessBuilder pb;

  private boolean support;

  protected NodeJSEnvironment() {
    pb =
        new ProcessBuilder()
            //  设置工作路径
            .directory(new File(NodeJSEnvironment.class.getResource("/export-runtime").getPath()));
    checkNodeEnvironment();
  }

  protected void checkNodeEnvironment() {
    start(
        null,
        (v) -> {
          log.info("There is runtime environment for NodeJS[version: {}]", v);
          this.support = true;
        },
        null,
        "-v");
    if (!this.support) {
      log.error(
          "There is not runtime environment for NodeJS which you can download form https://nodejs.org/zh-cn/download/");
    }
  }

  protected void start(String jsFile, Consumer<String> suc, Consumer<String> err, String... args) {
    List<String> command = new ArrayList<>(null == args ? 2 : (args.length + 2));
    command.add("node");
    if (!Objects.isNull(jsFile)) {
      command.add(jsFile);
    }
    if (null != args) {
      for (String arg : args) command.add(arg);
    }
    pb.command(command);
    try {
      Process process = pb.start();
      if (null != suc) {
        suc.accept(IoUtil.read(process.getInputStream(), StandardCharsets.UTF_8));
      }
      String errMsg = IoUtil.read(process.getErrorStream(), StandardCharsets.UTF_8);
      if (StringUtils.isNotBlank(errMsg)) {
        log.error("exec command:[{}] with err[{}]", String.join(" ", command), errMsg);
        if (null != err) {
          err.accept(errMsg);
        }
      }
    } catch (IOException e) {
      log.error("without NodeJS Runtime environment", e);
    } finally {
      if (!this.support) {
        log.error(
            "There is not runtime environment for NodeJS which you can download form https://nodejs.org/zh-cn/download/");
      }
    }
  }
}
