<template>
  <div class="home">
    <StartStopButtons
      :started="isRunning"
      :disabled="!connectionOk"
      @start="onStart"
      @stop="onStop"
    />
    <PositionInput
      :lat="lat"
      :lng="lng"
      :readonly="isRunning"
      @update:lat="lat = $event"
      @update:lng="lng = $event"
      :heading="heading"
    />

    <RadarVue
      :reference="{ lat: lat, lng: lng, heading: heading }"
      :points="points"
    />

    <ToastNotification ref="toast" />
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, onUnmounted } from "vue";
import StartStopButtons from "@/components/StartStopButtons.vue";
import PositionInput from "@/components/PositionInput.vue";
import ToastNotification from "@/components/ToastNotification.vue";
import RadarVue from "@/components/RadarVue.vue";
import { API, Position } from "@/api";

const isRunning = ref(false);
const lat = ref(43.6);
const lng = ref(6.9);
const heading = ref(0);
const toast = ref<InstanceType<typeof ToastNotification> | null>(null);
const connectionOk = ref(false);
const points = ref<{ distance: number; angle: number }[]>([]);

let poller: number | undefined = undefined;
let eventSource: EventSource | undefined;

// Start
async function onStart() {
  try {
    API.start();
    isRunning.value = true;
    toast.value?.showToast("Application démarrée", "info");
  } catch {
    toast.value?.showToast("Erreur lors du démarrage", "error");
  }
}

// Stop
async function onStop() {
  try {
    API.stop();
    isRunning.value = false;
    toast.value?.showToast("Application arretee", "info");
  } catch (error) {
    toast.value?.showToast("Erreur lors de l'arret", "error");
  }
}

// ➕ Connexion SSE
function connectSSE() {
  eventSource = API.stream();

  eventSource.addEventListener("point", (e) => {
    try {
      const data = JSON.parse((e as MessageEvent).data);
      points.value = [
        {
          distance: data.radiusMeter,
          angle: data.angleDegree,
        },
      ]; // Ajoute le point à la liste
    } catch (err) {
      console.error("Erreur parsing SSE point", err);
    }
  });

  eventSource.onerror = () => {
    toast.value?.showToast("Perte de connexion SSE", "error");
    eventSource?.close();
    eventSource = undefined;
  };
}

// Polling
async function pollStatus() {
  try {
    const res = await API.status();
    if (!res.ok) throw new Error();
    const status = await res.text();
    isRunning.value = status.trim() === "started";

    if (isRunning.value) {
      const position = await API.position();
      if (!position.ok) throw new Error();
      const pos: Position = await position.json();
      lat.value = pos.latDegrees;
      lng.value = pos.lonDegrees;
      heading.value = pos.headingDegrees;
    } else {
      points.value = [];
    }

    connectionOk.value = true;
  } catch {
    toast.value?.showToast("Connexion échouée", "error");
    connectionOk.value = false;
  }
}

// Démarrer/arrêter le polling
onMounted(() => {
  pollStatus();
  poller = window.setInterval(pollStatus, 3000);
  connectSSE();
});

onUnmounted(() => {
  if (poller) clearInterval(poller);
  if (eventSource) eventSource.close();
});
</script>

<style scoped>
.home {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}
</style>
