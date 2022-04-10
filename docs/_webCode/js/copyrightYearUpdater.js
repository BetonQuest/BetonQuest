const copyrightElement = document.getElementsByClassName("md-copyright__highlight");
const currentYear = new Date().getFullYear().toString();
//Assumes that there is only one copyright footer
copyrightElement.item(0).textContent = "© 2014-" + currentYear + "  BetonQuest Organisation. GPLv3";
