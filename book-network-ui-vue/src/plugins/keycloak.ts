// src/plugins/keycloak.js

import keycloakService from "@/services/keycloak/keycloak";

export default {
  install: (app) => {
    // 使用 provide/inject，这是 Vue 3 推荐的跨组件通信方式
    app.provide("keycloak", keycloakService);

    // 为了方便在 Options API 中使用，也可以挂载到全局属性
    // 在 Vue 3 中，this.$keycloak 这样用
    app.config.globalProperties.$keycloak = keycloakService;
  },
};
