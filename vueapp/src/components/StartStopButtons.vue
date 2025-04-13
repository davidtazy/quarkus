<template>
  <div class="button-group">
    <button
      :class="['button', isStartDisabled ? 'disabled' : 'start']"
      @click="$emit('start')"
    >
      Start
    </button>
    <button
      :class="['button', isStopDisabled ? 'disabled' : 'stop']"
      @click="$emit('stop')"
    >
      Stop
    </button>
  </div>
</template>

<script lang="ts" setup>
import { ref, watch } from "vue";

const props = defineProps<{
  started: boolean;
  disabled: boolean;
}>();

defineEmits<{
  (e: "start"): void;
  (e: "stop"): void;
}>();

const isStartDisabled = ref(true);
const isStopDisabled = ref(true);

// Synchronise les états internes en fonction des props
watch(
  () => [props.started, props.disabled],
  ([started, disabled]) => {
    isStartDisabled.value = started || disabled;
    isStopDisabled.value = !started || disabled;
  },
  { immediate: true }
);
</script>

<style scoped>
.button-group {
  display: flex;
  gap: 1rem;
}

.button {
  padding: 0.6rem 1.4rem;
  font-size: 1rem;
  border: none;
  border-radius: 6px;
  transition: background-color 0.2s ease;
  cursor: pointer;
  color: white;
}

.start {
  background-color: #4caf50;
}

.stop {
  background-color: #f44336;
}

.disabled {
  background-color: #ccc;
  color: #666;
  cursor: not-allowed;
}
</style>
