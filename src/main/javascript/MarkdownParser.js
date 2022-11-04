const { fromMarkdown } = require("mdast-util-from-markdown");
const { gfm } = require("micromark-extension-gfm");
const { gfmFromMarkdown } = require("mdast-util-gfm");
const fs = require("fs");

const toAST = (markdown) => {
  return fromMarkdown(markdown, {
    extensions: [gfm()],
    mdastExtensions: [gfmFromMarkdown()],
  });
};

const args = require("arg")({
  "--markdown": String,
  "--output": String,
});

fs.writeFileSync(args["--output"], JSON.stringify(toAST(args["--markdown"])));
