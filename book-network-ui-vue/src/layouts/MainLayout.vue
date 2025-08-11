<script lang="ts" setup>
import Menu from "@/components/Menu.vue";
</script>

<template>
  <div>
    <header>
      <Menu />
    </header>
    <main>
      <RouterView />
    </main>
    <!-- <footer class="footer">
      <p>@ 2025</p>
    </footer> -->
  </div>
</template>

<style scoped>
.footer {
  padding: 1rem;
  background-color: #f0f0f0;
  text-align: center;
  /*  */
}
</style>
