package com.example.securytyapp.auths;

import com.example.securytyapp.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Schema(
        name = "RegisterRequest",
        description = "Request body for registration",

        oneOf = RegisterRequest.class,
        example = """
                {
                  "nom": "toto",
                  "prenom": "toto",
                  "email": "toto@gmail.com",
                  "phone": "05454521225",
                  "adresse": "string",
                  "role": "string",
                  "password": "string"
                }""",
        implementation = RegisterRequest.class

)
public class RegisterRequest {

    @Pattern(regexp = "^[a-zA-Z]{3,}$", message = "firstname should be valid")
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    @Pattern(regexp = "^[a-zA-Z]{3,}$", message = "lastname should be valid")
    private String lastName;
    @Email(message = "Email should be valid")
    private String email;
    private Role role;
    private String password;

}
