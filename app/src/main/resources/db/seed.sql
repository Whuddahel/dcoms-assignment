INSERT INTO Users (firstName, lastName, userRole, icPassportNo, email, password)
VALUES ('Michael', 'Scott', 'admin', '010101100101', 'michael@gmail.com', '$2a$10$FfXryxc7O.rzccqjn4LrK.ccX9vo82EK8RyxhFPh2MNMlfLU2JmR.');

INSERT INTO ClinicAdministrator (userId)
SELECT userId FROM Users WHERE email = 'michael@gmail.com';