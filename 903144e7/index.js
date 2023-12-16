$(document).ready(function() {
  $("#header").load("header.html");
  $("#footer").load("footer.html");
});

function close_nav() {
    document.getElementById("nav").style.display = "none";
}

function open_nav() {
    document.getElementById("nav").style.display = "flex";
    console.log("open");
}

function toggle(x) {
  classname = '#' + x;
  $(classname).slideToggle('slow'); 
}

function closeOpenDropdowns(e) {
	let openDropdownEls = document.querySelectorAll("details.dropdown[open]");

	if (openDropdownEls.length > 0) {
		// If we're clicking anywhere but the summary element, close dropdowns
		if (e.target.parentElement.nodeName.toUpperCase() !== "SUMMARY") {
			openDropdownEls.forEach((dropdown) => {
				dropdown.removeAttribute("open");
			});
		}
	}
}

document.addEventListener("click", closeOpenDropdowns);