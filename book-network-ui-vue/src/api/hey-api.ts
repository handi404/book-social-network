import { client } from "@/client/client.gen";
import KeycloakService from "@/services/keycloak/keycloak";
import type { CreateClientConfig } from "@/client/client.gen";
import axios from "axios";

// hey-api.ts (示例文件名，可在生成配置 runtimeConfigPath 指定)
// 此文件会在 client.gen.ts 初始化之前被调用
export const createClientConfig: CreateClientConfig = (config) => {
  const instance = axios.create({
    baseURL: import.meta.env.VITE_BASE_URL || "http://localhost:8088/api/v1",
  });

  // 注册拦截器
  instance.interceptors.request.use((c) => {
    const token = KeycloakService.keycloak.token;
    if (token) {
      c.headers.set("Authorization", `Bearer ${token}`);
    }
    return c;
  });

  // 返回覆盖后的配置，client.gen.ts 将使用它来初始化 client
  return {
    ...config,
    baseURL: instance.defaults.baseURL,
    axios: instance,
  };
};
