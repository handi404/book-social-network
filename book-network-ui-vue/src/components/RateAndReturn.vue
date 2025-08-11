<template>
  <el-card shadow="always" class="feedback-card">
    <h2>Return and share feedback</h2>

    <el-form label-width="120px" label-position="left">
      <el-form-item label="Title"> Spring boot in action </el-form-item>
      <el-form-item label="Author"> Alibou </el-form-item>
      <el-form-item label="ISBN"> 123456789 </el-form-item>

      <el-form-item label="Rate">
        <el-row :gutter="15">
          <el-col :span="12">
            <el-slider v-model="rating" :max="5" />
          </el-col>
          <el-col :span="12" class="text-right">
            <el-rate
              v-model="rating"
              :max="5"
              disabled
              score-template="{value}"
              icon-size="18"
            />
          </el-col>
        </el-row>
      </el-form-item>
    </el-form>

    <el-input
      type="textarea"
      :rows="4"
      placeholder="Enter your feedback..."
      v-model="comment"
      class="mt-4"
    />

    <div class="action-buttons mt-6">
      <el-button type="primary" @click="submit" class="mr-3" :icon="Document">
        Rate the book & Return
      </el-button>
      <el-button @click="justReturn" class="mr-3" :icon="ArrowRight">
        Just Return
      </el-button>
      <el-button @click="cancel" type="danger" :icon="CloseBold">
        Cancel
      </el-button>
    </div>
  </el-card>
</template>

<script setup>
import { ref } from "vue";
import { Document, ArrowRight, CloseBold } from "@element-plus/icons";

const rating = ref(0);
const comment = ref("");

const submit = () => {
  // 提交逻辑
  console.log("Submitted:", { rating: rating.value, comment: comment.value });
};

const justReturn = () => {
  // 直接返回逻辑
  console.log("Just Return");
};

const cancel = () => {
  // 取消操作
  console.log("Canceled");
};
</script>

<style scoped>
.feedback-card {
  max-width: 600px;
}

.text-right {
  text-align: right;
}

.mt-4 {
  margin-top: 20px;
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
