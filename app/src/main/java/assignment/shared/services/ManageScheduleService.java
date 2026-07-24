package assignment.shared.services;

import assignment.shared.model.Schedule;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ManageScheduleService extends Remote {
  boolean addSchedule(String token, Schedule schedule) throws RemoteException;

  List<Schedule> getSchedulesByDoctor(String token, int userId) throws RemoteException;

  boolean deleteSchedule(String token, int scheduleId) throws RemoteException;
}
