package com.plim.kati_app.domain.model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("name")
    private String name;

    @SerializedName("birth")
    private LocalDate birth;

    @SerializedName("address")
    private String address;

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", birth=" + birth +
                ", address='" + address + '\'' +
                '}';
    }
}
