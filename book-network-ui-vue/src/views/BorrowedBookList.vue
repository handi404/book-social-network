<script setup lang="ts">
import { ref, onMounted } from "vue";
import router from "@/router";
import type {
  BorrowedBookResponse,
  PageResponseBorrowedBookResponse,
  FeedbackRequest,
  BorrowBookResponses,
} from "@/client";
import { Book, Feedback } from "@/client";
import { Document, Promotion, CloseBold } from "@element-plus/icons-vue";

const pageSize = ref(2);
const currentPage = ref(1);
const totalPages = ref(100);
const feedbackRequest = ref<FeedbackRequest>({
  bookId: 0,
  comment: "",
  note: 0,
});
const borrowedBookResponse = ref<PageResponseBorrowedBookResponse>({});
const selectedBook = ref<BorrowedBookResponse | undefined>(undefined);

// 当页码变化时触发
const handleCurrentChange = (newPage: number) => {
  console.log("当前页:", newPage); // newPage 就是用户点击的页码
  currentPage.value = newPage; // 可选：同步更新绑定值
  // 在这里调用接口获取数据
  // fetchData(newPage, pageSize)
  try {
    Book.findAllBorrowedBooks({
      query: {
        page: newPage - 1,
        size: pageSize.value,
      },
    }).then((response) => {
      borrowedBookResponse.value = response.data || {};
      console.log(borrowedBookResponse.value);
      totalPages.value = response.data?.totalPages || 0;
    });
  } catch (error) {
    console.log(error);
  }
};

const returnBorrowedBook = (book: BorrowedBookResponse) => {
  selectedBook.value = book;
  feedbackRequest.value.bookId = book.id as number;
  console.log(selectedBook.value);
};
const returnBook = (withFeedback: boolean) => {
  Book.returnBorrowBook({
    path: {
      "book-id": selectedBook.value?.id as number,
    },
  }).then(() => {
    if (withFeedback) {
      giveFeedback();
    }
  });
};

const giveFeedback = () => {
  Feedback.saveFeedback({
    body: feedbackRequest.value,
  }).then(() => {
    selectedBook.value = undefined;
    handleCurrentChange(currentPage.value);
  });
};

const cancel = () => {
  selectedBook.value = undefined;
};
// 组件挂载时获取数据
onMounted(async () => {
  handleCurrentChange(currentPage.value);
});
</script>

<template>
  <div>
    <h3>Borrowed book list</h3>
    <el-divider />
    <div v-if="selectedBook" class="feedback">
      <el-card shadow="always" class="feedback-card">
        <h2>Return and share feedback</h2>

        <el-form label-width="120px" label-position="left">
          <el-form-item label="Title">{{ selectedBook.title }}</el-form-item>
          <el-form-item label="Author">
            {{ selectedBook.authorName }}
          </el-form-item>
          <el-form-item label="ISBN"> {{ selectedBook.isbn }} </el-form-item>
          <el-form-item label="Rate"> {{ selectedBook.rate }} </el-form-item>
          <el-divider />
        </el-form>
        <div>
          <el-row :gutter="30" style="align-items: center">
            <el-col :span="10">
              <el-slider
                v-model="feedbackRequest.note"
                :max="5"
                :step="0.1"
                size="small"
              />
            </el-col>
            <el-col :span="6">
              <el-rate
                v-model="feedbackRequest.note"
                :max="5"
                :disabled="true"
                :show-text="false"
                :allow-half="true"
                text-color="#ff9900"
                score-template="{value}"
                show-score
                disabled-void-color="#c0c4cc"
                icon-size="18"
              />
            </el-col>
          </el-row>
        </div>
        <div>
          <p>Feedback</p>
          <el-input
            type="textarea"
            :rows="4"
            placeholder="Enter your feedback..."
            v-model="feedbackRequest.comment"
          />
        </div>
        <div class="action-buttons mt-6">
          <el-button
            type="primary"
            @click="returnBook(true)"
            class="mr-3"
            :icon="Document"
          >
            Rate the book & Return
          </el-button>
          <el-button @click="returnBook(false)" class="mr-3" :icon="Promotion">
            Just Return
          </el-button>
          <el-button @click="cancel" type="danger" :icon="CloseBold">
            Cancel
          </el-button>
        </div>
      </el-card>
    </div>
    <div v-if="!selectedBook">
      <div>
        <el-table :data="borrowedBookResponse.content" style="width: 100%">
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
              <el-button
                v-if="!scope.row.returned"
                type="danger"
                link
                @click="returnBorrowedBook(scope.row)"
              >
                <el-icon size="20"><Promotion /></el-icon>
              </el-button>
              <el-button v-if="scope.row.returned" type="success" link>
                <el-icon size="20"><Promotion /></el-icon>
              </el-button>
            </template>
          </el-table-column>
          <el-table-column label="Return Approved">
            <template #default="scope">
              <el-button
                :type="scope.row.returnApproved ? 'success' : 'danger'"
                link
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
.feedback {
  display: flex;
  justify-content: center;
}
.feedback-card {
  max-width: 800px;
}

.text-right {
  text-align: right;
}

.mt-6 {
  margin-top: 30px;
}

.mr-3 {
  margin-right: 15px;
}

:deep(.el-rate__text) {
  margin-left: 8px;
}

:deep(.el-slider__bar) {
  background-color: #409eff !important;
}
</style>
