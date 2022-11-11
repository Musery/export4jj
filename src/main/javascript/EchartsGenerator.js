const echarts = require("echarts");
const worldMap = require("./world.json");
const chinaMap = require("./china.json");
const themeLight = require("./theme-light");
const fs = require("fs");

echarts.registerMap("world", worldMap);
echarts.registerMap("china", chinaMap);
echarts.registerTheme("soc", themeLight);

const args = require("arg")({
  "--option": String,
  "--output": String,
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

fs.writeFileSync(
  args["--output"],
  build(
    process.env.DEFAULT_WIDTH,
    process.env.DEFAULT_HEIGHT,
    JSON.parse(args["--option"])
  ).renderToSVGString()
);

process.exit();
