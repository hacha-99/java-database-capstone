## MySQL Database Design

### Table: appointments
- id: INT, Primary Key, Auto Increment
- doctor_id: INT, Foreign Key → doctors(id)
- patient_id: INT, Foreign Key → patients(id)
- appointment_time: DATETIME, Not Null
- status: INT (0 = Scheduled, 1 = Completed, 2 = Cancelled)

Notes:
- there should be no duplicate combination of doctor_id and appointment_time, likewise for patient_id
- since appointments can be flagged as cancelled, this constraint should be handled when issuing appointments rather than on data layer using DDL or similar
- appointments old enough should be deleted (e.g., after a few years)

### Table: patients
- id: INT, Primary Key, Auto Increment
- first_name: VARCHAR(30)
- last_name: VARCHAR(30), Not Null
- date_of_birth: DATE, Not Null
- biological_gender: INT (0 = Male, 1 = Female), Not Null
- insurer: VARCHAR(20)
- email: VARCHAR(100)
- password_hash: VARCHAR(100)
- phone_number: VARCHAR(15)
- address: VARCHAR(50), Not Null
- active: BOOLEAN, Default TRUE

Notes:
- email validation via in application, e.g., using annotations and regex
- likewise for phone_number
- no cascade deletion
- only soft-deletion using 'active' column
- hash password using bcrypt → maximum hash length < 100

### Table: doctors
- id: INT, Primary Key, Auto Increment
- first_name: VARCHAR(30)
- last_name: VARCHAR(30), Not Null
- date_of_birth: DATE, Not Null
- gender: INT (0 = Male, 1 = Female, 2 = Other), Not Null
- specialization: VARCHAR(50)
- email: VARCHAR(100)
- password_hash: VARCHAR(100)
- phone_number: VARCHAR(15)
- address: VARCHAR(50), Not Null
- active: BOOLEAN, Default TRUE

Notes:
- email validation via in application, e.g., using annotations and regex
- likewise for phone_number
- no cascade deletion
- only soft-deletion using 'active' column
- hash password using bcrypt → maximum hash length < 100

### Table: admins
- id: INT, Primary Key, Auto Increment
- username: VARCHAR(30)
- password_hash: VARCHAR(100), Not Null
- email: VARCHAR(100), Not Null