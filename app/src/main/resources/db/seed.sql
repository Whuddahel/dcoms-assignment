INSERT INTO User (firstName, lastName, userRole, icPassportNo, email, password)
VALUES ('Michael', 'Scott', 'admin', '010101100101', 'michael@gmail.com', '$2a$10$GNJCniXr0F2.U8P884oyWe8D4gKaOrxjLaK9XhwuIDKJxv0RQk3Zy');

INSERT INTO ClinicAdministrator (userId)
SELECT userId FROM User WHERE email = 'michael@gmail.com';