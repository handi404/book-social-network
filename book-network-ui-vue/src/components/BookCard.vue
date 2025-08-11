<template>
  <el-card
    class="book-card"
    shadow="hover"
    :style="{
      borderColor: finalBorderColor,
    }"
  >
    <template #header>
      <el-image
        class="book-cover-image"
        :src="bookCover()"
        :preview-src-list="srcList"
        fit="cover"
        alt="Clean Code Book Cover"
      />
    </template>
    <!-- 默认插槽：包含封面和可滚动的内容区 -->

    <!-- 核心内容区，现在是可滚动的 -->
    <div class="card-content-scrollable">
      <div class="book-info">
        <div class="info-header">
          <el-icon :size="20" class="title-icon"><Document /></el-icon>
          <h3 class="book-title">{{ book.title }}</h3>
        </div>
        <div class="meta-list">
          <div class="meta-item">
            <el-icon><User /></el-icon>
            <el-icon class="verified-icon"><SuccessFilled /></el-icon>
            <span>&nbsp;{{ book.authorName }}</span>
          </div>
          <div class="meta-item">
            <el-icon><DataLine /></el-icon>
            <span>&nbsp;{{ book.isbn }}</span>
          </div>
          <div class="meta-item">
            <el-icon><Avatar /></el-icon>
            <span>&nbsp;{{ book.owner }}</span>
          </div>
        </div>
      </div>
      <el-divider />
      <p class="book-description">
        {{ book.synopsis }}
      </p>
    </div>

    <!-- 页脚插槽：保持固定在底部 -->
    <template #footer>
      <div class="card-footer">
        <div class="rating-section">
          <el-rate
            v-model="book.rate"
            disabled
            show-score
            score-template="{value}"
            text-color="#ff9900"
            allow-half
          />
        </div>
        <div class="action-buttons" v-if="manage">
          <el-button :icon="Edit" link @click="onEdit()" />
          <el-button :icon="Share" link @click="onShare()" />
          <el-button
            :icon="TakeawayBox"
            link
            class="delete-button"
            @click="onArchive()"
          />
        </div>
        <div class="action-buttons" v-if="!manage">
          <el-button :icon="InfoFilled" link @click="onShowDetails()" />
          <el-button :icon="Operation" link @click="handleBorrow()" />
          <el-button
            :icon="Share"
            link
            class="delete-button"
            @click="onAddToWaitingList()"
          />
        </div>
      </div>
    </template>
  </el-card>
  <!-- <el-dialog v-model="sync" title="Tips" width="500">
    <span>是否借阅此书籍？</span>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="sync = false">Cancel</el-button>
        <el-button type="primary" @click="handleBorrow()"> Confirm </el-button>
      </div>
    </template>
  </el-dialog> -->
</template>

<script setup lang="ts">
import type { BookResponse } from "@/client";
import { Book } from "@/client";
import { computed, ref, watch } from "vue";
import {
  Document,
  User,
  SuccessFilled,
  Avatar,
  DataLine,
  Edit,
  Share,
  Operation,
  InfoFilled,
  TakeawayBox,
} from "@element-plus/icons-vue";
import router from "@/router";

const props = defineProps<{
  book: BookResponse;
  manage: boolean;
}>();
const emit = defineEmits<{
  (e: "borrow", bookId: number): void;
}>();

const share = ref(props.book.shareable);
const archived = ref(props.book.archived);
const srcList = ref<string[]>([]);
// const sync = defineModel<boolean>("dialogVisible");
const bookCover = () => {
  if (!props.book.cover) {
    return "https://source.unsplash.com/user/c_v_r/1900x800";
  }
  srcList.value.push("data:image/jpg;base64," + props.book.cover);
  return "data:image/jpg;base64," + props.book.cover;
};

