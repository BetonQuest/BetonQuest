// For this script to work on GitHub Pages, you need to ensure CORS is set up correctly on your Reposilite server.
// For local testing, you need to disable CORS in your browser,
// and you need to set the repoUrl to the actual URL of your Reposilite server including the target repository
// (like in your pom.xml).
// E.g.: https://repo.betonquest.org/betonquest/

document$.subscribe(async () => {
  const repoUrl = "https://repo.betonquest.org/betonquest/";
  const parts = repoUrl.split("/");
  const baseUrl = parts.slice(0, -2).join("/") + "/";

  let hideTimeout = null;
  let fadeTimeout = null;
  let isDownloading = false;

  function setDownloadsDisabled(disabled, activeElement) {
    const buttons = document.querySelectorAll(
      ".download-latest-release-build, .download-latest-development-build, #download-all-release-build a, #download-all-development-build a"
    );
    buttons.forEach((button) => {
      if (disabled) {
        button.style.pointerEvents = "none";
        button.style.opacity = button === activeElement ? "1" : "0.5";
      } else {
        button.style.pointerEvents = "auto";
        button.style.opacity = "1";
      }
    });
  }

  function showDownloadProgress(filename) {
    if (hideTimeout) clearTimeout(hideTimeout);
    if (fadeTimeout) clearTimeout(fadeTimeout);

    const banner = document.getElementById("download-admonition");
    const title = document.getElementById("download-title");
    const progressBar = document.getElementById("download-progress-bar");
    const statusText = document.getElementById("download-status");

    if (!banner) return null;

    banner.className = "admonition info";
    banner.style.display = "block";
    banner.style.opacity = "1";
    if (title) title.textContent = `Downloading: ${filename}`;
    if (progressBar) {
      progressBar.style.width = "0%";
      progressBar.style.background = "var(--md-primary-fg-color, #4051b5)";
    }
    if (statusText) statusText.textContent = "Starting download...";

    return {
      onProgress: ({loaded, total, percent, loadedMb, totalMb}) => {
        if (progressBar) {
          progressBar.style.width = total ? `${percent}%` : "100%";
        }
        if (statusText) {
          statusText.textContent = total ? `${percent}% (${loadedMb} MB / ${totalMb} MB)` : `${loadedMb} MB downloaded`;
        }
      },
      onSuccess: () => {
        if (title) title.textContent = `Download complete: ${filename}`;
        if (progressBar) {
          progressBar.style.width = "100%";
          progressBar.style.background = "#4caf50";
        }
        if (statusText) statusText.textContent = "File has been saved.";
        fadeTimeout = setTimeout(() => {
          banner.style.opacity = "0";
          hideTimeout = setTimeout(() => {
            banner.style.display = "none";
          }, 500);
        }, 4000);
      },
      onError: (error) => {
        if (title) title.textContent = `Download failed: ${filename}`;
        if (progressBar) {
          progressBar.style.background = "#f44336";
        }
        if (statusText) statusText.textContent = error.message || "An error occurred during download.";
      }
    };
  }

  async function triggerDownload(url, filename, statusElement) {
    if (isDownloading) return;
    isDownloading = true;
    setDownloadsDisabled(true, statusElement);
    const progressUi = showDownloadProgress(filename);
    try {
      await downloadWithRename(url, filename, statusElement, progressUi ? progressUi.onProgress : null);
      if (progressUi) progressUi.onSuccess();
    } catch (error) {
      if (progressUi) progressUi.onError(error);
    } finally {
      isDownloading = false;
      setDownloadsDisabled(false);
    }
  }

  handleUrlDownload();
  showBuilds();

  async function handleUrlDownload() {
    let urlParams = new URLSearchParams(window.location.search);
    const path = urlParams.get("path");
    if (!path) return;

    // Clean URL query parameters immediately without page reload
    window.history.replaceState({}, document.title, window.location.pathname);

    const url = repoUrl + path;
    const filename = urlParams.get("filename") || path.split("/").pop();

    await triggerDownload(url, filename, null);
  }

  async function showBuilds() {
    getBuilds("?snapshots=false").then(builds =>
      loadBuilds("release-build", builds));
    getBuilds("?releases=false&limit=100", true).then(builds =>
      loadBuilds("development-build", builds));
  }

  async function loadBuilds(idKey, builds) {
    const latestBuild = document.getElementsByClassName("download-latest-" + idKey)[0];
    if (builds.length > 0) {
      const version = builds[0].version;
      latestBuild.textContent = version;
      const downloadUrl = builds[0].downloadUrl;
      latestBuild.onclick = function (event) {
        if (event) event.preventDefault();
        triggerDownload(downloadUrl, "BetonQuest-" + version + ".jar", latestBuild);
        return false;
      };
      resetDisabled(latestBuild);
    } else {
      latestBuild.textContent = "Nothing was Found";
    }
    builds.shift();

    const buildList = document.getElementById("download-all-" + idKey);
    resetDisabled(buildList.parentNode);
    await loadAllBuilds(builds, buildList);
  }

  async function loadAllBuilds(builds, buildList) {
    if (!buildList) return;
    buildList.innerHTML = "";
    if (builds.length > 0) {
      const buildListContainer = document.createElement("ul");
      buildList.appendChild(buildListContainer);
      for (const build of builds) {
        const listItem = document.createElement("li");
        listItem.style.cssText = "padding: 0";
        const downloadLink = document.createElement("a");
        const version = build.version;
        downloadLink.textContent = version;
        downloadLink.href = "#";
        downloadLink.onclick = function (event) {
          if (event) event.preventDefault();
          triggerDownload(build.downloadUrl, "BetonQuest-" + version + ".jar", downloadLink);
          return false;
        };
        downloadLink.style.cssText = "width: 100%; text-align: center;";
        downloadLink.classList.add("md-button");
        downloadLink.classList.add("md-button--secondary");
        if (isDownloading) {
          downloadLink.style.pointerEvents = "none";
          downloadLink.style.opacity = "0.5";
        }
        listItem.appendChild(downloadLink);
        buildListContainer.appendChild(listItem);
      }
    }
  }

  function resetDisabled(element) {
    if (isDownloading && element.tagName !== "DETAILS") {
      element.style.pointerEvents = "none";
      element.style.opacity = "0.5";
    } else {
      element.style.pointerEvents = "auto";
      element.style.opacity = "1";
    }
  }

  async function getBuilds(filter, firstGroupOnly = false) {
    const builds = [];
    try {
      let buildData = await fetch(baseUrl + `api/pommapper/id/BetonQuest` + filter)
        .then(response => response.json());
      for (const group of buildData) {
        for (const versionEntry of group["versions"]) {
          let pluginVersion = versionEntry["entries"]["pluginVersion"];
          let betonquestVersion = versionEntry["entries"]["betonquestVersion"];
          let downloadUrl = repoUrl + versionEntry["jar"].replace(".jar", "-shaded.jar");
          builds.push({version: pluginVersion ? pluginVersion : betonquestVersion, downloadUrl: downloadUrl});
        }
        if (firstGroupOnly) {
          break;
        }
      }
    } catch (error) {
      console.error("Failed to fetch builds:", error);
    }
    return builds;
  }

  async function downloadWithRename(url, filename, statusElement, onProgress) {
    const originalText = statusElement ? statusElement.textContent : "";
    if (statusElement) {
      statusElement.style.pointerEvents = "none";
      statusElement.textContent = "Downloading...";
    }
    try {
      const response = await fetch(url);
      if (!response.ok) throw new Error(`${response.status} ${response.statusText}`);

      const contentLength = response.headers.get("Content-Length");
      const total = contentLength ? parseInt(contentLength, 10) : 0;
      let loaded = 0;

      const reader = response.body.getReader();
      const chunks = [];

      while (true) {
        const {done, value: chunk} = await reader.read();
        if (done) break;
        chunks.push(chunk);
        loaded += chunk.length;

        const percent = total ? Math.round((loaded / total) * 100) : 0;
        const loadedMb = (loaded / (1024 * 1024)).toFixed(1);
        const totalMb = total ? (total / (1024 * 1024)).toFixed(1) : null;

        if (statusElement) {
          if (total) {
            statusElement.textContent = `Downloading... ${percent}%`;
          } else {
            statusElement.textContent = `Downloading... ${loadedMb} MB`;
          }
        }
        if (onProgress) {
          onProgress({loaded, total, percent, loadedMb, totalMb});
        }
      }

      const blob = new Blob(chunks);
      const downloadAnchor = document.createElement("a");
      downloadAnchor.href = URL.createObjectURL(blob);
      downloadAnchor.download = filename || url.split("/").pop();
      downloadAnchor.click();
      URL.revokeObjectURL(downloadAnchor.href);
      if (statusElement) {
        statusElement.textContent = "Download complete!";
      }
    } catch (error) {
      console.error("Download failed:", error);
      if (statusElement) {
        statusElement.textContent = "Download failed";
      }
      throw error;
    } finally {
      if (statusElement && (statusElement.tagName === "A" || statusElement.tagName === "BUTTON")) {
        setTimeout(() => {
          statusElement.textContent = originalText;
          resetDisabled(statusElement);
        }, 1500);
      }
    }
  }

});
