package by.baraznov.authenticationservice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "login")
    @Size(min = 4, max = 30, message = "The login must contain between 4 and 30 characters")
    @NotBlank(message = "The login mustn't be empty")
    private String login;
    @Column(name = "password")
    @Size(min = 4, max = 30, message = "The password must contain between 4 and 30 characters")
    @NotBlank(message = "The password mustn't be empty")
    private String password;
}