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
    .then(appointments => {
      tableBody.replaceChildren(); // empties innerHTML
      if (appointments.length > 0) {
        appointments.forEach(app => {
          // TODO TEST: can createPatientRow use app.patient or is manually constructed patient object necessary?
          tableBody.appendChild(createPatientRow(app.patient, app.id, app.doctor.id));
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
    e.target.disabled = true;
    selectedDate = (new Date()).toISOString().split("T")[0];
    document.querySelector("#date-picker").value = selectedDate;
    await loadAppointments();
    e.target.disabled = false;
  });

  document.querySelector("#date-picker").addEventListener("change", async e => {
    selectedDate = e.target.value;
    await loadAppointments();
  });

  document.querySelector("#searchBar").addEventListener("input", async e => {
    const searchbarValue = e.target.value.trim();
    patientName = searchbarValue !== "" ? searchbarValue : null;
    await loadAppointments();
  });
}
