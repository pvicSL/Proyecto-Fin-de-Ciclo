
/* =====================================================
-- SCRIPT PARA CONTROLAR VISIBILIDAD DE LA CONTRASEÑA --  
===================================================== */

    // Obtengo los elementos del HTML
    const passwordInput = document.getElementById('passwordInput');
    const toggleButton = document.getElementById('visualizarPassword');
    const toggleIcon = document.getElementById('toggleIcon'); // El <i> dentro del botón

    //Función que cambia la visibilidad
    toggleButton.addEventListener('click', function() {
        // Verifica el tipo actual del input
        const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
        
        // Cambia el tipo del input
        passwordInput.setAttribute('type', type);
        
        // Cambia el icono
        if (type === 'text') {
            // Si es visible (TEXT), muestra el icono de ojo normal (bi-eye)
            toggleIcon.classList.remove('bi-eye-slash');
            toggleIcon.classList.add('bi-eye');
        } else {
            // Si está oculto (PASSWORD), muestra el icono de ojo tachado (bi-eye-slash)
            toggleIcon.classList.remove('bi-eye');
            toggleIcon.classList.add('bi-eye-slash');
        }
    });


