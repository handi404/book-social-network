import { defineConfig } from "@hey-api/openapi-ts";

export default defineConfig({
  input: "./src/openapi/openapi.json",
  output: "src/client",
  plugins: [
    {
      name: "@hey-api/client-axios",
      runtimeConfigPath: "./src/api/hey-api.ts",
    },
    {
      name: "@hey-api/sdk",
      asClass: true,
    },
  ],
});
