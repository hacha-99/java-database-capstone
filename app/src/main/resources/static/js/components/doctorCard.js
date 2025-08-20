// doctorCard.js

import { openModal } from "./modals.js";
// import { triggerMessage } from "../render.js";
import { deleteDoctor } from "../services/doctorServices.js";
import { showBookingOverlay } from "../loggedPatient.js";
import { getPatientData } from "../services/patientServices.js";


export function createDoctorCard(doctor) {
  const role = localStorage.getItem("userRole");
  /* generate to be injected html structure */
  const card = document.createElement("div");
  card.classList.add("doctor-card");

  const infoDiv = document.createElement("div");
  infoDiv.classList.add("doctor-info");

  const name = document.createElement("h3");
  name.textContent = doctor.name;

  const specialization = document.createElement("p");
  specialization.textContent = doctor.specialization;

  const email = document.createElement("p");
  email.textContent = doctor.email;

  const availability = document.createElement("p");
  availability.textContent = `Consulting hours: ${doctor.availableTimes.join(", ")}`;
  // puts array elements into string with ", " as a separator

  infoDiv.replaceChildren(name, specialization, email, availability);

  const actionsDiv = document.createElement("div");
  actionsDiv.classList.add("card-actions");

  // generate buttons for card depending on role

  if (role === "admin") {
    const removeBtn = document.createElement("button");
    removeBtn.classList.add("cardButton");
    removeBtn.textContent = "Delete";
    removeBtn.addEventListener("click", async (e) => {
        removeBtn.disabled = true;
        const token = localStorage.getItem("token");
        const response = await deleteDoctor(doctor.id, token);
        if (response.success) {
            // triggerMessage(`Successfully deleted Dr. ${doctor.name}.`, "green");
            card.style.opacity = "0";
            setTimeout(() => card.remove(), 300);
        }
        removeBtn.disabled = false;
    });
    actionsDiv.appendChild(removeBtn);

  } else if (role === "patient") {
    const bookNow = document.createElement("button");
    bookNow.classList.add("cardButton");
    bookNow.textContent = "Book Now";
    bookNow.addEventListener("click", () => {
      alert("❌ You need to login first.");
      openModal("patientLogin", window.loginPatient);
    });
    actionsDiv.appendChild(bookNow);

  } else if (role === "loggedPatient") {
    const bookNow = document.createElement("button");
    bookNow.classList.add("cardButton");
    bookNow.textContent = "Book Now";
    bookNow.addEventListener("click", async (event) => {
      const token = localStorage.getItem("token");
      if(!token){
        // redirect
        window.location.href = "/";
      }
      const patientData = await getPatientData(token);
      showBookingOverlay(event, doctor, patientData);
    });
    actionsDiv.appendChild(bookNow);
  }

  card.appendChild(infoDiv);
  card.appendChild(actionsDiv);

  return card;
}
