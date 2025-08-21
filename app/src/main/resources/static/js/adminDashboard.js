// adminDashboard.js

import { openModal } from "./components/modals.js";
import { getDoctors, filterDoctors, saveDoctor } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";


document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();
  attachFilterListeners();
  document.getElementById("addDocBtn")?.addEventListener("click", () => openModal("addDoctor", adminAddDoctor));
});


function loadDoctorCards() {
  getDoctors()
    .then(doctors => {
      const contentDiv = document.getElementById("content");
      contentDiv.innerHTML = "";

      doctors.forEach(doctor => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
      });
    })
    .catch(error => {
      console.error("Failed to load doctors:", error);
    });
}

function filterDoctorsOnChange() {
  const searchBar = document.getElementById("searchBar").value.trim();
  const filterTime = document.getElementById("filterTime").value;
  const filterSpecialty = document.getElementById("filterSpecialty").value;


  const name = searchBar.length > 0 ? searchBar : null;
  const time = filterTime.length > 0 ? filterTime : null;
  const specialty = filterSpecialty.length > 0 ? filterSpecialty : null;

  filterDoctors(name, time, specialty)
    .then(response => {
      const doctors = response.doctors;
      const contentDiv = document.getElementById("content");
      contentDiv.innerHTML = "";

      if (doctors.length > 0) {
        console.log(doctors);
        doctors.forEach(doctor => {
          const card = createDoctorCard(doctor);
          contentDiv.appendChild(card);
        });
      } else {
        contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
        console.log("Nothing");
      }
    })
    .catch(error => {
      console.error("Failed to filter doctors:", error);
      alert("❌ An error occurred while filtering doctors.");
    });
}

function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "";

  doctors.forEach(doctor => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });

}

function collectDoctorData() {
  const checkboxes = document.querySelectorAll("input[name='availability']:checked");
  const times = Array.from(checkboxes).map(checkbox => checkbox.value);

  return doctor = {
    name: document.querySelector("#doctorName").value.trim(),
    specialization: document.querySelector("#specialization").value.trim(),
    email: document.querySelector("#doctorEmail").value.trim(),
    password: document.querySelector("#doctorPassword").value.trim(),
    phone: document.querySelector("#doctorPhone").value.trim(),
    availableTimes: times
  };
}

function attachFilterListeners() {
  document.getElementById("searchBar").value = "";
  document.getElementById("filterTime").value = "";
  document.getElementById("filterSpecialty").value = "";

  document.getElementById("searchBar")?.addEventListener("input", filterDoctorsOnChange);
  document.getElementById("filterTime")?.addEventListener("change", filterDoctorsOnChange);
  document.getElementById("filterSpecialty")?.addEventListener("change", filterDoctorsOnChange);
}

function adminAddDoctor() {
  const doctor = collectDoctorData();
  const token = localStorage.getItem("token");
  if (!token) {
    alert('❌ Unauthorized!');
    window.location.href("/");
  }

  saveDoctor(doctor, token)
    .then(response => {
      console.log(response.message);
      window.location.reload();
    })
    .catch(error => {
      console.error("Failed to save doctor:", error);
      alert(`Failed to save doctor: ${error}`);
    });
}
