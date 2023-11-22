package me.aikoo.stmary.discord.core.managers;

import jakarta.persistence.Entity;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.aikoo.stmary.discord.core.constants.BotConfigConstant;
import me.aikoo.stmary.discord.core.database.AdministratorEntity;
import me.aikoo.stmary.discord.core.database.MoveEntity;
import me.aikoo.stmary.discord.core.database.PlayerEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Database manager for the application. */
public class DatabaseManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseManager.class);
  private static StandardServiceRegistry registry;
  private static SessionFactory sessionFactory;

  /**
   * Retrieve or build the Hibernate session.
   *
   * @return The Hibernate session.
   */
  public static SessionFactory getSessionFactory() {
    if (sessionFactory == null) {
      try {
        StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();

        Map<String, Object> settings = getStringObjectMap();

        registryBuilder.applySettings(settings);

        registry = registryBuilder.build();

        MetadataSources sources = new MetadataSources(registry);
        Reflections reflections = new Reflections("me.aikoo.stmary.discord.core.database");
        for (Class<?> cls : reflections.getTypesAnnotatedWith(Entity.class)) {
          sources.addAnnotatedClass(cls);
        }

        Metadata metadata = sources.getMetadataBuilder().build();
        sessionFactory = metadata.getSessionFactoryBuilder().build();
      } catch (Exception e) {
        if (registry != null) {
          StandardServiceRegistryBuilder.destroy(registry);
        }

        LOGGER.error("An error occurred while building the Hibernate session factory: ", e);
      }
    }
    return sessionFactory;
  }

  /**
   * Get the settings for the Hibernate session.
   *
   * @return The settings for the Hibernate session.
   */
  @NotNull
  private static Map<String, Object> getStringObjectMap() {
    Map<String, Object> settings = new HashMap<>();
    settings.put(Environment.DRIVER, "org.mariadb.jdbc.Driver");
    settings.put(
        Environment.URL,
        "jdbc:mariadb"
            + "://"
            + BotConfigConstant.getDatabaseHost()
            + ":"
            + BotConfigConstant.getDatabasePort()
            + "/"
            + BotConfigConstant.getDatabaseName());
    settings.put(Environment.USER, BotConfigConstant.getDatabaseUsername());
    settings.put(Environment.PASS, BotConfigConstant.getDatabasePassword());
    settings.put(Environment.HBM2DDL_AUTO, "update");
    settings.put(Environment.SHOW_SQL, false);
    settings.put(Environment.FORMAT_SQL, true);
    settings.put(
        Environment.CONNECTION_PROVIDER,
        "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");

    // HikariCP settings

    settings.put("hibernate.hikari.connectionTimeout", "20000");
    settings.put("hibernate.hikari.minimumIdle", "10");
    settings.put("hibernate.hikari.maximumPoolSize", "20");
    settings.put("hibernate.hikari.idleTimeout", "300000");
    return settings;
  }

  /** Close the Hibernate session. */
  public static void shutdown() {
    if (registry != null) {
      StandardServiceRegistryBuilder.destroy(registry);
    }
  }

  /**
   * Find an entity by its ID.
   *
   * @param id The ID of the entity.
   * @param cls The class of the entity.
   * @param <T> The type of the entity.
   * @return The entity corresponding to the ID, or null if not found.
   */
  public static <T> T findById(Long id, Class<T> cls) {
    Session session = getSessionFactory().openSession();
    session.beginTransaction();
    T obj = session.find(cls, id);
    session.getTransaction().commit();
    session.close();
    return obj;
  }

  /**
   * Save or update an object in the database.
   *
   * @param obj The object to save or update.
   */
  public static void save(Object obj) {
    Session session = getSessionFactory().openSession();
    session.beginTransaction();
    session.persist(obj);
    session.getTransaction().commit();
    session.close();
  }

  /**
   * Delete an object from the database.
   *
   * @param obj The object to delete.
   */
  public static void delete(Object obj) {
    Session session = getSessionFactory().openSession();
    session.beginTransaction();
    session.remove(obj);
    session.getTransaction().commit();
    session.close();
  }

  /**
   * Update an object in the database.
   *
   * @param obj The object to update.
   */
  public static void update(Object obj) {
    Session session = getSessionFactory().openSession();
    session.beginTransaction();
    session.merge(obj);
    session.getTransaction().commit();
    session.close();
  }

  /**
   * Get a player by their Discord ID.
   *
   * @param idLong The Discord ID of the player.
   * @return The player corresponding to the Discord ID, or null if not found.
   */
  public static PlayerEntity getPlayer(long idLong) {
    Session session = getSessionFactory().openSession();
    session.beginTransaction();
    PlayerEntity player =
        session
            .createQuery("from PlayerEntity where discordId = :discordId", PlayerEntity.class)
            .setParameter("discordId", idLong)
            .uniqueResult();
    session.getTransaction().commit();
    session.close();
    return player;
  }

  /**
   * Get an administrator by their Discord ID.
   *
   * @param idLong The Discord ID of the administrator.
   * @return The administrator corresponding to the Discord ID, or null if not found.
   */
  public static AdministratorEntity getAdministrator(long idLong) {
    Session session = getSessionFactory().openSession();
    session.beginTransaction();
    AdministratorEntity admin =
        session
            .createQuery(
                "from AdministratorEntity where discordId = :discordId", AdministratorEntity.class)
            .setParameter("discordId", idLong)
            .uniqueResult();
    session.getTransaction().commit();
    session.close();
    return admin;
  }

  /**
   * Check if a user is an administrator.
   *
   * @param idLong The Discord ID of the user to check.
   * @return True if the user is an administrator, otherwise False.
   */
  public static boolean isAdministrator(long idLong) {
    Session session = getSessionFactory().openSession();
    session.beginTransaction();
    AdministratorEntity admin =
        session
            .createQuery(
                "from AdministratorEntity where discordId = :discordId", AdministratorEntity.class)
            .setParameter("discordId", idLong)
            .uniqueResult();
    session.getTransaction().commit();
    session.close();
    return admin != null;
  }

  /**
   * Get the movements of a player by their UUID.
   *
   * @param uuid The UUID of the player.
   * @return The movements of the player corresponding to the UUID, or null if not found.
   */
  public static MoveEntity getMove(UUID uuid) {
    Session session = getSessionFactory().openSession();
    session.beginTransaction();
    MoveEntity moves =
        session
            .createQuery("from MoveEntity where playerId = :uuid", MoveEntity.class)
            .setParameter("uuid", uuid)
            .uniqueResult();
    session.getTransaction().commit();
    session.close();
    return moves;
  }
}
