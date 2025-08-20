// modals.js

export function openModal(type, callback) {
    let modalContent = '';
    if (type === 'addDoctor') {
        modalContent = `
            <h2>Add Doctor</h2>
            <input type="text" id="doctorName" placeholder="Doctor Name" class="input-field">
            <select id="specialization" class="input-field select-dropdown">
                <option value="" disabled selected>Specialization</option>
                <option value="cardiologist">Cardiologist</option>
                <option value="dermatologist">Dermatologist</option>
                <option value="neurologist">Neurologist</option>
                <option value="pediatrician">Pediatrician</option>
                <option value="orthopedic">Orthopedic</option>
                <option value="gynecologist">Gynecologist</option>
                <option value="psychiatrist">Psychiatrist</option>
                <option value="dentist">Dentist</option>
                <option value="ophthalmologist">Ophthalmologist</option>
                <option value="ent">ENT Specialist</option>
                <option value="urologist">Urologist</option>
                <option value="oncologist">Oncologist</option>
                <option value="gastroenterologist">Gastroenterologist</option>
                <option value="general">General Physician</option>
            </select>
            <input type="email" id="doctorEmail" placeholder="Email" class="input-field">
            <input type="password" id="doctorPassword" placeholder="Password" class="input-field">
            <input type="text" id="doctorPhone" placeholder="Mobile No." class="input-field">
            <div class="availability-container">
                <label class="availabilityLabel">Select Availability:</label>
                    <div class="checkbox-group">
                        <label><input type="checkbox" name="availability" value="09:00-10:00"> 9:00 AM - 10:00 AM</label>
                        <label><input type="checkbox" name="availability" value="10:00-11:00"> 10:00 AM - 11:00 AM</label>
                        <label><input type="checkbox" name="availability" value="11:00-12:00"> 11:00 AM - 12:00 PM</label>
                        <label><input type="checkbox" name="availability" value="12:00-13:00"> 12:00 PM - 1:00 PM</label>
                    </div>
                </label>
            </div>
            <button class="dashboard-btn" id="saveDoctorBtn">Save</button>
        `;
    } else if (type === 'patientLogin') {
        modalContent = `
        <h2>Patient Login</h2>
        <input type="text" id="email" placeholder="Email" class="input-field">
        <input type="password" id="password" placeholder="Password" class="input-field">
        <button class="dashboard-btn" id="loginBtn">Login</button>
      `;
    }
    else if (type === "patientSignup") {
        modalContent = `
      <h2>Patient Signup</h2>
      <input type="text" id="name" placeholder="Name" class="input-field">
      <input type="email" id="email" placeholder="Email" class="input-field">
      <input type="password" id="password" placeholder="Password" class="input-field">
      <input type="text" id="phone" placeholder="Phone" class="input-field">
      <input type="text" id="address" placeholder="Address" class="input-field">
      <button class="dashboard-btn" id="signupBtn">Signup</button>
    `;

    } else if (type === 'adminLogin') {
        modalContent = `
        <h2>Admin Login</h2>
        <input type="text" id="username" name="username" placeholder="Username" class="input-field">
        <input type="password" id="password" name="password" placeholder="Password" class="input-field">
        <button class="dashboard-btn" id="adminLoginBtn" >Login</button>
      `;
    } else if (type === 'doctorLogin') {
        modalContent = `
        <h2>Doctor Login</h2>
        <input type="text" id="email" placeholder="Email" class="input-field">
        <input type="password" id="password" placeholder="Password" class="input-field">
        <button class="dashboard-btn" id="doctorLoginBtn" >Login</button>
      `;
    }

    document.getElementById('modal-body').innerHTML = modalContent;
    document.getElementById('modal').style.display = 'block';
    document.body.style.overflow = "hidden";

    document.getElementById('closeModal').onclick = () => {
        document.getElementById('modal').style.display = 'none';
        document.body.style.overflow = "initial";
    };

    document.querySelector('.modal').onclick = (e) => {
        if (e.currentTarget === e.target) {
            // this if excludes the modal content by checking whether the source of the event and the element the event is attached to are the same.
            // without it, the modal will close even when clicking on the modal body
            document.getElementById('modal').style.display = 'none';
            document.body.style.overflow = "initial";
        }
    };

    document.onkeydown = (e) => {
        if (e.key === "Escape") {
            document.getElementById('modal').style.display = 'none';
            document.body.style.overflow = "initial";
        }
    };

    if (type === "patientSignup") {
        document.getElementById("signupBtn").addEventListener("click", e => {
            e.currentTarget.disabled = true;
            callback();
            e.currentTarget.disabled = false;
        });
    }

    if (type === "patientLogin") {
        document.getElementById("loginBtn").addEventListener("click", e => {
            e.currentTarget.disabled = true;
            callback();
            e.currentTarget.disabled = false;
        });
    }

    if (type === 'addDoctor') {
        document.getElementById('saveDoctorBtn').addEventListener('click', e => {
            e.currentTarget.disabled = true;
            callback();
            e.currentTarget.disabled = false;
        });
    }

    if (type === 'adminLogin') {
        document.getElementById('adminLoginBtn').addEventListener('click', e => {
            e.currentTarget.disabled = true;
            callback();
            e.currentTarget.disabled = false;
        });
    }

    if (type === 'doctorLogin') {
        document.getElementById('doctorLoginBtn').addEventListener('click', e => {
            e.currentTarget.disabled = true;
            callback();
            e.currentTarget.disabled = false;
        });
    }

    document.addEventListener("keydown", e => {
        if (document.getElementById('modal').style.display == 'block' && e.key === "Enter") {
            e.currentTarget.disabled = true;
            callback();
            e.currentTarget.disabled = false;
        }
    });
}
