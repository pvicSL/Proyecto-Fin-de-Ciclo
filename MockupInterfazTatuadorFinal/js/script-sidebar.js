/* =========================
-- SCRIPT PARA EL SIDEBAR --  
============================ */

// Obtengo los elementos del HTML
const hamburger = document.querySelector('#toggle-btn');

// Función que al darle click expande el sidebar
hamburger.addEventListener('click', function(){
    document.querySelector('#sidebar').classList.toggle('expand')
})