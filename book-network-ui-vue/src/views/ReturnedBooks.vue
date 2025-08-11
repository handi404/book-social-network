<script setup lang="ts">
import { ref, onMounted } from "vue";
import router from "@/router";
import type {
  PageResponseBorrowedBookResponse,
  BorrowedBookResponse,
} from "@/client";
import { Book } from "@/client";
import { Promotion } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";

const pageSize = ref(2);
const currentPage = ref(1);
const totalPages = ref(100);
const returnedBooks = ref<PageResponseBorrowedBookResponse>({});
const message = ref("");

// 当页码变化时触发
const handleCurrentChange = (newPage: number) => {
  console.log("当前页:", newPage); // newPage 就是用户点击的页码
  currentPage.value = newPage; // 可选：同步更新绑定值
  // 在这里调用接口获取数据
  // fetchData(newPage, pageSize)
  try {
    Book.findAllReturnedBooks({
      query: {
        page: newPage - 1,
        size: pageSize.value,
      },
    }).then((response) => {
      returnedBooks.value = response.data || {};
      console.log(returnedBooks.value);
      totalPages.value = response.data?.totalPages || 0;
    });
  } catch (error) {
    console.log(error);
  }
};

const approveBookReturn = (book: BorrowedBookResponse) => {
  ElMessageBox.confirm("是否批准此书籍的归还？", "Tips", {
    distinguishCancelAndClose: true,
    confirmButtonText: "Approve",
    cancelButtonText: "Cancel",
  })
    .then(() => {
      // 处理批准归还逻辑
      Book.approveReturnBorrowBook({
        path: {
          "book-id": book.id as number,
        },
      }).then((response: any) => {
        if (response.status === 200) {
          ElMessage.success("Book return approved successfully!");
          handleCurrentChange(currentPage.value);
        } else {
          ElMessage.error(response.error.error);
        }
      });
      console.log("Book return approved");
    })
    .catch(() => {});
};

// 组件挂载时获取数据
onMounted(async () => {
  handleCurrentChange(currentPage.value);
});
</script>

<template>
  <div>
    <h3>Returned books</h3>
    <el-divider />
    <div>
      <div>
        <el-table :data="returnedBooks.content" style="width: 100%">
          <el-table-column prop="id" label="#" width="180" />
          <el-table-column prop="title" label="Title" width="180" />
          <el-table-column prop="authorName" label="Author" />
          <el-table-column prop="isbn" label="ISBN" />
          <el-table-column label="Rate">
            <template #default="scope">
              <el-rate
                :model-value="scope.row.rate"
                :disabled="true"
                :show-text="false"
                :allow-half="true"
                text-color="#ff9900"
                score-template="{value}"
                show-score
                disabled-void-color="#c0c4cc"
                size="small"
              />
            </template>
          </el-table-column>
          <el-table-column label="Returned">
            <template #default="scope">
              <el-button :type="scope.row.returned ? 'success' : 'danger'" link>
                <el-icon size="20"><Promotion /></el-icon>
              </el-button>
            </template>
          </el-table-column>
          <el-table-column label="Return Approved">
            <template #default="scope">
              <el-button v-if="scope.row.returnApproved" type="success" link>
                <el-icon size="20"><CircleCheckFilled /></el-icon>
              </el-button>
              <el-button
                v-if="!scope.row.returnApproved"
                type="danger"
                link
                @click="approveBookReturn(scope.row)"
              >
                <el-icon size="20"><CircleCheckFilled /></el-icon>
              </el-button>
            </template>
          </el-table-column>
        </el-table>
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
  </div>
</template>

<style scoped>
.example-pagination {
  display: flex;
  justify-content: center;
  margin-top: 90px;
}
</style>
