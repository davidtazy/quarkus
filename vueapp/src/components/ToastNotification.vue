<template>
  <div v-if="visible" class="toast" :class="type">
    {{ message }}
  </div>
</template>

<script lang="ts" setup>
import { ref } from "vue";

const visible = ref(false);
const message = ref("");
const type = ref<"info" | "error">("info");
let timeout: number | undefined;

function showToast(
  msg: string,
  toastType: "info" | "error" = "info",
  duration = 3000
) {
  message.value = msg;
  type.value = toastType;
  visible.value = true;

  clearTimeout(timeout);
  timeout = window.setTimeout(() => {
    visible.value = false;
  }, duration);
}

// expose la fonction showToast pour l'extérieur
defineExpose({ showToast });
</script>

<style scoped>
.toast {
  position: fixed;
  bottom: 2rem;
  left: 50%;
  transform: translateX(-50%);
  padding: 1rem 1.5rem;
  border-radius: 6px;
  color: white;
  font-weight: bold;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  z-index: 999;
  animation: fade-in 0.3s ease;
}

.toast.info {
  background-color: #4caf50;
}

.toast.error {
  background-color: #f44336;
}

@keyframes fade-in {
  from {
    opacity: 0;
    transform: translateX(-50%) translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
}
</style>
