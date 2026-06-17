// Import FastStats Web Analytics using ESM version 0.3.x (latest patch).
import { WebAnalytics } from "https://esm.sh/@faststats/web@0.3";

function getConsentMode() {
  const consent = globalThis.__md_get("__consent");
  if (!consent) return "pending";

  // "denied" currently puts analytics in cookieless mode until a full consent manager exists
  return consent.analytics ? "granted" : "denied";
}

new WebAnalytics({
  siteKey: "",
  trackErrors: true,
  webVitals: { enabled: true },
  consent: {
    mode: getConsentMode(),
    cookielessWhilePending: true,
  },
});
