import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import ElementPlus from "element-plus";
import "element-plus/dist/index.css";
import * as ElementPlusIconsVue from "@element-plus/icons-vue";
import keycloakService from "@/services/keycloak/keycloak";
import keycloakPlugin from "@/plugins/keycloak";

// 最关键的一步：先初始化 Keycloak，再创建和挂载 Vue 应用
keycloakService
  .init()
  .then(() => {
    const app = createApp(App);
    for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
      app.component(key, component);
    }
    app.use(keycloakPlugin); // 使用我们的插件
    app.use(router);

    app.use(ElementPlus);

    app.mount("#app");
  })
  .catch((error) => {
    // 如果 Keycloak 初始化失败，你可以在这里渲染一个错误提示
    document.body.innerHTML =
      "<h1>Error: Could not connect to Keycloak. Please try again later.</h1>";
  });
