package com.stmarygate.luna.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

  @Column(nullable = true)
  private String jwt;
}
