function renderHeader() {
    if (window.location.pathname.endsWith("/")) {
        localStorage.removeItem("userRole");
        localStorage.removeItem("token");
    }

    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");

    // non empty string means true
    if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
        localStorage.removeItem("userRole");
        alert("Session expired or invalid login. Please log in again.");
        window.location.href = "/";
        return;
    }

    let headerContent = "";
    if (role === "admin") {
        headerContent += `
            <button id="addDocBtn" class="headerBtn" onclick="openModal('addDoctor')">Add Doctor</button>
            <a href="#" onclick="logout()">Logout</a>`;
    }
    if (role === "doctor") {
        headerContent += `
            <a href="#" onclick="home()">Home</a>
            <a href="#" onclick="logout()">Logout</a>`
    }
    if (role === "patient") {
        headerContent += `
        <button id="loginBtn" class="headerBtn" onclick="openModal('login')">Login</button>
        <button id="signUpBtn" class="headerBtn" onclick="openModal('signUp')">Sign Up</button>`;
    }
    if (role === "loggedPatient") {
        headerContent += `
            <a href="/patient/dashboard">Home</a>
            <a href="/patient/dashboard">Appointments</a>
            <a href="#" onclick="logoutPatient()">Logout</a>`
    }

    const headerDiv = document.getElementById("header");

    headerDiv.innerHTML = headerContent;
}

function logout() {
    localStorage.removeItem("userRole");
    localStorage.removeItem("token");
    window.location.href = "/";
}

function logoutPatient() {
    localStorage.removeItem("token");
    localStorage.setItem("userRole", "patient");
    localStorage.setItem("openLogin", "1");
    window.location.href = "/";
}