import { API_BASE_URL } from "../config/config.js";
// import { triggerMessage } from "../render.js";

const DOCTOR_API = API_BASE_URL + "/doctor"

// simple api call to get list of doctors
export async function getDoctors() {
  try {
    const response = await fetch(`${DOCTOR_API}/list`);

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

// simple api call to get list of doctors
export async function deleteDoctor(id, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/delete/${id}`, {
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

// api call to get list of doctors
export async function saveDoctor(doctor, token) {// simple api call to get list of doctors
  try {
    const response = await fetch(`${DOCTOR_API}/save`, {
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

export async function filterDoctors(name, time, specialty) {
  try {
    const response = await fetch(`${DOCTOR_API}/list/filter/${name}/${time}/${specialty}`, {
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
