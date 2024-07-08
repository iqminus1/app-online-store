package uz.pdp.apponlinestore.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.java.Log;
import uz.pdp.apponlinestore.entity.User;
import uz.pdp.apponlinestore.enums.RoleEnum;
import uz.pdp.apponlinestore.service.RequiredServiceManager;

import java.util.List;

@Log
@WebListener
public class StartStopListener implements ServletContextListener {


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.err.println("=============== STARTED ================");
        initializeEntityManagerFactor();
        createSuperUser();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.err.println("=============== SHUT DOWN ================");
        log.warning("System is started");
    }

    private void initializeEntityManagerFactor() {
        RequiredServiceManager.getInstance();
    }

    private void createSuperUser() {

        EntityManagerFactory factory = RequiredServiceManager.getInstance().getEntityManagerFactory();
        EntityManager entityManager = factory.createEntityManager();

        TypedQuery<User> query = entityManager.createQuery("select t from users t where t.role=:role", User.class);
        query.setParameter("role", RoleEnum.ADMIN);

        List<User> admins = query.getResultList();

        if (admins.isEmpty()){

            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            User user = new User(
                    "admin",
                    "admin",
                    "admin@gmail.com",
                    RoleEnum.ADMIN,
                    "root123"
            );

            entityManager.persist(user);

            transaction.commit();
        }
    }
}
