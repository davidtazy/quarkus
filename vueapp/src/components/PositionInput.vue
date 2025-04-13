<template>
  <dl class="position">
    <dt>Latitude:</dt>
    <dd>
      <input
        type="number"
        :readonly="readonly"
        :value="lat"
        @input="onLatInput"
      />
    </dd>

    <dt>Longitude:</dt>
    <dd>
      <input
        type="number"
        :readonly="readonly"
        :value="lng"
        @input="onLngInput"
      />
    </dd>
    <dt>Heading:</dt>
    <dd>
      <input type="text" readonly="true" :value="`${heading}°`" />
    </dd>
  </dl>
</template>

<script lang="ts" setup>
const props = defineProps<{
  lat: number;
  lng: number;
  heading: number;
  readonly?: boolean;
}>();

const emit = defineEmits<{
  (e: "update:lat", value: number): void;
  (e: "update:lng", value: number): void;
}>();

const onLatInput = (e: Event) => {
  const value = parseFloat((e.target as HTMLInputElement).value);
  emit("update:lat", value);
};

const onLngInput = (e: Event) => {
  const value = parseFloat((e.target as HTMLInputElement).value);
  emit("update:lng", value);
};
</script>

<style scoped>
.position {
  display: grid;
  grid-template-columns: max-content 1fr;
  gap: 0.5rem 1rem;
  align-items: center;
}

dt {
  font-weight: bold;
  margin: 0;
  text-align: right;
}

dd {
  margin: 0;
}

input {
  width: 100%;
  padding: 0.4rem;
  font-size: 1rem;
  border-radius: 4px;
  border: 1px solid #ccc;
}

input[readonly] {
  background-color: #eee;
  color: #666;
}
</style>
