// loggedPatient.js

import { getDoctors } from './services/doctorServices.js';
import { createDoctorCard } from './components/doctorCard.js';
import { filterDoctors, getDoctorAvailability } from './services/doctorServices.js';
import { bookAppointment } from './services/appointmentRecordService.js';


document.addEventListener("DOMContentLoaded", () => {
    loadDoctorCards();
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

export function showBookingOverlay(e, doctor, patient) {
    document.body.style.overflow = "hidden";

    const button = e.target;
    const rect = button.getBoundingClientRect();
    console.log(patient.name);
    console.log(patient);
    const ripple = document.createElement("div");
    ripple.classList.add("ripple-overlay");
    ripple.style.left = `${e.clientX}px`;
    ripple.style.top = `${e.clientY}px`;
    document.body.appendChild(ripple);

    setTimeout(() => ripple.classList.add("active"), 50);


    const modalApp = document.createElement("div");
    modalApp.classList.add("modalApp");

    modalApp.innerHTML = `
    <h2>Book Appointment</h2>
    <input class="input-field" type="text" value="${patient.name}" disabled />
    <input class="input-field" type="text" value="${doctor.name}" disabled />
    <input class="input-field" type="text" value="${doctor.specialty}" disabled/>
    <input class="input-field" type="email" value="${doctor.email}" disabled/>
    <input class="input-field" type="date" id="appointment-date" />
    <select class="input-field" id="appointment-time">
      <option value="" disabled selected>Select a date, then a time</option>
    </select>
    <button class="confirm-booking">Confirm Booking</button>
  `;

    document.body.appendChild(modalApp);

    const today = new Date().toISOString().split("T")[0]; // yyyy-MM-dd
    document.getElementById("appointment-date").min = today;

    ripple.addEventListener("click", e => {
        ripple.remove();
        modalApp.remove();
        document.body.style.overflow = "initial";
    });

    document.addEventListener("keydown", (e) => {
        if (e.key === "Escape") {
            ripple.remove();
            modalApp.remove();
            document.body.style.overflow = "initial";
        }
    });

    document.querySelector("#appointment-date").addEventListener("change", async (e) => {
        if (document.querySelector("#appointment-date").value !== "") {
            const availability = await getDoctorAvailability("patient", doctor.id, document.querySelector("#appointment-date").value, localStorage.getItem("token"));
            document.querySelector("#appointment-time").innerHTML = `
                <option value="" disabled selected>Select a date, then a time</option>
                ${availability.map(time => `<option value="${time}">${time}</option>`).join('')}`;
        } else {
            document.querySelector("#appointment-time").innerHTML = `
                <option value="" disabled selected>Select a date, then a time</option>
                ${doctor.availableTimes.map(time => `<option value="${time}">${time}</option>`).join('')}`;
        }
    });

    setTimeout(() => modalApp.classList.add("active"), 600);

    modalApp.querySelector(".confirm-booking").addEventListener("click", async (e) => {
        modalApp.querySelector(".confirm-booking").disabled = true;
        const date = modalApp.querySelector("#appointment-date").value;
        const time = modalApp.querySelector("#appointment-time").value;
        const token = localStorage.getItem("token");
        const startTime = time.split('-')[0];
        const appointment = {
            doctor: { id: doctor.id },
            patient: { id: patient.id },
            appointmentTime: `${date}T${startTime}:00`,
            status: 0
        };


        const { success, message } = await bookAppointment(appointment, token);

        if (success) {
            alert("Appointment booked successfully");
            ripple.remove();
            modalApp.remove();
            document.body.style.overflow = "initial";
        } else {
            alert("❌ Failed to book an appointment :: " + message);
        }
        modalApp.querySelector(".confirm-booking").disabled = false;
    });
}



// Filter Input
document.getElementById("searchBar").value = "";
document.getElementById("filterTime").value = "";
document.getElementById("filterSpecialty").value = "";

document.getElementById("searchBar").addEventListener("input", filterDoctorsOnChange);
document.getElementById("filterTime").addEventListener("change", filterDoctorsOnChange);
document.getElementById("filterSpecialty").addEventListener("change", filterDoctorsOnChange);



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

export function renderDoctorCards(doctors) {
    const contentDiv = document.getElementById("content");
    contentDiv.innerHTML = "";

    doctors.forEach(doctor => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
    });

}
