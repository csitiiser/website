$(document).ready(function() {
  $("#header").load("./header.html");
  $("#footer").load("./footer.html");
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