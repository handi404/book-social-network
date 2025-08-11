<template>
  <div>
    <h3>Manage book</h3>
    <el-divider />
    <div class="manage-book-container">
      <el-card shadow="hover" class="form-card">
        <h2 class="form-title">Manage my book</h2>

        <el-row :gutter="30" class="form-body">
          <!-- 图片上传 -->
          <el-col :span="8" class="image-section">
            <el-image :src="selectedPicture" fit="contain" class="book-image" />
            <el-upload
              class="upload-btn"
              action="#"
              :auto-upload="false"
              :show-file-list="false"
              :on-change="handleFileChange"
            >
              <el-button type="primary" plain>Choose file</el-button>
            </el-upload>
          </el-col>

          <!-- 表单区域 -->
          <el-col :span="16">
            <el-form
              :model="bookRequest"
              label-position="top"
              class="book-form"
            >
              <el-form-item label="Title">
                <el-input
                  v-model="bookRequest.title"
                  placeholder="Book title"
                />
              </el-form-item>

              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="Author name">
                    <el-input
                      v-model="bookRequest.authorName"
                      placeholder="Author name"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="ISBN">
                    <el-input v-model="bookRequest.isbn" placeholder="ISBN" />
                  </el-form-item>
                </el-col>
              </el-row>

              <el-form-item label="Synopsis">
                <el-input
                  type="textarea"
                  :rows="5"
                  v-model="bookRequest.synopsis"
                  placeholder="Book synopsis"
                />
              </el-form-item>

              <el-form-item>
                <el-checkbox v-model="bookRequest.shareable"
                  >Share me</el-checkbox
                >
              </el-form-item>

              <div class="form-actions">
                <el-button type="primary" @click="saveBook">
                  <el-icon><Upload /></el-icon> Save
                </el-button>
                <el-button type="danger" text @click="cancel">Cancel</el-button>
              </div>
            </el-form>
          </el-col>
        </el-row>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { Upload } from "@element-plus/icons-vue";
import { Book } from "@/client";
import type { BookRequest } from "@/client/types.gen";
import { useRoute } from "vue-router";
import router from "@/router";

const route = useRoute();
const bookRequest = ref<BookRequest>({
  title: "",
  authorName: "",
  isbn: "",
  synopsis: "",
});
// 上传的图片
const selectedBookCover = ref();
// 预览图片的 base64 字符串
const selectedPicture = ref<string | undefined>("");

// 传入的 file 通过 any 来处理，因为文件上传库类型定义不太完善
const handleFileChange = (file: any) => {
  selectedBookCover.value = file.raw;
  console.log(selectedBookCover.value);
  if (selectedBookCover.value) {
    const reader = new FileReader();
    reader.onload = (e) => {
      // 将文件内容转换为 base64 字符串
      selectedPicture.value = e.target?.result as string;
      // console.log(selectedPicture.value);
    };
    reader.readAsDataURL(selectedBookCover.value);
  }
};

const saveBook = () => {
  Book.save({
    body: bookRequest.value,
  }).then((bookId) => {
    Book.uploadBookCoverPicture({
      path: {
        "book-id": bookId.data as number,
      },
      body: {
        file: selectedBookCover.value,
      },
    }).then(() => {
      router.push("/my-books");
    });
  });
  // console.log("保存数据", bookRequest.value);
};

const cancel = () => {
  router.push("/my-books");
  console.log("取消操作");
};

onMounted(() => {
  const bookId = route.params.bookId as any;
  if (bookId) {
    Book.findBookById({
      path: {
        "book-id": bookId as number,
      },
    })
      .then((book) => {
        bookRequest.value = {
          id: book.data?.id,
          title: book.data?.title as string,
          authorName: book.data?.authorName as string,
          isbn: book.data?.isbn as string,
          synopsis: book.data?.synopsis as string,
          shareable: book.data?.shareable,
        };
        selectedPicture.value = "data:image/jpg;base64," + book.data?.cover;
      })
      .catch((error) => {
        console.error("获取书籍信息失败", error);
      });
  }
});
</script>

<style scoped>
.manage-book-container {
  padding: 40px;
  background: linear-gradient(135deg, #f8fafc, #eef2f3);
  min-height: 100vh;
}

.form-card {
  padding: 20px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.05);
}

.form-title {
  font-size: 1.8rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
}

.image-section {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.book-image {
  width: 100%;
  height: 350px;
  object-fit: cover;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  margin-bottom: 12px;
}

.upload-btn {
  margin-top: 8px;
}

.book-form {
  padding: 10px 0;
}

.form-actions {
  display: flex;
  gap: 16px;
  margin-top: 20px;
}
</style>
