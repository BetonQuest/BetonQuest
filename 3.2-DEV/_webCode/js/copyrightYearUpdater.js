//mkdocs-material provided observable that is called when the page is fully loaded. Respects both default and "instant loading" of pages.
document$.subscribe(function () {
  const copyrightElement = document.getElementById("bqCopyright");
  const currentYear = new Date().getFullYear().toString();
  copyrightElement.textContent = copyrightElement.textContent.replace("{CurrentYear}", currentYear);
});
