// is this the right place for the modal opening and closing functions and the logout functions?

const modal = document.getElementById("modal");
const backdrop = document.getElementById("backdrop");
const closeModal = document.getElementById("closeModal");

function openModal(type = "empty") { // default val
    modal.classList.toggle("active");
    modal.style.opacity = '1';
    modal.style.visibility = 'visible';
    modal.style.pointerEvents = 'auto';

    backdrop.classList.toggle("active");
    backdrop.style.opacity = '1';

    setTimeout(() => {
        modal.classList.toggle("active");

    }, 500);
}

// cant be same name as variable, breaks stuff...
function closeModalfunc() {
    backdrop.style.opacity = '0';
    setTimeout(() => {
        backdrop.classList.toggle("active");
    }, 500);

    modal.classList.toggle("hide");

    setTimeout(() => {
        modal.classList.toggle("hide");
        modal.style.opacity = '0';
        modal.style.visibility = 'hidden';
        modal.style.pointerEvents = 'none';
    }, 500);
}

closeModal.addEventListener("click", e => {
    closeModalfunc();
})

backdrop.addEventListener("click", e => {
    closeModalfunc();
})

document.addEventListener("keydown", e => {
    closeModalfunc();
})