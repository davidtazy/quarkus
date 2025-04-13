import { config } from "./config";

const API_URL = config.apiBaseUrl;

export type Position = {
  latDegrees: number;
  lonDegrees: number;
  headingDegrees: number;
  speedKnot: number;
};

export const API = {
  start: () => fetch(`${API_URL}/app/start`, { method: "PUT" }),
  stop: () => fetch(`${API_URL}/app/stop`, { method: "PUT" }),
  status: () => fetch(`${API_URL}/app`),
  position: () => fetch(`${API_URL}/app/position`),
  stream: () => new EventSource(`${API_URL}/app/stream`),
};
