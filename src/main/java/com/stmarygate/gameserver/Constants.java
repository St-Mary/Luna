package com.stmarygate.gameserver;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.Objects;

public class Constants {
  public static final int VERSION_MAJOR = 0;
  public static final int VERSION_MINOR = 0;
  public static final int VERSION_PATCH = 1;
  public static final String VERSION_BUILD = "SNAPSHOT";

  private static final Dotenv dotenv = Dotenv.load();
  public static final String DB_HOST = Objects.requireNonNull(dotenv.get("DB_HOST"));
  public static final int DB_PORT = Integer.parseInt(Objects.requireNonNull(dotenv.get("DB_PORT")));
  public static final String DB_USER = Objects.requireNonNull(dotenv.get("DB_USER"));
  public static final String DB_PASSWORD = Objects.requireNonNull(dotenv.get("DB_PASSWORD"));
  public static final String DB_DEV_DB = Objects.requireNonNull(dotenv.get("DB_DEV_DB"));
  public static final String DB_PROD_DB = Objects.requireNonNull(dotenv.get("DB_PROD_DB"));
  public static final int PORT = Integer.parseInt(Objects.requireNonNull(dotenv.get("PORT")));
  public static final boolean TEST_CLIENT =
      Boolean.parseBoolean(Objects.requireNonNull(dotenv.get("TEST_CLIENT")));
  public static final String GITHUB_ACTOR = Objects.requireNonNull(dotenv.get("GITHUB_ACTOR"));
  public static final String GITHUB_TOKEN = Objects.requireNonNull(dotenv.get("GITHUB_TOKEN"));
}
