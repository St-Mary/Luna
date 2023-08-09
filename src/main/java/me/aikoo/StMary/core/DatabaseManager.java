package me.aikoo.StMary.core;

import jakarta.persistence.Entity;
import me.aikoo.StMary.BotConfig;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;

public class DatabaseManager {
    private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();

                Map<String, Object> settings = new HashMap<>();
                settings.put(Environment.DRIVER, "org.mariadb.jdbc.Driver");
                settings.put(Environment.URL, "jdbc:mariadb" + "://" + BotConfig.getDatabaseHost() + ":" + BotConfig.getDatabasePort() + "/" + BotConfig.getDatabaseName());
                settings.put(Environment.USER, BotConfig.getDatabaseUsername());
                settings.put(Environment.PASS, BotConfig.getDatabasePassword());
                settings.put(Environment.HBM2DDL_AUTO, "update");
                settings.put(Environment.SHOW_SQL, false);
                settings.put(Environment.FORMAT_SQL, true);
                settings.put(Environment.CONNECTION_PROVIDER, "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
                settings.put(Environment.DIALECT, "org.hibernate.dialect.MariaDBDialect");

                // HikariCP settings

                // Maximum waiting time for a connection from the pool
                settings.put("hibernate.hikari.connectionTimeout", "20000");
                // Minimum number of ideal connections in the pool
                settings.put("hibernate.hikari.minimumIdle", "10");
                // Maximum number of actual connection in the pool
                settings.put("hibernate.hikari.maximumPoolSize", "20");
                // Maximum time that a connection is allowed to sit ideal in the pool
                settings.put("hibernate.hikari.idleTimeout", "300000");

                registryBuilder.applySettings(settings);

                registry = registryBuilder.build();

                MetadataSources sources = new MetadataSources(registry);
                Reflections reflections = new Reflections("fr.redboxing.redbot.database.entities");
                for(Class<?> cls : reflections.getTypesAnnotatedWith(Entity.class)) {
                    sources.addAnnotatedClass(cls);
                }

                Metadata metadata = sources.getMetadataBuilder().build();
                sessionFactory = metadata.getSessionFactoryBuilder().build();
            } catch (Exception e) {
                if (registry != null) {
                    StandardServiceRegistryBuilder.destroy(registry);
                }
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    public static <T> T findById(Long id, Class<T> cls) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        T obj = session.find(cls, id);
        session.getTransaction().commit();
        return obj;
    }

    public static void save(Object obj) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.saveOrUpdate(obj);
        session.getTransaction().commit();
    }

    public static void delete(Object obj) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.delete(obj);
        session.getTransaction().commit();
    }

    public static void update(Object obj) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.update(obj);
        session.getTransaction().commit();
    }

    public static void createOrUpdate(Object object) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.saveOrUpdate(object);
        session.getTransaction().commit();
    }
}
