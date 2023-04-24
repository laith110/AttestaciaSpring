window.onload = showSlides;
var slideIndex = 1;
showSlides(slideIndex);

// Вперед/назад элементы управления
function plusSlides(n) {
    showSlides(slideIndex += n);
}

// Элементы управления миниатюрами изображений
function currentSlide(n) {
    showSlides(slideIndex = n);
}

function showSlides(n) {
    var i;
    var slides = document.getElementsByClassName("mySlides ");
    var dots = document.getElementsByClassName("dot");
    if (n > slides.length) {
        slideIndex = 1
    }
    if (n < 1) {
        slideIndex = slides.length
    }
    for (i = 0; i < slides.length; i++) {
        slides[i].style.display = "none";
    }
    for (i = 0; i < dots.length; i++) {
        dots[i].className = dots[i].className.replace(" active", "");
    }
    slides[slideIndex - 1].style.display = "block";
    dots[slideIndex - 1].className += " active";
}

var slideIndex = 0;
showSlides();


function d2() {
    return alert("Зарегестрируйтесь или войдите в аккаунт");

}

function viewDiv() {
    if (document.getElementById("SFP").style.display == "block") {
        document.getElementById("SFP").style.display = "none";
        document.getElementById("prodField").style.display = "none";
    } else {
        document.getElementById("SFP").style.display = "block";
        document.getElementById("prodField").style.display = "block";
    }
}
;

function viewDiv1() {

    document.getElementById("SFP").style.display = "none";
    document.getElementById("prodField").style.display = "none";

};

function viewDiv2() {
    if (document.getElementById("SFP1").style.display == "block") {
        document.getElementById("SFP1").style.display = "none";
        document.getElementById("formAuth").style.display = "none";

    } else {
        document.getElementById("SFP1").style.display = "block";
        document.getElementById("formAuth").style.display = "block";
    }
}
;



