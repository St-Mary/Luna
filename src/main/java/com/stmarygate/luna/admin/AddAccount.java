package com.stmarygate.luna.admin;

import com.stmarygate.coral.utils.BCryptEncryptionUtils;
import com.stmarygate.coral.utils.JWTUtils;
import com.stmarygate.luna.Constants;
import com.stmarygate.luna.database.DatabaseManager;
import com.stmarygate.luna.database.entities.Account;

public class AddAccount {
  public static void main(String[] args) {
    DatabaseManager.getSessionFactory();
    String password = BCryptEncryptionUtils.encrypt(Constants.PASSWORD_RANDOM, "admin");

    String randomNumber = String.valueOf(Constants.PASSWORD_RANDOM.nextInt(1000000));

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
