package ru.otus.wh11hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import ru.otus.wh11hibernate.dataset.*;

public class DBServiceHibernateImpl implements DBService {

    private final SessionFactory sessionFactory;

    public DBServiceHibernateImpl() {
        Configuration configuration = new Configuration();

        configuration.addAnnotatedClass(UserDataSet.class);
        configuration.addAnnotatedClass(AddressDataSet.class);
        configuration.addAnnotatedClass(PhoneDataSet.class);

        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:h2:~/test");
        configuration.setProperty("hibernate.connection.username", "sa");
        configuration.setProperty("hibernate.connection.password", "sa");

        configuration.setProperty("hibernate.show_sql", "false");
        configuration.setProperty("hibernate.generate_statistics", "false");
        configuration.setProperty("hibernate.use_sql_comments", "false");


        configuration.setProperty("hibernate.hbm2ddl.auto", "create");
        configuration.setProperty("hibernate.connection.useSSL", "false");
        configuration.setProperty("hibernate.enable_lazy_load_no_trans", "true");

        sessionFactory = createSessionFactory(configuration);

    }

    private static SessionFactory createSessionFactory(Configuration configuration) {
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = builder.build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

    public void addClass(Class classInfo, String tableName) {

    }

    public <T extends DataSet> T load(long id, Class<T> clazz) {
        try (Session session = sessionFactory.openSession()) {
            return session.load(clazz, id);
            //UserDataSetDAO dao = new UserDataSetDAO(session);
            //dao.save(dataSet);
            //session.load();
        }
        //return null;
    }

    public <T extends DataSet> void save(T obj) {
        try (Session session = sessionFactory.openSession()) {
            //UserDataSetDAO dao = new UserDataSetDAO(session);
            //dao.save(dataSet);
            session.save(obj);
        }
    }
}
