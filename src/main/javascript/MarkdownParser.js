const { fromMarkdown } = require("mdast-util-from-markdown");
const { gfm } = require("micromark-extension-gfm");
const { gfmFromMarkdown } = require("mdast-util-gfm");

const toAST = (markdown) => {
  return fromMarkdown(markdown, {
    extensions: [gfm()],
    mdastExtensions: [gfmFromMarkdown()],
  });
};

const args = require("arg")({
  "--markdown": String,
});

console.log(toAST(args["--markdown"]));
