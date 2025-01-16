package it.epicode.marsiflexbe.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Email non può essere vuota")
    @Email(message = "Email non valida")
    private String email;
    private String password;
}
