<template>
  <el-dialog v-model="dialogVisible" title="Tips" width="500">
    <span>是否退出登录</span>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="handleLogout"> Confirm </el-button>
      </div>
    </template>
  </el-dialog>
  <el-menu
    :router="true"
    :default-active="route.path"
    class="el-menu-demo"
    mode="horizontal"
    @select="handleSelect"
  >
    <el-text class="mx-1" size="large"><strong>BSN</strong></el-text>
    <el-space :size="size" :spacer="spacer">
      <div v-for="i in 2" :key="i">
        <p></p>
      </div>
    </el-space>
    <el-menu-item index="/">
      <el-icon><HomeFilled /></el-icon>Home
    </el-menu-item>
    <el-menu-item index="/my-books">
      <el-icon><Memo /></el-icon>My books
    </el-menu-item>
    <el-menu-item index="/my-waiting-list">
      <el-icon><List /></el-icon>My waiting list
    </el-menu-item>
    <el-menu-item index="/my-returned-books">
      <el-icon><HomeFilled /></el-icon>My returned books
    </el-menu-item>
    <el-menu-item index="/my-borrowed-books">
      <el-icon><HomeFilled /></el-icon>Borrowed books
    </el-menu-item>
    <el-text>
      <el-row :gutter="20" justify="end">
        <el-col :span="12">
          <el-input aria-label="First Name" placeholder="First Name" />
        </el-col>
        <el-col :span="6">
          <el-button type="primary" :icon="Search" />
        </el-col>
        <!-- <el-col :span="6">
          <el-text class="mx-1" size="large">Welcome {{ username }}</el-text>
        </el-col> -->
      </el-row>
    </el-text>
    <el-menu-item>
      <el-text class="mx-1" size="large">
        Welcome <strong>{{ username }}</strong>
      </el-text>
    </el-menu-item>

    <el-menu-item @click="keycloakService.keycloak.accountManagement()">
      <el-button link>
        <el-icon size="25" style="color: RGB(64, 158, 255)"
          ><UserFilled
        /></el-icon>
      </el-button>
    </el-menu-item>
    <el-menu-item @click="dialogVisible = true">
      <el-icon size="20" style="color: RGB(64, 158, 255)"
        ><SwitchButton
      /></el-icon>
    </el-menu-item>
  </el-menu>
</template>

<script lang="ts" setup>
import { ref, h } from "vue";
import { ElDivider, ElMessageBox } from "element-plus";
import { Search } from "@element-plus/icons-vue";
import keycloakService from "@/services/keycloak/keycloak";
import { useRoute } from "vue-router";

const route = useRoute();
const username =
  (keycloakService.state.profile?.firstName as string) +
  keycloakService.state.profile?.lastName;
const size = ref(10);
const spacer = h(ElDivider, { direction: "vertical" });
const handleSelect = (key: string, keyPath: string[]) => {
  // console.log(key, keyPath);
};
const dialogVisible = ref(false);

const handleLogout = () => {
  dialogVisible.value = false;
  keycloakService.keycloak.logout();
};
</script>

<style scoped></style>
