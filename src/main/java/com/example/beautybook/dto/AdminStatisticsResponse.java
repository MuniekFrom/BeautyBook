package com.example.beautybook.dto;

public class AdminStatisticsResponse {

    private long servicesCount;
    private long employeesCount;
    private long appointmentsCount;
    private long bookedAppointmentsCount;
    private long completedAppointmentsCount;
    private long cancelledAppointmentsCount;

    public AdminStatisticsResponse(long servicesCount,
                                   long employeesCount,
                                   long appointmentsCount,
                                   long bookedAppointmentsCount,
                                   long completedAppointmentsCount,
                                   long cancelledAppointmentsCount) {
        this.servicesCount = servicesCount;
        this.employeesCount = employeesCount;
        this.appointmentsCount = appointmentsCount;
        this.bookedAppointmentsCount = bookedAppointmentsCount;
        this.completedAppointmentsCount = completedAppointmentsCount;
        this.cancelledAppointmentsCount = cancelledAppointmentsCount;
    }

    public long getServicesCount() {
        return servicesCount;
    }

    public long getEmployeesCount() {
        return employeesCount;
    }

    public long getAppointmentsCount() {
        return appointmentsCount;
    }

    public long getBookedAppointmentsCount() {
        return bookedAppointmentsCount;
    }

    public long getCompletedAppointmentsCount() {
        return completedAppointmentsCount;
    }

    public long getCancelledAppointmentsCount() {
        return cancelledAppointmentsCount;
    }
}