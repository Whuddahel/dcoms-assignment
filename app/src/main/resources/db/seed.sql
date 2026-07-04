INSERT INTO Users (firstName, lastName, userRole, icPassportNo, email, password, createdAt)
VALUES ('Michael', 'Scott', 'admin', '010101100101', 'michael@gmail.com', '$2a$10$GNJCniXr0F2.U8P884oyWe8D4gKaOrxjLaK9XhwuIDKJxv0RQk3Zy', CURRENT_TIMESTAMP);

INSERT INTO ClinicAdministrator (userId)
SELECT userId FROM Users WHERE email = 'michael@gmail.com';