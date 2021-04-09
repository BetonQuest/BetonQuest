//Adds an EventListener to all "chain link" elements next to headings.
let links = document.getElementsByClassName("headerlink");
for (let i = 0; i < links.length; i++) {
  links[i].addEventListener("click", onClick);
}

//Copies the current URL into the users clipboard.
//This needs to be delayed by a bit since the browser needs to update the URL before it's copied.
function onClick() {
  setTimeout(() => {
    let url = window.location.href;
    navigator.clipboard.writeText(url);
  }, 10);
}
