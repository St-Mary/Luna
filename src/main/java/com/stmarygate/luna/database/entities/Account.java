package com.stmarygate.luna.database.entities;

import com.stmarygate.luna.Constants;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

@Setter
@Getter
@Entity
@Table(name = "accounts")
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false)
  private String password; // Hashed password

  @Column(nullable = false, unique = true)
  private String email;

  public boolean checkPassword(String incomingPassword) {
    BCryptPasswordEncoder bCryptPasswordEncoder =
        new BCryptPasswordEncoder(16, new SecureRandom(Constants.PASSWORD_HASH.getBytes()));
    return bCryptPasswordEncoder.matches(incomingPassword, password);
  }
}
