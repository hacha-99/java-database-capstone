import { openModal } from "../components/modals.js";
import { API_BASE_URL } from "../config/config.js";
// import { triggerMessage, wrongCredentialsRoutine } from "../render.js";

const ADMIN_API = API_BASE_URL + "/admin";
const DOCTOR_API = API_BASE_URL + "/doctor";

window.onload = () => {
  attachMainButtonListeners();
};

// ==================================================================================================

// api call to login admin
window.adminLoginHandler = async function () {
  try {
    const email = document.querySelector("#email").value.trim();
    const password = document.querySelector("#password").value.trim();

    if (email === "") throw new Error("Enter a valid username.");
    if (password === "") throw new Error("Enter a valid password.");

    const admin = { username, password };

    const response = await fetch(`${ADMIN_API}/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(admin)
    });

    console.log("Status Code:", response.status);
    console.log("Response OK:", response.ok);
    if (response.ok) {
      const result = await response.json();
      console.log(result);
      selectRole('admin');
      localStorage.setItem('token', result.token);
    } else {
      alert('❌ Invalid credentials!');
    }
  }
  catch (error) {
    alert("❌ Failed to Login : ", error);
    console.log("Error :: adminLoginHandler :: ", error);
  }
}

// api call to login doctor
window.doctorLoginHandler = async function () {
  try {
    const email = document.querySelector("#email").value.trim();
    const password = document.querySelector("#password").value.trim();

    if (email === "") throw new Error("Enter a valid username.");
    if (password === "") throw new Error("Enter a valid password.");

    const doctor = { username, password };

    const response = await fetch(`${DOCTOR_API}/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(doctor)
    });

    console.log("Status Code:", response.status);
    console.log("Response OK:", response.ok);
    if (response.ok) {
      const result = await response.json();
      console.log(result);
      selectRole('doctor');
      localStorage.setItem('token', result.token);
    } else {
      alert('❌ Invalid credentials!');
    }
  }
  catch (error) {
    alert("❌ Failed to Login : ", error);
    console.log("Error :: doctorLoginHandler :: ", error);
  }
}

function attachMainButtonListeners() {
  document.getElementById("admin-btn")?.addEventListener("click", () => {
    openModal("adminLogin", adminLoginHandler);
  });

  document.getElementById("doctor-btn")?.addEventListener("click", () => {
    openModal("doctorLogin", doctorLoginHandler);
  });

  // patient-btn: see index.html, it has onclick="selectRole('patient')", which redirects to dashboard
}
