package com.kops.sem_tracker.repository;

import com.kops.sem_tracker.entyties.TimeTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;

public interface TimeTableRepository extends JpaRepository<TimeTable, Long> {

    // Find regular schedules by day of week
    List<TimeTable> findByDay(String day);

    // Find by username
    List<TimeTable> findByUsername(String username);
    List<TimeTable> findByUsernameAndDay(String username, String day);

    // Find schedules with start time between given times
    @Query("SELECT t FROM TimeTable t WHERE t.startTime BETWEEN :start AND :end")
    List<TimeTable> findByStartTimeBetween(@Param("start") LocalTime start, @Param("end") LocalTime end);

    // Find special schedules by specific date
    @Query("SELECT t FROM TimeTable t WHERE t.specialDate = :date AND t.isSpecialSchedule = true")
    List<TimeTable> findBySpecialDate(@Param("date") String date);

    // Find all special schedules
    List<TimeTable> findByIsSpecialScheduleTrue();

    // Find regular schedules (non-special) by day of week
    @Query("SELECT t FROM TimeTable t WHERE t.day = :day AND t.isSpecialSchedule = false")
    List<TimeTable> findRegularByDay(@Param("day") String day);

    // Find schedules by day, including special schedules for that specific date
    @Query("SELECT t FROM TimeTable t WHERE " +
            "(t.isSpecialSchedule = false AND t.day = :day) OR " +
            "(t.isSpecialSchedule = true AND t.specialDate = :date)")
    List<TimeTable> findByDayAndDate(@Param("day") String day, @Param("date") String date);

    // Find upcoming events between time range (for notifications)
    @Query("SELECT t FROM TimeTable t WHERE " +
            "t.startTime BETWEEN :start AND :end AND " +
            "(t.isSpecialSchedule = false OR " +
            "(t.isSpecialSchedule = true AND t.specialDate = :currentDate))")
    List<TimeTable> findUpcomingEvents(@Param("start") LocalTime start,
                                       @Param("end") LocalTime end,
                                       @Param("currentDate") String currentDate);

    // Find schedules by notification preference
    List<TimeTable> findByNotificationPreference(String notificationPreference);

    // Find schedules by location
    List<TimeTable> findByLocationContainingIgnoreCase(String location);

    // Find schedules by lecturer
    List<TimeTable> findByLecturerContainingIgnoreCase(String lecturer);

    // Find schedules by subject
    List<TimeTable> findBySubjectContainingIgnoreCase(String subject);
}