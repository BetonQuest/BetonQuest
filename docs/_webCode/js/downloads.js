// For this script to work on GitHub Pages, you need to ensure CORS is set up correctly on your Reposilite server.
// For local testing, you need to disable CORS in your browser,
// and you need to set the repoUrl to the actual URL of your Reposilite server including the target repository
// (like in your pom.xml).
// E.g.: https://repo.betonquest.org/betonquest/

document$.subscribe(async () => {
  const repoUrl = "${REPOSITORY_URL}";
  const parts = repoUrl.split("/");
  const baseUrl = parts.slice(0, -2).join("/") + "/";
  const repositoryName = parts[parts.length - 2];

  window.onload = async function () {
    let urlParams = new URLSearchParams(window.location.search);
    const path = urlParams.get("path");
    if (path) {
      const url = repoUrl + path;
      const filename = urlParams.get("filename");
      await downloadWithRename(url, filename);
      window.location.href = window.location.href.split("?")[0];
    }
  };

  await showBuilds();

  async function showBuilds() {
    getBuilds("?snapshots=false").then(builds =>
      loadBuilds("release-build", builds));
    getBuilds("?releases=false&limit=100", true).then(builds =>
      loadBuilds("development-build", builds));
  }

  async function loadBuilds(idKey, builds) {
    const latestBuild = document.getElementsByClassName("download-latest-" + idKey)[0];
    if (builds.length > 0) {
      latestBuild.textContent = builds[0].version;
      const downloadUrl = builds[0].downloadUrl;
      latestBuild.onclick = function () {
        downloadWithRename(downloadUrl, "BetonQuest.jar");
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
    if (builds.length > 0) {
      const ul = document.createElement("ul");
      buildList.appendChild(ul);
      for (const build of builds) {
        const li = document.createElement("li");
        li.style.cssText = "padding: 0";
        const a = document.createElement("a");
        a.textContent = build.version;
        a.href = "#";
        a.onclick = function () {
          downloadWithRename(build.downloadUrl, "BetonQuest.jar");
        };
        a.style.cssText = "width: 100%; text-align: center;";
        a.classList.add("md-button");
        a.classList.add("md-button--secondary");
        li.appendChild(a);
        ul.appendChild(li);
      }
    }
  }

  function resetDisabled(element) {
    element.style.pointerEvents = "auto";
    element.style.opacity = "1";
  }

  async function getBuilds(filter, firstGroupOnly = false) {
    const builds = [];
    try {
      let data = await fetch(baseUrl + `api/pommapper/id/BetonQuest` + filter)
        .then(response => response.json());
      for (const group of data) {
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

  function downloadWithRename(url, filename) {
    return fetch(url).then(async response => {
      if (response.ok) {
        const link = document.createElement("a");
        link.href = URL.createObjectURL(await response.blob());
        link.download = filename ? filename : url.split("/").pop();
        link.click();
      } else {
        console.error("Error while downloading file: " + response.status + " " + response.statusText + "");
      }
    });
  }

});
