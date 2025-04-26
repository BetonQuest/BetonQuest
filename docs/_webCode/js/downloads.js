// For this script to work on GitHub Pages, you need to ensure CORS is set up correctly on your Nexus server.
// For local testing, you need to disable CORS in your browser,
// and you need to set the nexusUrl to the actual URL of your Nexus server including the target repository
// (like in your pom.xml).
// E.g.: https://nexus.betonquest.org/repository/betonquest/

document$.subscribe(async () => {
  const nexusUrl = "${REPOSITORY_URL}";
  const parts = nexusUrl.split("/");
  const baseUrl = parts.slice(0, -3).join("/") + "/";
  const repositoryName = parts[parts.length - 2];

  window.onload = async function () {
    let urlParams = new URLSearchParams(window.location.search);
    const path = urlParams.get("path");
    if (path) {
      const url = nexusUrl + path;
      const filename = urlParams.get("filename");
      await downloadWithRename(url, filename);
      window.location.href = window.location.href.split("?")[0];
    }
  };

  await showBuilds();

  async function showBuilds() {
    const builds = await getBuilds();

    let cachedVersions = {};
    try {
      cachedVersions = JSON.parse(localStorage.getItem("cachedVersions"));
    } catch (e) {
      console.log("Failed to parse cached versions");
    }
    const newCachedVersions = {};

    await loadBuilds("release-build", builds.filter(build => !build.version.includes("-")));
    await loadBuilds("development-build", builds.filter(build => build.version.includes("-")), cachedVersions, newCachedVersions);

    localStorage.setItem("cachedVersions", JSON.stringify(newCachedVersions));
  }

  async function loadBuilds(idKey, builds, cachedVersions, newCachedVersions) {
    const latestBuild = document.getElementsByClassName("download-latest-" + idKey)[0];
    if (builds.length > 0) {
      latestBuild.textContent = await getLazyVersion(builds[0], cachedVersions, newCachedVersions);
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
    if (builds.length > 0) {
      const ul = document.createElement("ul");
      buildList.appendChild(ul);
      for (const build of builds) {
        const li = document.createElement("li");
        li.style.cssText = "padding: 0";
        const a = document.createElement("a");
        a.textContent = await getLazyVersion(build, cachedVersions, newCachedVersions);
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
      resetDisabled(buildList.parentNode);
    }
  }

  async function getLazyVersion(build, cachedVersions, newCachedVersions) {
    debugger;
    if (!build.version.includes("-")) {
      return build.version;
    }
    let version;
    if (cachedVersions[build.version]) {
      version = cachedVersions[build.version];
    } else {
      const result = await getPomResult(build);
      if (!result) {
        return null;
      }
      version = result[1];
    }
    newCachedVersions[version] = version;
    return version;
  }

  async function getPomResult(build) {
    const pomUrl = build.downloadUrl.replace("-shaded.jar", ".pom");
    const pomResponse = await fetch(pomUrl);
    const pomData = await pomResponse.text();
    return pomData.match(/<betonquest\.version>(.+?)<\/betonquest\.version>/);
  }

  function resetDisabled(element) {
    element.style.pointerEvents = "auto";
    element.style.opacity = "1";
  }

  async function getBuilds() {
    let prereleaseSearch = undefined;
    let continuationToken = undefined;
    const builds = [];

    nextPage: while (continuationToken !== null) {
      const params = getURLParams(prereleaseSearch, continuationToken);
      try {
        let data = await fetch(baseUrl + `service/rest/v1/search/assets?${params}`)
          .then(response => response.json());

        for (const build of data["items"]) {
          const buildVersion = {version: build["maven2"]["version"], downloadUrl: build["downloadUrl"]};
          if (buildVersion) {
            builds.push(buildVersion);
            if (prereleaseSearch === undefined && !buildVersion.version.includes("-")) {
              prereleaseSearch = false;
              continuationToken = undefined;
              continue nextPage;
            }
          }
        }

        continuationToken = data["continuationToken"];
      } catch (error) {
        console.error("Failed to fetch builds:", error);
        continuationToken = null;
      }
    }
    return builds;
  }

  function getURLParams(prerelease, continuationToken) {
    const params = new URLSearchParams();
    params.set("repository", repositoryName);
    params.set("group", "org.betonquest");
    params.set("name", "betonquest");
    params.set("maven.extension", "jar");
    params.set("maven.classifier", "shaded");
    params.set("sort", "version");

    if (prerelease !== undefined) {
      params.set("prerelease", prerelease);
    }
    if (continuationToken !== undefined) {
      params.set("continuationToken", continuationToken);
    }
    return params;
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
