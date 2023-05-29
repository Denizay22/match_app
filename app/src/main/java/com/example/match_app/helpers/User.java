package com.example.match_app.helpers;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class User {
    //kayıt olurken fieldlar

    private String name;
    private String surname;
    private String email;


    //profil oluştururken fieldlar

    private String photo;
    private String user_ID;
    private String department;
    private int year;
    private String status; //Ev Arkadaşı Arıyor, Ev Arıyor, Aramıyor
    private int distance_to_campus; //TODO böyle mi tutulacak?
    private int duration;
    private String phone_no;

    public User() {

    }

    public User(String name, String surname, String email, String photo, String user_ID, String department, int year, String status, int distance_to_campus, int duration, String phone_no) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.photo = photo;
        this.user_ID = user_ID;
        this.department = department;
        this.year = year;
        this.status = status;
        this.distance_to_campus = distance_to_campus;
        this.duration = duration;
        this.phone_no = phone_no;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(String user_ID) {
        this.user_ID = user_ID;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getDistance_to_campus() {
        return distance_to_campus;
    }

    public void setDistance_to_campus(int distance_to_campus) {
        this.distance_to_campus = distance_to_campus;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", user_ID='" + user_ID + '\'' +
                ", department='" + department + '\'' +
                ", year=" + year +
                ", status='" + status + '\'' +
                ", distance_to_campus='" + distance_to_campus + '\'' +
                ", duration='" + duration + '\'' +
                ", phone_no='" + phone_no + '\'' +
                '}';
    }
}
