function close_nav() {
    document.getElementById("nav").style.display = "none";
}

function open_nav() {
    document.getElementById("nav").style.display = "flex";
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