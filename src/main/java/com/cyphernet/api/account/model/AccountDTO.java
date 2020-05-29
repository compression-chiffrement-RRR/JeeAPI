package com.cyphernet.api.account.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class AccountDTO {
    private String uuid;
    @NotNull(message = "Please provide a email")
    @Length(min=1, message = "Please don't provide a empty email")
    @Email(message = "Your email format is wrong !")
    private String email;
    @NotNull(message = "Please provide a username")
    @Length(min=1, message = "Please don't provide a empty username")
    private String username;
    @NotNull(message = "Please provide a password")
    private String password;
}
