INSERT INTO Users (firstName, lastName, userRole, icPassportNo, email, password)
VALUES ('Michael', 'Scott', 'admin', '010101100101', 'michael@gmail.com', '$2a$10$u7pm5/donhya7Ajf6gk1euSlfNfXKE3Kg6gT4g3RcZBxQOVrTAoVa');

INSERT INTO ClinicAdministrator (userId)
SELECT userId FROM Users WHERE email = 'michael@gmail.com';

INSERT INTO Users (firstName, lastName, userRole, icPassportNo, email, password)
VALUES ('doctor', 'smith', 'doctor', '22222222', 'doctor@gmail.com', '$2a$10$u7pm5/donhya7Ajf6gk1euSlfNfXKE3Kg6gT4g3RcZBxQOVrTAoVa');

INSERT INTO Doctor (userId, Specialization)
SELECT userId, 'General Practitioner' FROM Users WHERE email = 'doctor@gmail.com';

INSERT INTO Users (firstName, lastName, userRole, icPassportNo, email, password)
VALUES ('receptionist', 'smith', 'receptionist', '33333333', 'receptionist@gmail.com', '$2a$10$u7pm5/donhya7Ajf6gk1euSlfNfXKE3Kg6gT4g3RcZBxQOVrTAoVa');

INSERT INTO Receptionist (userId)
SELECT userId FROM Users WHERE email = 'receptionist@gmail.com';

INSERT INTO Users (firstName, lastName, userRole, icPassportNo, email, password)
VALUES ('patient', 'smith', 'patient', '44444444', 'patient@gmail.com', '$2a$10$u7pm5/donhya7Ajf6gk1euSlfNfXKE3Kg6gT4g3RcZBxQOVrTAoVa');

INSERT INTO Patient (userId, medicalRecordId, contactNumber)
SELECT userId, NEXT VALUE FOR medical_record_seq, '1234567890' FROM Users WHERE email = 'patient@gmail.com';
