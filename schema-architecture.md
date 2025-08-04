This Spring Boot web application uses MVC and REST controllers for the presentation layer. Thymeleaf templates are returned for requests for Admin and Doctor dashboards, while REST APIs serve all other purposes and possible future additions like a mobile application. Static resources like the landing page and a Patient Dashboard are automatically provided by Spring Boot. The application works with two database types — MySQL (for patient, doctor, appointment, and admin data) and MongoDB (for prescriptions). All controllers route requests through a service layer, which in turn access the database using the repositories as an interface. MySQL uses JPA entities while MongoDB uses document models.

1. User (e.g. Admins, Doctors) accesses AdminDashboard or Appointment pages.
2. The action is automatically routed to the correct Thymeleaf or REST controller.
3. The controller calls the service layer for business logic and validations.
4. The service layer writes data to and requests data from the repositories, in this case the MySQL and the MongoDB repositories.
5. The repositories act as an interface where the actual database is accessed for write and read operations.
6. The data retrieved from the database is mapped onto Java classes (model binding).
7. Models bound are used in the response layer, e.g. rendered in html or serialized JSON and returned in the ResponseBody. 