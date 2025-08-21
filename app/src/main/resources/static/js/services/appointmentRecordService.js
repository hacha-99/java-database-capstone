// appointmentRecordService.js

import { API_BASE_URL } from "../config/config.js";


const APPOINTMENT_API = `${API_BASE_URL}/appointments`;

//This is for the doctor to get all the patient Appointments
export async function getAllAppointments(date, patientName, token) {

    const params = new URLSearchParams();
    if (patientName) params.append("patientName", patientName);
    if (date) params.append("date", date);
    if (token) params.append("token", token);

    const response = await fetch(`${APPOINTMENT_API}?${params.toString()}`);
    if (!response.ok) {
        throw new Error("Failed to fetch appointments");
    }

    return await response.json();
}

export async function bookAppointment(appointment, token) {
    try {
        const response = await fetch(`${APPOINTMENT_API}/${token}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(appointment)
        });

        const data = await response.json();
        return {
            success: response.ok,
            message: data.message || "Something went wrong"
        };
    } catch (error) {
        console.error("Error while booking appointment:", error);
        return {
            success: false,
            message: "Network error. Please try again later."
        };
    }
}

export async function updateAppointment(appointment, token) {
    try {
        const response = await fetch(`${APPOINTMENT_API}/${token}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(appointment)
        });

        const data = await response.json();
        return {
            success: response.ok,
            message: data.message || "Something went wrong"
        };
    } catch (error) {
        console.error("Error while updating appointment:", error);
        return {
            success: false,
            message: "Network error. Please try again later."
        };
    }
}

export async function cancelAppointment(appointmentId, token) {
    try {
        const response = await fetch(`${APPOINTMENT_API}/${appointmentId}/${token}`, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json"
            },
        });

        const data = await response.json();
        return {
            success: response.ok,
            message: data.message || "Something went wrong"
        };
    } catch (error) {
        console.error("Error while canceling appointment:", error);
        return {
            success: false,
            message: "Network error. Please try again later."
        };
    }
}
