package com.springboot.medease.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Profile {

    @NotBlank(message = "firstName is required")
    protected String firstName;

    @NotBlank(message = "lastName is required")
    protected String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Indexed(unique = true, sparse = true)
    protected String email;

    @NotBlank(message = "phoneNumber is required")
    @Indexed(unique = true)
    protected String phoneNumber;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    protected String password;
//
//    @NotBlank(message = "The date of birth  is required")
//    @NotNull(message = "this field must not be null")
//    private Date dateOfBirth;
//
//    @NotBlank(message = "The gender is required")
//    @NotNull(message = "this field must not be null")
//    private String gender;

    protected UserType userType;

}
