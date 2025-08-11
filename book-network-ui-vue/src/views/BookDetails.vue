<template>
  <div class="book-detail-root">
    <el-card class="book-card" shadow="hover">
      <el-row :gutter="24" class="card-body" align="middle">
        <!-- 左侧：封面 -->
        <el-col :xs="24" :sm="8" :md="6" class="cover-col">
          <div
            class="cover-wrap"
            @mouseenter="hoverCover = true"
            @mouseleave="hoverCover = false"
          >
            <el-image
              :src="'data:image/jpg;base64,' + book.cover"
              fit="contain"
              class="cover-img"
              :style="{ transform: hoverCover ? 'scale(1.02)' : 'none' }"
              lazy
              alt="book cover"
            />
            <div v-if="!book.cover" class="cover-placeholder">
              <i class="placeholder-icon">📘</i>
              <div class="placeholder-text">No cover</div>
            </div>
          </div>
        </el-col>

        <!-- 右侧：基本信息 -->
        <el-col :xs="24" :sm="16" :md="18">
          <div class="meta-top">
            <div class="title-row">
              <h2 class="book-title">
                <el-icon><Memo /></el-icon>&nbsp;{{ book.title }}
              </h2>
              <!-- <el-badge class="new-badge" value="New" /> -->
            </div>

            <div class="meta-row">
              <div class="author">
                <div>
                  <el-icon size="20" style="color: black"
                    ><UserFilled
                  /></el-icon>
                  <el-icon size="10"><Check /></el-icon>
                </div>
                <span class="author-name">{{ book.authorName }}</span>
                <span class="divider-dot">•</span>
                <span class="book-id">ISBN: {{ book.isbn }}</span>
              </div>

              <div class="rating">
                <el-rate
                  :model-value="book.rate"
                  :disabled="true"
                  :show-text="false"
                  :allow-half="true"
                />
                <span class="rating-value">{{ book.rate?.toFixed(1) }}</span>
                <!-- <span class="feedback-count"
                  >({{ book.feedbacks }} feedbacks)</span
                > -->
              </div>
            </div>
          </div>

          <el-divider class="sep" />

          <div class="synopsis">
            <h3 class="synopsis-title">Synopsis</h3>
            <p class="synopsis-text" v-if="book.synopsis">
              {{ book.synopsis }}
            </p>
            <p class="synopsis-empty" v-else>暂无简介。</p>
          </div>

          <!-- <div class="actions-row">
            <el-button
              type="primary"
              round
              :loading="loading.borrow"
              @click="onBorrow"
            >
              Borrow
            </el-button>
            <el-button
              plain
              round
              :icon="isFavorited ? 'el-icon-star-on' : 'el-icon-star-off'"
              @click="toggleFavorite"
            >
              {{ isFavorited ? "Favorited" : "Add to favorites" }}
            </el-button>
            <el-button type="text" round @click="onShare">Share</el-button>

            <div class="spacer" />

            <small class="muted">Last updated: {{ lastUpdatedText }}</small>
          </div> -->
        </el-col>
      </el-row>
      <el-divider />
      <h3>Feedback</h3>
      <!-- <div
        v-infinite-scroll="load"
        :infinite-scroll-disabled="disabled"
        class="infinite-list"
      >
        <el-alert v-for="i in count" key="i" :closable="false" class="list-item"
          >good</el-alert
        >
        <p v-if="loading">Loading...</p>
        <p v-if="noMore">No more</p>
      </div> -->
      <div class="infinite-list-wrapper" style="overflow: auto">
        <ul
          v-infinite-scroll="load"
          class="list"
          :infinite-scroll-disabled="disabled"
        >
          <el-alert
            v-for="feedback in feedbacks.content"
            :key="feedback"
            :closable="false"
            :type="feedback.ownFeedback ? 'success' : 'primary'"
            class="list-item"
          >
            <el-rate
              :model-value="feedback.note"
              :disabled="true"
              :show-text="false"
              :allow-half="true"
              text-color="#ff9900"
              score-template="{value} points"
              show-score
              disabled-void-color="#c0c4cc"
              size="small"
            />
            <br />{{ feedback.comment }}
          </el-alert>
        </ul>
      </div>
      <div class="example-pagination">
        <div class="example-pagination-block">
          <el-pagination
            size="large"
            layout="prev, pager, next"
            :page-size="pageSize"
            :page-count="totalPages"
            :total="totalPages"
            :current-page="currentPage"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { useRoute } from "vue-router";
import type {
  BookResponse,
  PageResponseFeedbackResponse,
  FeedbackResponse,
} from "@/client";
import { Book, Feedback } from "@/client";
import { ElMessage } from "element-plus";
const route = useRoute();
// local state
const hoverCover = ref(false);
// const loading = ref<{ borrow: boolean }>({ borrow: false });
// const isFavorited = ref(false);

