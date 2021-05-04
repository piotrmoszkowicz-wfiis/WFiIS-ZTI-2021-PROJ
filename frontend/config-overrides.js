const path = require("path");
module.exports = function override(config) {
  config.resolve = {
    ...config.resolve,
    alias: {
      ...config.alias,
      "@components": path.resolve(__dirname, "src/components"),
      "@contexts": path.resolve(__dirname, "src/contexts"),
      "@enums": path.resolve(__dirname, "src/enums"),
      "@generated": path.resolve(__dirname, "src/generated"),
      "@interfaces": path.resolve(__dirname, "src/interfaces"),
      "@themes": path.resolve(__dirname, "src/themes"),
    },
  };
  return config;
};
