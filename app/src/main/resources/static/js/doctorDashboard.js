// doctorDashboard.js

import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";
// import { triggerMessage } from "./render.js";


document.addEventListener("DOMContentLoaded", () => {
  loadAppointments();
});

const tableBody = document.querySelector("#patientTableBody");
let selectedDate = (new Date()).toISOString().split("T")[0];
const token = localStorage.getItem("token");
let patientName = null;

attachFilterListeners();

function loadAppointments() {
  getAllAppointments(selectedDate, patient, token)
    .then(response => {
      tableBody.replaceChildren(); // empties innerHTML
      if (response.appointments.length > 0) {
        response.appointments.forEach(app => {
          const patient = {
            id: app.patientId,
            name: app.patientName,
            email: app.patientEmail,
            phone: app.patientPhone,
            address: app.patientAddress
          }
          tableBody.appendChild(createPatientRow(patient, app.id, app.doctor.id));
        });
      }
      else {
        tableBody.innerHTML = `<tr><td class="noPatientRecord" colspan='5'>No appointments found.</td></tr>`
      }
    })
    .catch(error => {
      tableBody.innerHTML = `<tr><td class="noPatientRecord" colspan='5'>Error loading appointments. Try again later.</td></tr>`
    });
}

function attachFilterListeners() {
  document.querySelector("#today-btn").addEventListener("click", async e => {
    e.currentTarget.disabled = true;
    selectedDate = (new Date()).toISOString().split("T")[0];
    document.querySelector("#date-picker").value = selectedDate;
    loadAppointments();
    e.currentTarget.disabled = false;
  });

  document.querySelector("#date-picker").addEventListener("change", async e => {
    selectedDate = e.target.value;
    loadAppointments();
  });

  document.querySelector("#searchBar").addEventListener("input", async e => {
    const searchbarValue = e.target.value.trim();
    patientName = searchbarValue !== "" ? searchbarValue : null;
    loadAppointments();
  });
}
