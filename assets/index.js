function close_nav() {
	  document.getElementById("nav").style.display = "none";
  }
  
  function open_nav() {
	  document.getElementById("nav").style.display = "flex";
	  //console.log("open");
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
  
  let slideIndex = [1,1];
  let slideId = ["mySlides1", "mySlides2"]
  showSlides(1, 0);
  showSlides(1, 1);
  
  function plusSlides(n, no) {
	showSlides(slideIndex[no] += n, no);
  }
  
  function showSlides(n, no) {
	let i;
	let x = document.getElementsByClassName(slideId[no]);
	if (n > x.length) {slideIndex[no] = 1}    
	if (n < 1) {slideIndex[no] = x.length}
	for (i = 0; i < x.length; i++) {
	   x[i].style.display = "none";  
	}
	x[slideIndex[no]-1].style.display = "block";  
  }