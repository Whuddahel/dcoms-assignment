-- User
CREATE TABLE User (
    userId       INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    firstName    VARCHAR(100) NOT NULL,
    lastName     VARCHAR(100) NOT NULL,
    userRole     VARCHAR(20) NOT NULL CHECK (userRole IN ('admin', 'doctor', 'receptionist', 'patient')),
    icPassportNo VARCHAR(50)  NOT NULL,
    email        VARCHAR(150) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL
);

CREATE INDEX idx_user_email    ON User(email);
CREATE INDEX idx_user_userRole ON User(userRole);


-- ClinicAdministrator
CREATE TABLE ClinicAdministrator (
    adminId INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    userId  INT NOT NULL UNIQUE,
    FOREIGN KEY (userId) REFERENCES User(userId)
);

CREATE INDEX idx_clinicadmin_userId ON ClinicAdministrator(userId);


-- Receptionist
CREATE TABLE Receptionist (
    receptionistId INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    userId         INT NOT NULL UNIQUE,
    FOREIGN KEY (userId) REFERENCES User(userId)
);

CREATE INDEX idx_receptionist_userId ON Receptionist(userId);


-- Doctor
CREATE TABLE Doctor (
    doctorId       INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    userId         INT NOT NULL UNIQUE,
    Specialization VARCHAR(100) NOT NULL,
    FOREIGN KEY (userId) REFERENCES User(userId)
);

CREATE INDEX idx_doctor_userId         ON Doctor(userId);
CREATE INDEX idx_doctor_Specialization ON Doctor(Specialization);


CREATE SEQUENCE medical_record_seq START WITH 1;

-- Patient
CREATE TABLE Patient (
    patientId       INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    userId          INT NOT NULL UNIQUE,
    medicalRecordId INT NOT NULL UNIQUE,
    contactNumber   VARCHAR(20)  NOT NULL,
    FOREIGN KEY (userId) REFERENCES User(userId)
);

CREATE INDEX idx_patient_userId          ON Patient(userId);
CREATE INDEX idx_patient_medicalRecordId ON Patient(medicalRecordId);


-- Schedule
CREATE TABLE Schedule (
    scheduleId INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    doctorId   INT         NOT NULL,
    day        VARCHAR(20) NOT NULL,
    startTime  TIME        NOT NULL,
    endTime    TIME        NOT NULL,
    FOREIGN KEY (doctorId) REFERENCES Doctor(doctorId)
);

CREATE INDEX idx_schedule_doctorId ON Schedule(doctorId);
CREATE INDEX idx_schedule_day      ON Schedule(day);


-- Appointment
CREATE TABLE Appointment (
    appointmentId   INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    doctorId        INT      NOT NULL,
    patientId       INT      NOT NULL,
    scheduleId      INT      NOT NULL,
    createdAt       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cancelledByUserId     INT,
    FOREIGN KEY (doctorId)        REFERENCES Doctor(doctorId),
    FOREIGN KEY (patientId)       REFERENCES Patient(patientId),
    FOREIGN KEY (scheduleId)      REFERENCES Schedule(scheduleId)
);

CREATE INDEX idx_appointment_doctorId        ON Appointment(doctorId);
CREATE INDEX idx_appointment_patientId       ON Appointment(patientId);
CREATE INDEX idx_appointment_scheduleId      ON Appointment(scheduleId);
CREATE INDEX idx_appointment_createdAt       ON Appointment(createdAt);


-- Consultation
CREATE TABLE Consultation (
    consultationId INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    appointmentId  INT           NOT NULL UNIQUE,
    content        CLOB          NOT NULL,
    fee            DECIMAL(10,2) NOT NULL,
    createdAt      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (appointmentId) REFERENCES Appointment(appointmentId)
);

CREATE INDEX idx_consultation_appointmentId ON Consultation(appointmentId);
CREATE INDEX idx_consultation_createdAt     ON Consultation(createdAt);