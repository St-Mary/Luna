package com.stmarygate.luna.admin;

import com.stmarygate.coral.utils.BCryptEncryptionUtils;
import com.stmarygate.coral.utils.JWTUtils;
import com.stmarygate.luna.Constants;
import com.stmarygate.luna.database.DatabaseManager;
import com.stmarygate.luna.database.entities.Account;
import java.security.SecureRandom;

public class AddAccount {
  public static void main(String[] args) {
    DatabaseManager.getSessionFactory();
    SecureRandom random = new SecureRandom(Constants.PASSWORD_HASH.getBytes());
    String password = BCryptEncryptionUtils.encrypt(random, "admin");

    String randomNumber = String.valueOf(random.nextInt(1000000));

    Account account = new Account();
    account.setUsername("admin" + randomNumber);
    account.setPassword(password);
    account.setEmail("contact" + randomNumber + "@stmarygate.com");

    String token =
        JWTUtils.setSecret(Constants.JWT_SECRET)
            .generateToken(String.valueOf(account.getId()), "admin", account.getEmail());

    account.setJwt(token);
    DatabaseManager.save(account);
  }
}
