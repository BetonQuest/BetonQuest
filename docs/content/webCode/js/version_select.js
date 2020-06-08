window.addEventListener("DOMContentLoaded", function() {
  const BASE_URL = window.location.origin + "/" + window.location.pathname.split("/")[1];
  const CURRENT_VERSION = window.location.pathname.split("/")[3];

  function makeSelect(options, selected) {
    const select = document.createElement("select");
    select.classList.add("form-control");

    options.forEach(function(i) {
      const option = new Option(i.text, i.value, undefined,
          i.value === selected);
      select.add(option);
    });

    return select;
  }

  const xhr = new XMLHttpRequest();
  xhr.open("GET", BASE_URL + "/versions.json");
  xhr.onload = function() {
    const versions = JSON.parse(this.responseText);

    const realVersion = versions.find(function(i) {
      return i.version === CURRENT_VERSION;
    }).version;

    const select = makeSelect(versions.map(function(i) {
      return {text: i.title, value: i.version};
    }), realVersion);
    select.addEventListener("change", function(event) {
      window.location.href = BASE_URL + "/versions/" + this.value;
    });

    const container = document.createElement("div");
    container.id = "version-selector";
    container.appendChild(select);

    const title = document.querySelector("nav.md-header-nav");
    const height = window.getComputedStyle(title).getPropertyValue("height");
    container.style.height = height;

    title.appendChild(container);
  };
  xhr.send();
});