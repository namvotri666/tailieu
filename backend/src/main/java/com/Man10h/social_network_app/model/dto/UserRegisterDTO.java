package com.Man10h.social_network_app.model.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDTO {
    @JsonProperty(value = "username", required = true)
    private String username;

    @JsonProperty(value = "password", required = true)
    private String password;

    @JsonProperty(value = "email", required = true)
    private String email;

    @JsonProperty(value = "firstName", required = true)
    private String firstName;

    @JsonProperty(value = "lastName", required = true)
    private String lastName;

    @JsonProperty(value = "gender", required = true)
    private String gender;
}