const book = ref<BookResponse>({});
const bookId = route.params.bookId as any;
const feedbacks = ref<PageResponseFeedbackResponse>({});
const pageSize = ref(10);
const currentPage = ref(1);
const totalPages = ref(100);
const count = ref(0);
const loading = ref(false);
const noMore = computed(() => count.value >= 20);
const disabled = computed(() => loading.value || noMore.value);
const load = () => {
  loading.value = true;
  setTimeout(() => {
    count.value += 2;
    loading.value = false;
  }, 2000);
};
// 当页码变化时触发
const handleCurrentChange = (newPage: number) => {
  console.log("当前页:", newPage); // newPage 就是用户点击的页码
  currentPage.value = newPage; // 可选：同步更新绑定值

  // 在这里调用接口获取数据
  // fetchData(newPage, pageSize)
  Feedback.findAllFeedbacksByBook({
    path: {
      "book-id": bookId as number,
    },
    query: {
      page: newPage - 1,
      size: pageSize.value,
    },
  })
    .then((response) => {
      feedbacks.value = response.data || {};
      console.log(feedbacks.value);
      totalPages.value = response.data?.totalPages || 0;
    })
    .catch((error) => {
      console.log(error);
    });
};

onMounted(() => {
  if (bookId) {
    Book.findBookById({
      path: {
        "book-id": bookId as number,
      },
    })
      .then((response) => {
        book.value = response.data || {};
        handleCurrentChange(currentPage.value);
      })
      .catch((error) => {
        console.error("获取书籍信息失败", error);
      });
  }
});
</script>

<style scoped>
.example-pagination {
  display: flex;
  justify-content: center;
  margin-top: 90px;
}
.infinite-list-wrapper {
  height: 300px;
  /* text-align: center; */
}
.infinite-list-wrapper .list {
  padding: 0;
  margin: 0;
  list-style: none;
}

.infinite-list-wrapper .list-item {
  display: flex;
  align-items: center;
  /* justify-content: center; */
  /* color: var(--el-color-danger); */
}
.infinite-list-wrapper .list-item + .list-item {
  margin-top: 10px;
}

.book-detail-root {
  padding: 28px;
  background: linear-gradient(160deg, #fbfdff 0%, #f1f6f5 100%);
  min-height: 100vh;
  box-sizing: border-box;
}

.book-card {
  border-radius: 14px;
  padding: 20px;
  max-width: 1100px;
  margin: 0 auto;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 10px 30px rgba(19, 24, 28, 0.06);
  transition: transform 0.18s ease;
}

.card-body {
  align-items: flex-start;
}

/* cover */
.cover-col {
  display: flex;
  justify-content: center;
}

.cover-wrap {
  width: 100%;
  max-width: 320px;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
  transition: box-shadow 0.18s ease, transform 0.18s ease;
}

.cover-wrap:hover {
  box-shadow: 0 12px 30px rgba(16, 24, 32, 0.08);
}

.cover-img {
  width: 100%;
  height: calc(320px * 1.333); /* maintain book-like aspect */
  object-fit: cover;
  display: block;
  transition: transform 0.25s ease;
}

/* placeholder */
.cover-placeholder {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #aab8b6;
  background: linear-gradient(
    180deg,
    rgba(255, 255, 255, 0),
    rgba(255, 255, 255, 0.25)
  );
}

.placeholder-icon {
  font-size: 36px;
  margin-bottom: 8px;
}

.placeholder-text {
  font-size: 12px;
}

/* meta area */
.meta-top {
  padding: 6px 8px;
}

.title-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.book-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0;
  color: #222;
  line-height: 1.1;
}

.new-badge {
  --el-badge-background-color: #e7f6ef;
  --el-badge-text-color: #2b8a5a;
  font-weight: 600;
}

/* meta row */
.meta-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-top: 10px;
  flex-wrap: wrap;
}

.author {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #4b5560;
  font-size: 14px;
}

.author-name {
  font-weight: 600;
  color: #2b3440;
}

.divider-dot {
  margin: 0 6px;
  color: #c7d0ce;
}

.book-id {
  color: #6b6f74;
  font-size: 13px;
}

/* rating */
.rating {
  display: flex;
  align-items: center;
  gap: 8px;
}

.rating-value {
  font-weight: 600;
  color: #2b3440;
}

.feedback-count {
  color: #7d8791;
  font-size: 13px;
}

.synopsis-title {
  margin: 0 0 8px 0;
  color: #2b3440;
  font-size: 16px;
  font-weight: 600;
}

.synopsis-text {
  color: #444b50;
  line-height: 1.6;
  max-height: 9.6em; /* limit */
  overflow: hidden;
  text-overflow: ellipsis;
}

.synopsis-empty {
  color: #9aa6a3;
}

/* actions */
.actions-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 18px;
}

.spacer {
  flex: 1;
}

/* small screens tweaks */
@media (max-width: 768px) {
  .book-card {
    padding: 14px;
  }
  .book-title {
    font-size: 20px;
  }
  .cover-img {
    height: 360px;
  }
}
</style>
