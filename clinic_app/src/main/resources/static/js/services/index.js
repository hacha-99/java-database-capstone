document.addEventListener("DOMContentLoaded", () => {
    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");
    const openLogin = localStorage.getItem("openLogin");

    if (role === "patient" && !token && openLogin === "1") {
        openModal("login");
        localStorage.removeItem("openLogin"); // open just once
    }
});