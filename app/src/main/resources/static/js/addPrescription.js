// addPrescription.js

import { savePrescription, getPrescription } from "./services/prescriptionServices.js";


document.addEventListener('DOMContentLoaded', async () => {
    const savePrescriptionBtn = document.getElementById("savePrescription");
    const patientNameInput = document.getElementById("patientName");
    const medicinesInput = document.getElementById("medicines");
    const dosageInput = document.getElementById("dosage");
    const notesInput = document.getElementById("notes");
    const heading = document.getElementById("heading")


    const urlParams = new URLSearchParams(window.location.search);
    const appointmentId = urlParams.get("appointmentId");
    const mode = urlParams.get("mode");
    const token = localStorage.getItem("token");
    const patientName = urlParams.get("patientName")

    if (heading) {
        if (mode === "view") {
            heading.innerHTML = `View <span>Prescription</span>`;
        } else {
            heading.innerHTML = `Add <span>Prescription</span>`;
        }
    }


    // Pre-fill patient name
    if (patientNameInput && patientName) {
        patientNameInput.value = patientName;
    }

    // Fetch and pre-fill existing prescription if it exists
    if (appointmentId && token) {
        try {
            const response = await getPrescription(appointmentId, token);
            console.log("getPrescription :: ", response);

            // Now, check if the prescription exists in the response and access it from the array
            if (response.prescription && response.prescription.length > 0) {
                const existingPrescription = response.prescription[0]; // Access first prescription object
                patientNameInput.value = existingPrescription.patientName || YOU;
                // medicinesInput.value = existingPrescription.medication || "";
                // dosageInput.value = existingPrescription.dosage || "";
                // notesInput.value = existingPrescription.doctorNotes || "";
                medicinesInput.value = existingPrescription.medication.join(";\n") || YOU;
                dosageInput.value = existingPrescription.medication.map(med => `${med.name}: ${med.dosage}`).join(";\n") || "";
                notesInput.value = existingPrescription.medication.map(med => `${med.name}: ${med.doctorNotes}`).join(";\n") || "";
            }

        } catch (error) {
            console.warn("No existing prescription found or failed to load:", error);
        }
    }
    if (mode === 'view') {
        // Make fields read-only
        patientNameInput.disabled = true;
        medicinesInput.disabled = true;
        dosageInput.disabled = true;
        notesInput.disabled = true;
        savePrescriptionBtn.style.display = "none";  // Hide the save button
    }
    // Save prescription on button click
    savePrescriptionBtn.addEventListener('click', async (e) => {
        e.preventDefault();

        if(medicinesInput.value.length === 0 || dosageInput.value.length === 0){
            alert("❌ Enter prescription with correct format.");
            return;
        }
        let medication = [];
        const dosages = dosageInput.value.split(";");
        const doctorNotes = notesInput.value.split(";");

        let counter = 0;
        medicinesInput.value.split(";").forEach(medicineName => {
            medication.push(
                { 
                    name: medicineName.trim(),
                    dosage: dosages[counter].trim()
                })
            counter++;
        });
        counter = 0;

        doctorNotes.forEach(note =>{
            let found = medication.find(med => med.name === note.split(":")[0].trim());
            if(found){
                found.doctorNotes = note.split(":")[1].trim();
            } else {
                alert("❌ Medicine name across fields does not match");
                return;
            }
        });

        const prescription = {
            patientName: patientNameInput.value,
            medication,
            appointmentId
        };

        const { success, message } = await savePrescription(prescription, token);

        if (success) {
            alert("✅ Prescription saved successfully.");
            selectRole('doctor');
        } else {
            alert("❌ Failed to save prescription. " + message);
        }
    });
});