const onEdit = () => {
  router.push(`/manage/${props.book.id}`);
};
const onShare = () => {
  Book.updateShareableStatus({
    path: {
      "book-id": props.book.id as number,
    },
  }).then(() => {
    share.value = !share.value;
  });
};
const onArchive = () => {
  Book.updateArchivedStatus({
    path: {
      "book-id": props.book.id as number,
    },
  }).then(() => {
    archived.value = !archived.value;
  });
};
const finalBorderColor = computed(() => {
  if (archived.value && props.manage) {
    return "rgb(235,182, 53)";
  } else if (share.value && props.manage) {
    return "rgb(62,112,84)";
  } else {
    return "var(--el-border-color-lighter)";
  }
});

const onShowDetails = () => {
  router.push(`/details/${props.book.id}`);
};
const handleBorrow = () => {
  emit("borrow", props.book.id as number);
};
const onAddToWaitingList = () => {};
</script>

<style scoped>
/* 1. 尺寸与布局基础 */
.book-card {
  width: 300px; /* 固定宽度 */
  height: 350px; /* 固定高度 */
  border-radius: 12px;
  border: 3px solid var(--el-border-color-lighter);
  overflow: hidden;
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB",
    "Microsoft YaHei", Arial, sans-serif;
  transition: all 0.3s ease-in-out;

  /* 关键：将卡片本身设置为列式Flexbox容器，以控制body和footer的垂直布局 */
  display: flex;
  flex-direction: column;
}

:deep(.el-card__body) {
  padding: 0;
  flex-grow: 1; /* 让body占据卡片内除了footer的所有剩余空间 */
  overflow: hidden; /* 必须隐藏，防止内容溢出破坏布局 */

  /* body内部也使用Flexbox来管理封面和内容区 */
  display: flex;
  flex-direction: column;
}

:deep(.el-card__footer) {
  padding: 10px 15px; /* 调整内边距以适应更小的尺寸 */
  flex-shrink: 0; /* 确保页脚高度固定，不被挤压 */
  border-top: 1px solid var(--el-border-color-lighter);
}

/* 2. 封面：调整高度以适应新尺寸 */
.book-cover-image {
  width: 100%;
  height: 150px; /* 减少封面高度，为内容留出空间 */
  display: block;
  flex-shrink: 0;
}

/* 3. 智能滚动区域 */
.card-content-scrollable {
  flex-grow: 1; /* 占据封面下方、页脚上方的所有可用空间 */
  overflow-y: auto; /* 核心：当内容超出此区域高度时，自动显示垂直滚动条 */
  padding: 16px 18px; /* 为滚动内容提供内边距 */
}
/* 为滚动条添加更现代、简约的样式 (可选) */
.card-content-scrollable::-webkit-scrollbar {
  width: 6px;
}
.card-content-scrollable::-webkit-scrollbar-track {
  background: transparent;
}
.card-content-scrollable::-webkit-scrollbar-thumb {
  background: #ccc;
  border-radius: 3px;
}
.card-content-scrollable::-webkit-scrollbar-thumb:hover {
  background: #aaa;
}

/* 4. 内容样式微调 */
.book-info {
  margin-bottom: 12px;
  flex-shrink: 0;
}
.info-header {
  margin-bottom: 10px;
}
.book-title {
  font-size: 1.1rem;
}
.meta-list {
  gap: 6px;
}
.meta-item {
  font-size: 0.85rem;
}

/* 5. 移除不再需要的文本截断 */
.book-description {
  font-size: 0.85rem;
  color: #86868b;
  line-height: 1.6;
  margin: 0;
  /* 不再需要 -webkit-line-clamp 等截断样式 */
}

/* 底部操作栏样式保持不变 */
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.rating-section :deep(.el-rate__text) {
  font-size: 0.9rem;
  font-weight: bold;
  color: #333 !important;
  padding-left: 5px;
}
.action-buttons .el-button {
  font-size: 16px;
  color: #8e8e93;
}
.action-buttons .el-button:hover {
  color: #007aff;
}
.action-buttons .delete-button {
  color: #f56c6c;
}
.action-buttons .delete-button:hover {
  color: #d93d3d;
}
</style>
