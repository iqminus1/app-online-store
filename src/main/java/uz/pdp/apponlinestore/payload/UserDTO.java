package uz.pdp.apponlinestore.payload;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import uz.pdp.apponlinestore.enums.RoleEnum;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDTO {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;
    @Email
    private String email;

    private RoleEnum role;

    @NotBlank
    private String password;
}
