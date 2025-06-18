# User Story Template

**Title:**
_As a [user role], I want [feature/goal], so that [reason]._

**Acceptance Criteria:**
1. [Criteria 1]
2. [Criteria 2]
3. [Criteria 3]

**Priority:** [High/Medium/Low]
**Story Points:** [Estimated Effort in Points]
**Notes:**
- [Additional information or edge cases]


## Admin User Stories

**Title: Admin login**
_As an admin, I want to log into the portal with my username and password, so that I can manage the platform securely._

**Acceptance Criteria:**
1. Secure login using username and password
2. Show admin dashboard upon successful login

**Priority:** High
**Story Points:** 8
**Notes:**
- using JSON Web Token


**Title: Logout**
_As an admin, I want to be able to logout, so that system access is protected._

**Acceptance Criteria:**
1. Show logout button
2. After clicking, log user out
3. Upon logout, show login website

**Priority:** High
**Story Points:** 3


**Title: Add doctors**
_As an admin, I want add doctors to the portal, so that they can access and use the portal._

**Acceptance Criteria:**
1. Have a button to add a new doctor
2. Set information and credentials
3. Save new doctor to database

**Priority:** Medium
**Story Points:** 8


**Title: Delete doctor's profile**
_As an admin, I want to delete doctor's profiles, so that the list of employees can be maintained._

**Acceptance Criteria:**
1. List all doctors
2. Choose delete for one of the doctors
3. Doctor is removed from database

**Priority:** Low
**Story Points:** 5


**Title: Stored procedure for statistics**
_As an admin, I want to run a stored procedure in MySQL CLI , so that I can get the number of appointments per month and track usage statistics._

**Acceptance Criteria:**
1. Create procedure
2. Run procedure
3. Display formatted statistics

**Priority:** Low
**Story Points:** 3


## Patient User Stories

**Title: View list of doctors without logging in**
_As a patient, I want to view a list of doctors without logging in, so that I can explore options before registering._

**Acceptance Criteria:**
1. Have the backend return a list of doctors with the http request
2. Create thymeleaf template to render list of doctors

**Priority:** Low
**Story Points:** 5


**Title: Sign up**
_As a patient, I want to sign up using my email and a password, so that I can book appointments._

**Acceptance Criteria:**
1. Create page to sign up using thymeleaf form asking for account information
2. Submitting creates an account and persists data in database using appropriate API
3. Confirm account creation

**Priority:** High
**Story Points:** 8
**Notes:**
- Validation


**Title: Patient login**
_As a patient, I want to login to the portal, so that I can manage my bookings._

**Acceptance Criteria:**
1. Display login form
2. On valid credentials, redirect to dashboard
3. Display bookings

**Priority:** High
**Story Points:** 13


**Title: Logout**
_As a patient, I want to log out of the portal, so that my account is secured._

**Acceptance Criteria:**
1. Provide button to log out
2. Log out on click
3. Redirect to login or home page

**Priority:** High
**Story Points:** 3
**Notes:**
- Sticky button?
- Same as admin logout?


**Title: Book an appointment**
_As a patient, I want to book an hour-long appointment, so that I can consult with a doctor._

**Acceptance Criteria:**
1. Display button to display form to create new appointment
2. Don't allow booking of reserved slots.
3. Persist submitted appointment and display on patient dashboard

**Priority:** High
**Story Points:** 13


## Doctor User Stories

**Title: Doctor login**
_As a doctor, I want to login to the portal, so that I can manage my appointments._

**Acceptance Criteria:**
1. Display login form
2. On valid credentials, show dashboard
3. Display appointments

**Priority:** High
**Story Points:** 13


**Title: Logout**
_As a doctor, I want to log out, so that I can protect my data._

**Acceptance Criteria:**
1. Display logout button
2. On click, perform logout
3. Show login or home page

**Priority:** High
**Story Points:** 3


**Title: View appointment calendar**
_As a doctor, I want to view my appointment calendar, so that I can stay organized._

**Acceptance Criteria:**
1. Embed appointment calendar into dashboard or appointment overview
2. Show all appointments within a certain time

**Priority:** Low
**Story Points:** 5


**Title: Mark unavailability**
_As a doctor, I want to mark my unavailability, so that patients are informed only the available slots._

**Acceptance Criteria:**
1. Form to mark unavailability for a desired range
2. Persist unavailability

**Priority:** Medium
**Story Points:** 8
**Notes:**
- Unavailability has to be taken into account when patients book appointments.
- Absence can only be set for full days, no partials.


**Title: Update profile**
_As a doctor, I want to update my profile with specialization and contact information, so that patients have up-to-date information._

**Acceptance Criteria:**
1. Update profile button
2. Option to replace specialization and contact information
3. Option to persist data

**Priority:** Low
**Story Points:** 3
**Notes:**
- Button exists only for doctors?


**Title: View patient details**
_As a doctor, I want to view patient details for upcoming appointments, so that I can be prepared._

**Acceptance Criteria:**
1. On dashboard/appointment overview, make appointments clickable
2. On click, display patient details
3. [Criteria 3]

**Priority:** Low
**Story Points:** 5