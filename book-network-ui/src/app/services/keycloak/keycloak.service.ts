import { Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';
import { UserProfile } from './user-profile';

@Injectable({
  providedIn: 'root',
})
export class KeycloakService {
  private _keycloak: Keycloak | undefined;

  get keycloak() {
    if (!this._keycloak) {
      this._keycloak = new Keycloak({
        url: 'http://localhost:9090',
        realm: 'book-social-network',
        clientId: 'bsn',
      });
    }
    return this._keycloak;
  }

  private _profile: UserProfile | undefined;

  get profile(): UserProfile | undefined {
    return this._profile;
  }
  // 初始化
  async init() {
    const authenticated = await this.keycloak.init({
      // login-required: 检查用户是否已经登录
      // check-sso: 单点登录
      onLoad: 'login-required',
    });
    // 若通过身份验证
    if (authenticated) {
      // 加载用户详细信息
      this._profile = (await this.keycloak.loadUserProfile()) as UserProfile;
      this._profile.token = this.keycloak.token || '';
    }
  }
  // 登录
  login() {
    return this.keycloak.login();
  }
  // 注销
  logout() {
    // 可以直接跳转到账户管理页面
    // this.keycloak.accountManagement();
    // 可以指定重定向uri
    return this.keycloak.logout();
  }
}
