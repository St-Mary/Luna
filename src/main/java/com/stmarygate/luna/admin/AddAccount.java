package com.stmarygate.luna.admin;

import com.stmarygate.coral.entities.Account;
import com.stmarygate.coral.entities.Player;
import com.stmarygate.coral.utils.BCryptEncryptionUtils;
import com.stmarygate.coral.utils.JWTUtils;
import com.stmarygate.luna.constants.Constants;
import com.stmarygate.luna.database.DatabaseManager;
import com.stmarygate.luna.utils.PlayerUtils;

public class AddAccount {
  public static void main(String[] args) {
    DatabaseManager.getSessionFactory();
    String password = BCryptEncryptionUtils.encrypt(Constants.PASSWORD_RANDOM, "admin");

    String randomNumber = String.valueOf(Constants.PASSWORD_RANDOM.nextInt(1000000));

    Account account = new Account();
    account.setUsername("admin" + randomNumber);
    account.setPassword(password);
    account.setEmail("contact" + randomNumber + "@stmarygate.com");
    Player player = PlayerUtils.generateNewPlayer();
    String token =
        JWTUtils.setSecret(Constants.JWT_SECRET)
            .generateToken(String.valueOf(account.getId()), "admin", account.getEmail());

    account.setJwt(token);
    player.setUsername(account.getUsername());
    DatabaseManager.save(player);
    account.setPlayer(player);
    DatabaseManager.save(account);
    Account p = DatabaseManager.findByUsername(account.getUsername(), Account.class);
    Player player1 = p.getPlayer();
    System.out.println("Username of the player: " + player1.getUsername());
    System.out.println("Exp of the player: " + player1.getExp());
  }
}
