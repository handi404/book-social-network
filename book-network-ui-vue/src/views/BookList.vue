<template>
  <div>
    <h3>Books list</h3>
    <el-divider />
    <el-text class="text-right" size="small" v-if="message">
      {{ message }}
    </el-text>
    <div class="book-list">
      <book-card
        v-for="book in bookResponse.content"
        :key="book.id"
        :book="book"
        :manage="false"
        v-mode:sync="dialogVisible"
        @borrow="handleBorrow"
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
import { ElMessage, ElMessageBox } from "element-plus";

const pageSize = ref(5);
const currentPage = ref(1);
const totalPages = ref(100);
const bookResponse = ref<PageResponseBookResponse>({});
const dialogVisible = ref(false);
const message = ref("");

// 当页码变化时触发
const handleCurrentChange = (newPage: number) => {
  console.log("当前页:", newPage); // newPage 就是用户点击的页码
  currentPage.value = newPage; // 可选：同步更新绑定值

  // 在这里调用接口获取数据
  // fetchData(newPage, pageSize)
  Book.findAllBooks({
    query: {
      page: newPage - 1,
      size: pageSize.value,
    },
  })
    .then((response) => {
      bookResponse.value = response.data || {};
      console.log(bookResponse.value);
      totalPages.value = response.data?.totalPages || 0;
    })
    .catch((err) => {
      message.value = err.error.message;
    });
};
const handleBorrow = (bookId: number) => {
  message.value = "";
  ElMessageBox.confirm("是否借阅此书籍？", "Tips", {
    distinguishCancelAndClose: true,
    confirmButtonText: "Borrow",
    cancelButtonText: "Cancel",
  })
    .then(() => {
      // 处理借书逻辑
      Book.borrowBook({
        path: {
          "book-id": bookId,
        },
      }).then((response: any) => {
        if (response.status === 200) {
          ElMessage.success("Book borrowed successfully!");
        } else {
          ElMessage.error(response.error.error);
        }
        // console.log(response);
      });
    })
    .catch(() => {});
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
  flex-wrap: wrap; /* 允许换行 */
  gap: 20px; /* 卡片之间的间距 */
  justify-content: center; /* 居中对齐 */
}
</style>
