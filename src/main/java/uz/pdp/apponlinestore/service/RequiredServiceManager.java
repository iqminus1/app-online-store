package uz.pdp.apponlinestore.service;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;

public class RequiredServiceManager {
    private static RequiredServiceManager instance;
    private static EntityManagerFactory entityManagerFactory;
    private static ValidatorFactory validatorFactory;

    private RequiredServiceManager() {

    }

    public static RequiredServiceManager getInstance() {
        if (instance == null) {
            instance = new RequiredServiceManager();
            entityManagerFactory = Persistence.createEntityManagerFactory("app_online_store");
            validatorFactory = Validation.buildDefaultValidatorFactory();
        }
        return instance;
    }

    public  EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public  ValidatorFactory getValidatorFactory() {
        return validatorFactory;
    }
}
