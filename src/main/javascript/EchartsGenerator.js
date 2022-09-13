const echarts = require("echarts");
const args = require("arg")({
  "--option": String,
});
const build = (width, height, option) => {
  const chart = echarts.init(null, null, {
    renderer: "svg", // 必须使用 SVG 模式
    ssr: true, // 开启 SSR
    width, // 需要指明高和宽
    height,
  });
  // 像正常使用一样 setOption
  chart.setOption(option);
  return chart;
};
console.log(
  build(
    process.env.DEFAULT_WIDTH,
    process.env.DEFAULT_HEIGHT,
    JSON.parse(args["--option"])
  ).renderToSVGString()
);
process.exit();
