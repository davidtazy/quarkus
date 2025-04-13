<template>
  <canvas ref="canvas" class="radar"></canvas>
  <dl class="properties">
    <dt>Distance max:</dt>
    <dd>
      <input type="number" v-model="range" class="input_number" />
      <p>m</p>
    </dd>
    <dt>Point duration:</dt>
    <dd>
      <input type="number" v-model="pointDuration" class="input_number" />
      <p>s</p>
    </dd>
    <dt>Draw mode:</dt>
    <dd>
      <select v-model="drawMode" class="input_number">
        <option value="absolute">absolute</option>
        <option value="relative">relative</option>
      </select>
    </dd>
  </dl>
</template>

<script lang="ts" setup>
import { ref, onMounted, onUnmounted, watch } from "vue";

type PolarPoint = {
  distance: number; // en mètres
  angle: number; // en degrés (relatif au cap)
  timestamp: number; // Date.now()
};

const props = defineProps<{
  reference: {
    lat: number;
    lng: number;
    heading: number; // cap en degrés (0 = vers le haut)
  };
  points: { distance: number; angle: number }[];
}>();

const canvas = ref<HTMLCanvasElement | null>(null);
const livePoints = ref<PolarPoint[]>([]);
const range_max = 1000; // distance max à afficher (en mètres)
const range = ref(range_max); // distance max à afficher (en mètres)
const pointDuration = ref(2); // durée d'affichage des points (en secondes)
const heading = ref(props.reference.heading); // cap en degrés (0 = vers le haut)
const drawMode = ref("absolute"); // mode de dessin (absolute ou relative)
let animationFrame: number;

// Ajouter des points avec un timestamp
watch(
  () => props.points,
  (newPoints) => {
    const now = Date.now();
    newPoints.forEach((pt) => {
      livePoints.value.push({ ...pt, timestamp: now });
    });
  },
  { deep: true }
);

function draw() {
  if (drawMode.value === "absolute") {
    heading.value = props.reference.heading;
  } else {
    heading.value = 0;
  }

  const ctx = canvas.value?.getContext("2d");
  if (!ctx || !canvas.value) return;

  const width = (canvas.value.width = canvas.value.offsetWidth);
  const height = (canvas.value.height = canvas.value.offsetHeight);
  const centerX = width / 2;
  const centerY = height / 2;
  const radius = Math.min(width, height) / 2;
  ctx.clearRect(0, 0, width, height);

  // Fond radar
  drawBackground(ctx, centerX, centerY, radius);

  // Nettoyer les points expirés (2s)
  const now = Date.now();
  livePoints.value = livePoints.value.filter(
    (p) => now - p.timestamp < pointDuration.value * 1000
  );

  // Dessiner le porteur triangle pointant vers le heading
  drawCenter(heading.value, radius, centerX, centerY, ctx);

  // Dessiner les points
  livePoints.value.forEach((pt) => {
    drawPoint(pt, heading.value, centerX, centerY, radius, ctx, range.value);
  });

  animationFrame = requestAnimationFrame(draw);
}

onMounted(() => {
  draw();
});

onUnmounted(() => {
  cancelAnimationFrame(animationFrame);
});

function drawPoint(
  pt: PolarPoint,
  heading: number,
  centerX: number,
  centerY: number,
  radius: number,
  ctx: CanvasRenderingContext2D,
  range: number
) {
  // Convertir la distance en pixels

  if (pt.distance > range) return; // hors radar

  // Calculer la position du point
  const relativeAngleRad = ((pt.angle + heading) * Math.PI) / 180;
  const maxDisplayDistance = range;

  const r = (pt.distance / maxDisplayDistance) * radius;
  if (r > radius) return; // hors radar

  const x = centerX + r * Math.sin(relativeAngleRad);
  const y = centerY - r * Math.cos(relativeAngleRad);

  ctx.beginPath();
  ctx.arc(x, y, 4, 0, 2 * Math.PI);
  ctx.fillStyle = "#ff3366";
  ctx.fill();
}

function drawCenter(
  heading: number,
  radius: number,
  centerX: number,
  centerY: number,
  ctx: CanvasRenderingContext2D
) {
  const headingRad = ((heading - 90) * Math.PI) / 180;
  const headingLength = radius * 0.2;
  const headingX = centerX + headingLength * Math.cos(headingRad);
  const headingY = centerY + headingLength * Math.sin(headingRad);
  const leftX =
    centerX + (headingLength / 4) * Math.cos(headingRad + Math.PI / 3);
  const leftY =
    centerY + (headingLength / 4) * Math.sin(headingRad + Math.PI / 3);
  const rightX =
    centerX + (headingLength / 4) * Math.cos(headingRad - Math.PI / 3);
  const rightY =
    centerY + (headingLength / 4) * Math.sin(headingRad - Math.PI / 3);
  ctx.beginPath();
  ctx.moveTo(headingX, headingY);
  ctx.lineTo(leftX, leftY);
  ctx.lineTo(rightX, rightY);
  ctx.closePath();
  ctx.fillStyle = "#00ffcc";
  ctx.fill();
  ctx.strokeStyle = "#00ffcc";
  ctx.stroke();
}

function drawBackground(
  ctx: CanvasRenderingContext2D,
  centerX: number,
  centerY: number,
  radius: number
) {
  ctx.beginPath();
  ctx.arc(centerX, centerY, radius, 0, 2 * Math.PI);
  ctx.fillStyle = "#001a1a";
  ctx.fill();
  ctx.strokeStyle = "#00ffcc";
  ctx.stroke();
}
</script>

<style scoped>
.radar {
  width: 600px;
  height: 600px;
  background-color: black;
  border-radius: 50%;
  display: block;
}

.radar-info {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 0.5rem;
}
.input_number {
  width: 100px;
  padding: 0.5rem;
  border-radius: 4px;
  border: 1px solid #ccc;
  font-size: 1rem;
}
.input_number:focus {
  outline: none;
  border-color: #00ffcc;
}
.properties {
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
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin: 0;
}
</style>
