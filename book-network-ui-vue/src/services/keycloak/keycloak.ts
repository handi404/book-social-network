// src/services/keycloak.js

import Keycloak from "keycloak-js";
import { reactive } from "vue";
import type { UserProfile } from "./user-profile";

// 1. Keycloak 配置
const keycloakConfig = {
  url: import.meta.env.VITE_KEYCLOAK_URL,
  realm: import.meta.env.VITE_KEYCLOAK_REALM,
  clientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID,
};

// 2. 创建 Keycloak 实例
const _keycloak = new Keycloak(keycloakConfig);

// 3. 创建一个响应式对象来存储 Keycloak 的状态
// 这是关键！直接使用 keycloak 实例在 Vue 中不是响应式的。
const state = reactive({
  isAuthenticated: false as boolean | undefined,
  profile: null as UserProfile | null,
  token: "" as string | undefined,
  roles: [] as string[],
  hasRole: (role: string) => state.roles.includes(role), // 提供一个方便的检查角色的方法
});

/**
 * 更新响应式 state 的函数
 */
const updateState = () => {
  state.isAuthenticated = _keycloak.authenticated;
  state.token = _keycloak.token;
  if (_keycloak.authenticated) {
    // 解析 Token 获取角色信息
    state.roles = _keycloak.tokenParsed?.realm_access?.roles || [];
    // 加载用户信息
    _keycloak.loadUserProfile().then((profile) => {
      state.profile = profile;
    });
  } else {
    state.roles = [];
    state.profile = null;
  }
};

/**
 * 初始化函数
 * @returns {Promise<Keycloak>} 返回 Keycloak 实例的 Promise
 */
const init = () => {
  return new Promise((resolve, reject) => {
    _keycloak
      .init({
        onLoad: "login-required", // 或者 'check-sso'
      })
      .then((authenticated) => {
        console.log(`Keycloak authenticated: ${authenticated}`);
        updateState();
        resolve(_keycloak); // 初始化成功后，resolve Keycloak 实例
      })
      .catch((error) => {
        console.error("Keycloak initialization failed", error);
        reject(error);
      });
  });
};

// 定时刷新 Token
_keycloak.onTokenExpired = () => {
  _keycloak
    .updateToken(30)
    .then((refreshed) => {
      if (refreshed) {
        console.log("Token was successfully refreshed");
        updateState();
      } else {
        console.log("Token is still valid");
      }
    })
    .catch(() => {
      console.error("Failed to refresh the token, or the session has expired");
      _keycloak.logout(); // 刷新失败，强制登出
    });
};

// 导出我们需要的一切
export default {
  keycloak: _keycloak, // 原始实例，用于调用 logout, login 等方法
  state, // 响应式状态，用于在组件中读取信息
  init, // 初始化函数
};
