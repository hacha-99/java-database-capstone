// header.js

function renderHeader() {
  const headerDiv = document.getElementById("header");

  if (window.location.pathname.endsWith("/") || window.location.pathname.endsWith("/index.html")) {
    localStorage.removeItem("userRole");
    headerDiv.innerHTML = `
      <header class="header">
        <div class="logo-section">
          <img src="../assets/images/logo/logo.png" alt="Hospital CMS Logo" class="logo-img">
          <span class="logo-title">Hospital CMS</span>
        </div>
      </header>`;
    return;
  }

  const role = localStorage.getItem("userRole");
  const token = localStorage.getItem("token");

  let headerContent = `
          <header class="header">
            <div class="logo-section" onclick="window.location.href = '/'">
              <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
              <span class="logo-title">Hospital CMS</span>
            </div>
            <nav>`;

  if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
    localStorage.removeItem("userRole");
    alert("Session expired or invalid login. Please log in again.");
    window.location.href = "/";
    return;
  } else if (role === "admin") {
    headerContent += `
           <button id="addDocBtn" class="headerBtn">Add Doctor</button>
           <button class="headerBtn" onclick="logout()">Logout</button>`;
  } else if (role === "doctor") {
    headerContent += `
           <button class="headerBtn" onclick="selectRole('doctor')">Home</button>
           <button class="headerBtn" onclick="logout()">Logout</button>`;
  } else if (role === "patient") {
    headerContent += `
           <button id="patientLogin" class="headerBtn">Login</button>
           <button id="patientSignup" class="headerBtn">Sign Up</button>`;
  } else if (role === "loggedPatient") {
    headerContent += `
           <button id="home" class="headerBtn" onclick="window.location.href='/pages/loggedPatientDashboard.html'">Home</button>
           <button id="patientAppointments" class="headerBtn" onclick="window.location.href='/pages/patientAppointments.html'">Appointments</button>
           <button class="headerBtn" onclick="logoutPatient()">Logout</button>`;
  }

  headerContent += `</nav></header>`;
  headerDiv.innerHTML = headerContent;

  attachHeaderButtonListeners();
}

function logout() {
  localStorage.removeItem("userRole");
  localStorage.removeItem("token");
  window.location.href = "/";
}

function logoutPatient() {
  localStorage.removeItem("token");
  localStorage.setItem("userRole", "patient");
  localStorage.setItem("openLogin", true); // used to reopen patient login modal after homepage is loaded
  window.location.href = "/pages/patientDashboard.html";
}

function attachHeaderButtonListeners() {
  document.querySelector("#logoutBtn")?.addEventListener("click", e => {
    logout();
  });

  document.querySelector("#logoutPatientBtn")?.addEventListener("click", e => {
    logoutPatient();
  });

  // document.querySelector("#patientLogin")?.addEventListener("click", e => {
  //   openModal("patientLogin");
  // });

  // document.querySelector("#patientSignup")?.addEventListener("click", e => {
  //   openModal("patientSignup");
  // });

  // document.getElementById("addDocBtn")?.addEventListener("click", e => {
  //   openModal("addDoctor");
  // });
  // these are already added in the patientDashboard.js and adminDashboard.js
}

renderHeader();
