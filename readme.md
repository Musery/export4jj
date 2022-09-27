## 环境要求

1. 运行环境需要可执行 nodeJS 环境

## 如何更新 MarkDown Parse / Echarts Generator JS

1. 本地环境需要有 NPM/[ESbuild](https://esbuild.docschina.org/getting-started/#install-esbuild)
2. 更新 javascript/路径下源 JS 文件
3. 执行 npm run build

## 执行链路

```
Markdown -- Parse --> AST -- Java Traverse --> Paragraph  --
                                  |                         |
                                  |                         | -- Export --> DOCX / PDF
                                  |                         |
                            Echarts Generator --> SVG     --
```

## 为什么使用 JAVA Traverse? 而不是直接使用 JS Traverse. 这样能全部在 JS 文件中执行？

主要是因为需要进行 Echarts Generator 的数据来源主要是 Java 进程数据.
使用 Java Traverse 可以有效减少进程相互调用次数.(当前设计分别 Java 调用 2 次(Parse, EchartsGenerator)
