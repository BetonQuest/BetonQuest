const currentDomain = window.location.origin;
const links = document.querySelectorAll(".md-content__inner a");

links.forEach(link => {
  const href = link.href;
  if (!href.startsWith(currentDomain)) {
    link.classList.add("external-link");
  }
});
