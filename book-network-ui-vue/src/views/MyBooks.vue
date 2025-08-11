<template>
  <div>
    <h3>My books</h3>
    <el-divider />
    <div class="mb-2">
      <el-button
        type="primary"
        plain
        :icon="Plus"
        @click="() => router.push('/books/manage')"
      >
        New book
      </el-button>
    </div>
    <div class="book-list">
      <book-card
        v-for="book in bookResponse.content"
        :key="book.id"
        :book="book"
        :manage="true"
      />
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
  </div>
</template>

<script setup lang="ts">
import BookCard from "@/components/BookCard.vue";
import type {
  BookResponse,
  PageResponseBookResponse,
} from "@/client/types.gen";
import { Book } from "@/client";
import { inject, onMounted, ref } from "vue";
import { Plus } from "@element-plus/icons-vue";
import { RouterLink } from "vue-router";
import router from "@/router";

const pageSize = ref(2);
const currentPage = ref(1);
const totalPages = ref(100);
const bookResponse = ref<PageResponseBookResponse>({});

// 当页码变化时触发
const handleCurrentChange = (newPage: number) => {
  console.log("当前页:", newPage); // newPage 就是用户点击的页码
  currentPage.value = newPage; // 可选：同步更新绑定值

  // 在这里调用接口获取数据
  // fetchData(newPage, pageSize)
  try {
    Book.findAllBooksByOwner({
      query: {
        page: newPage - 1,
        size: pageSize.value,
      },
    }).then((response) => {
      bookResponse.value = response.data || {};
      console.log(bookResponse.value);
      totalPages.value = response.data?.totalPages || 0;
    });
  } catch (error) {
    console.log(error);
  }
};

// 组件挂载时获取数据
onMounted(async () => {
  handleCurrentChange(currentPage.value);
});
</script>

<style scoped>
.example-pagination {
  display: flex;
  justify-content: center;
  margin-top: 90px;
}
.book-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}
.mb-2 {
  margin-bottom: 20px;
}
.mb-2 > .el-button {
  margin-left: 90%;
}
</style>
