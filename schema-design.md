## MySQL Database Design

### Table: appointments
- id: INT, Primary Key, Auto Increment
- doctor_id: INT, Foreign Key → doctors(id)
- patient_id: INT, Foreign Key → patients(id)
- appointment_time: DATETIME, Not Null
- status: VARCHAR(10) (Scheduled, Completed, Cancelled)

Notes:
- there should be no duplicate combination of doctor_id and appointment_time, likewise for patient_id
- since appointments can be flagged as cancelled, this constraint should be handled when issuing appointments rather than on data layer using DDL or similar
- appointments old enough should be deleted (e.g., after a few years)

### Table: patients
- id: INT, Primary Key, Auto Increment
- full_name: VARCHAR(100), Not Null
- date_of_birth: DATE, Not Null
- biological_gender: VARCHAR(10) (Male, Female), Not Null
- insurer: VARCHAR(50), Not Null
- email: VARCHAR(100), Not Null
- password_hash: VARCHAR(100), Not Null
- phone_number: VARCHAR(10), Not Null
- address: VARCHAR(255), Not Null
- active: BOOLEAN, Default TRUE

Notes:
- email validation via in application, e.g., using annotations and regex
- likewise for phone_number
- no cascade deletion
- only soft-deletion using 'active' column
- hash password using bcrypt → maximum hash length < 100

### Table: doctors
- id: INT, Primary Key, Auto Increment
- full_name: VARCHAR(100), Not Null
- date_of_birth: DATE
- gender: VARCHAR(10) (Male, Female, Other)
- specialization: VARCHAR(50), Not Null
- email: VARCHAR(100), Not Null
- password_hash: VARCHAR(100), Not Null
- phone_number: VARCHAR(10), Not Null
- address: VARCHAR(255), Not Null
- active: BOOLEAN, Default TRUE

Notes:
- email validation via in application, e.g., using annotations and regex
- likewise for phone_number
- no cascade deletion
- only soft-deletion using 'active' column
- hash password using bcrypt → maximum hash length < 100

### Table: admins
- id: INT, Primary Key, Auto Increment
- username: VARCHAR(30), Not Null
- password_hash: VARCHAR(100), Not Null
- email: VARCHAR(100)
---

## MongoDB Collection Design

### Collection: prescriptions

```json
{
    "_id": "ObjectId('64abc123456')",
    "patientName": "John Smith",
    "appointmentId": 51,
    "medication": [
        {
            "name": "Paracetamol",
            "dosage": "500mg",
            "doctorNotes": "Take 1 tablet every 6 hours.",
            "refillCount": 2
        }, 
        {
            "name": "Nasal Spray",
            "dosage": "0.1%",
            "doctorNotes": "Up to three sprays daily per nostril",
            "refillCount": 2
        }
    ]
}
```

Notes:
- timestamp extractable from ObjectId
- one prescription for one appointment → array of medications
- doctorNotes should contain frequency and amount of tables to take in