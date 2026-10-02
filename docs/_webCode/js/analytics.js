import { init } from "https://esm.sh/@faststats/web@0.11";
import { errorTracking } from "https://esm.sh/@faststats/web@0.11/error";
import { sessionReplay } from "https://esm.sh/@faststats/web@0.11/replay";
import { webVitals } from "https://esm.sh/@faststats/web@0.11/web-vitals";

function getConsentMode() {
  const consent = globalThis.__md_get("__consent");
  if (!consent) return "anonymous";

  return consent.analytics ? "granted" : "denied";
}

init({
  siteKey: "50d515577b9fff402b3b07c8c777f751",
  consent: getConsentMode(),
  extensions: [
    errorTracking(),
    webVitals(),
    sessionReplay(),
  ],
});
