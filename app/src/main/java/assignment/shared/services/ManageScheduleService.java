package assignment.shared.services;

import assignment.shared.model.Schedule;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ManageScheduleService extends Remote {
  boolean addSchedule(Schedule schedule) throws RemoteException;

  List<Schedule> getSchedulesByDoctor(int userId) throws RemoteException;

  boolean deleteSchedule(int scheduleId) throws RemoteException;
}
