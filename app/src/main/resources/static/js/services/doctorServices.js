// doctorServices.js

import { API_BASE_URL } from "../config/config.js";
// import { triggerMessage } from "../render.js";


const DOCTOR_API = API_BASE_URL + "/doctor"

export async function getDoctorAvailability(user, doctorId, date, token) {
    try {
        const response = await fetch(`${DOCTOR_API}/availability/${user}/${doctorId}/${date}/${token}`, {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            }
        });
        if (response.ok) {
            return (await response.json()).availability;
        }
        else {
            console.error("Failed to fetch detailed information about doctors available times:", response.statusText);
            return [];
        }
    } catch (error) {
        console.error("Error :: getDoctorAvailability :: ", error);
        alert("Something went wrong!");
        // triggerMessage("Error retrieving doctor availability", "red");
        return [];
    }
}

// api call to get list of doctors
export async function getDoctors() {
    try {
        const response = await fetch(`${DOCTOR_API}`);

        if (response.ok) {
            return (await response.json()).doctors;
        }
        else {
            console.error("Failed to fetch doctors:", response.statusText);
            return [];
        }
    } catch (error) {
        console.error("Error :: getDoctors :: ", error);
        alert("Something went wrong!");
        // triggerMessage("Error retrieving doctor information", "red");
        return [];
    }
}

// api call to delete doctor
export async function deleteDoctor(id, token) {
    try {
        const response = await fetch(`${DOCTOR_API}/${id}/${token}`, {
            method: "DELETE",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            }
        });

        const result = await response.json();
        if (!response.ok) {
            throw new Error(result.message);
        }
        return { success: response.ok, message: result.message };
    }
    catch (error) {
        console.log(`Error :: deleteDoctor :: ${error}`);
        return { success: false, message: error.message };
    }
}

// api call to save doctor
export async function saveDoctor(doctor, token) {
    try {
        const response = await fetch(`${DOCTOR_API}/${token}`, {
            method: "POST",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(doctor)
        });

        const result = await response.json();
        if (!response.ok) {
            throw new Error(result.message);
        }
        return { success: response.ok, message: result.message };
    }
    catch (error) {
        console.log(`Error :: saveDoctor :: ${error}`);
        return { success: false, message: error.message };
    }
}

// api call to update doctor
export async function updateDoctor(doctor, token) {
    try {
        const response = await fetch(`${DOCTOR_API}/${token}`, {
            method: "PUT",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(doctor)
        });

        const result = await response.json();
        if (!response.ok) {
            throw new Error(result.message);
        }
        return { success: response.ok, message: result.message };
    }
    catch (error) {
        console.log(`Error :: updateDoctor :: ${error}`);
        return { success: false, message: error.message };
    }
}

export async function filterDoctors(name, time, specialty) {
    try {
        const params = new URLSearchParams();
        if (name) params.append("name", name);
        if (time) params.append("time", time);
        if (specialty) params.append("specialty", specialty);

        const response = await fetch(`${DOCTOR_API}/filter?${params.toString()}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        });

        if (response.ok) {
            return (await response.json()).doctors;
        }
        else {
            console.error("Failed to fetch filtered doctors:", response.statusText);
            return [];
        }
    } catch (error) {
        console.error("Error :: filterDoctors :: ", error);
        alert("Something went wrong!");
        // triggerMessage("Error retrieving doctor information", "red");
        return [];
    }
}
