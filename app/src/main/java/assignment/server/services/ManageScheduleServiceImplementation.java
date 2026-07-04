package assignment.server.services;

import assignment.server.database.ScheduleRepository;
import assignment.shared.model.Schedule;
import assignment.shared.services.ManageScheduleService;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList; // Added this import to fix ArrayList compilation
import java.util.List;

public class ManageScheduleServiceImplementation extends UnicastRemoteObject implements ManageScheduleService
{

    public ManageScheduleServiceImplementation() throws RemoteException
    {
        super();
    }

    @Override
    public boolean addSchedule(Schedule schedule) throws RemoteException
    {
        try
        {
            int realDoctorId = ScheduleRepository.getDoctorIdByUserId(schedule.getDoctorId());
            // -1 means user is not in doctor table
            if (realDoctorId == -1)
            {
                throw new RemoteException("SERVER_ERROR: No matching Doctor record found for this User account.");
            }

            Schedule scheduleWithTheDoctorID = new Schedule(
                    schedule.getScheduleId(),
                    realDoctorId,
                    schedule.getDay(),
                    schedule.getStartTime(),
                    schedule.getEndTime()
            );
            return ScheduleRepository.addSchedule(scheduleWithTheDoctorID);
        }
        catch (SQLException e) {
            throw new RemoteException("DB_ERROR: Failed to add schedule slot", e);
        }
    }

    @Override
    public List<Schedule> getSchedulesByDoctor(int userId) throws RemoteException {
        try {
            int realDoctorId = ScheduleRepository.getDoctorIdByUserId(userId);
            if (realDoctorId == -1) {
                return new ArrayList<>(); // Return empty list safely if user isn't a doctor
            }
            return ScheduleRepository.getSchedulesByDoctorId(realDoctorId);
        } catch (SQLException e) {
            throw new RemoteException("DB_ERROR: Failed to retrieve schedules", e);
        }
    }

    @Override
    public boolean deleteSchedule(int scheduleId) throws RemoteException
    {
        try
        {
            if (ScheduleRepository.countLinkedAppointments(scheduleId) > 0)
            {
                throw new RemoteException("CANNOT_DELETE: Patients have already booked this time slot.");
            }
            return ScheduleRepository.deleteSchedule(scheduleId);
        }
        catch (SQLException e)
        {
            throw new RemoteException("DB_ERROR: Failed to delete schedule slot", e);
        }
    }
}