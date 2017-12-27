package com.mike;

public class LogItem {

    String date;
    String ipAddress;
    String request;
    int status;
    String userAgent;

    public LogItem(String date, String ipAddress, String request, int status, String userAgent) {
        this.date = date;
        this.ipAddress = ipAddress;
        this.request = request;
        this.status = status;
        this.userAgent = userAgent;
    }

    @Override
    public String toString() {
        return "LogItem{" +
                "date='" + date + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", request='" + request + '\'' +
                ", status='" + status + '\'' +
                ", userAgent='" + userAgent + '\'' +
                '}';
    }
}
