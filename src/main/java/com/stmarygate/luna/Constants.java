package com.stmarygate.luna;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.Objects;

public class Constants {
  public static final int VERSION_MAJOR = 0;
  public static final int VERSION_MINOR = 0;
  public static final int VERSION_PATCH = 1;
  public static final String VERSION_BUILD = "SNAPSHOT";

  private static final Dotenv dotenv = Dotenv.load();
  public static final String DB_HOST = Objects.requireNonNull(dotenv.get("DB_HOST"));
  public static final String PASSWORD_HASH = Objects.requireNonNull(dotenv.get("PASSWORD_SALT"));
  public static final String DB_USER = Objects.requireNonNull(dotenv.get("DB_USER"));
  public static final String DB_PASSWORD = Objects.requireNonNull(dotenv.get("DB_PASSWORD"));
  public static final String DB_NAME = Objects.requireNonNull(dotenv.get("DB_NAME"));
  public static final int PORT = Integer.parseInt(Objects.requireNonNull(dotenv.get("PORT")));
  public static final String STOREPASS = Objects.requireNonNull(dotenv.get("STOREPASS"));
  public static final String GITHUB_ACTOR = Objects.requireNonNull(dotenv.get("GITHUB_ACTOR"));
  public static final String GITHUB_TOKEN = Objects.requireNonNull(dotenv.get("GITHUB_TOKEN"));
}
