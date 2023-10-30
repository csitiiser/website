$(document).ready(function() {
  $("#header").load("./header.html");
  $("#footer").load("./footer.html");
  $("#bottom-nav").load("./nav.html");
  $("#nav").load("./nav.html");
});

function close_nav() {
    document.getElementById("nav").style.display = "none";
}

function open_nav() {
    document.getElementById("nav").style.display = "flex";
}

function toggle(x) {
  classname = '#' + x;
  $(classname).slideToggle('slow'); 
}

const observerOptions = {
    root: null,
    rootMargin: "0px",
    threshold: 0.7
  };
  
  function observerCallback(entries, observer) {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.replace('fadeOut', 'fadeIn');
      } else {
        entry.target.classList.replace('fadeIn', 'fadeOut');
      }
    });
  }
  
  const observer = new IntersectionObserver(observerCallback, observerOptions);
  
  const fadeElms = document.querySelectorAll('.fade');
  fadeElms.forEach(el => observer.observe(el));