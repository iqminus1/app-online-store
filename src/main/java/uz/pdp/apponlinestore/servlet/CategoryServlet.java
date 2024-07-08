package uz.pdp.apponlinestore.servlet;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import uz.pdp.apponlinestore.entity.Attachment;
import uz.pdp.apponlinestore.entity.Category;
import uz.pdp.apponlinestore.enums.RoleEnum;
import uz.pdp.apponlinestore.payload.ApiResult;
import uz.pdp.apponlinestore.payload.CategoryDTO;
import uz.pdp.apponlinestore.service.RequiredServiceManager;
import uz.pdp.apponlinestore.utils.AppConstant;
import uz.pdp.apponlinestore.utils.CommonUtils;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static uz.pdp.apponlinestore.utils.CommonUtils.checkRole;
import static uz.pdp.apponlinestore.utils.CommonUtils.gson;

@WebServlet(AppConstant.BASE_PATH + "/category")
public class CategoryServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        long id = Long.parseLong(req.getParameter("id"));

        EntityManagerFactory factory = RequiredServiceManager.getInstance().getEntityManagerFactory();
        EntityManager entityManager = factory.createEntityManager();

        List<Category> resultList = entityManager.createQuery("select c from category c where c.id =: id", Category.class)
                .setParameter("id", id).getResultList();

        if (resultList.isEmpty()) {
            resp.getWriter().write(gson.toJson(ApiResult.error("category not found")));
            return;
        }
        Category category = resultList.get(0);

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName(category.getName());
        categoryDTO.setPhotoId(category.getPhoto().getId());
        categoryDTO.setId(category.getId());
        if (Objects.nonNull(category.getParentCategory())) {
            categoryDTO.setParentCategoryId(category.getParentCategory().getId());
        }

        resp.getWriter().write(gson.toJson(categoryDTO));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        checkRole(List.of(RoleEnum.MODERATOR, RoleEnum.ADMIN), req);

        resp.setContentType("application/json");

        String collect = req.getReader()
                .lines()
                .collect(Collectors.joining(System.lineSeparator()));

        CategoryDTO categoryDTO = gson.fromJson(collect, CategoryDTO.class);

        EntityManagerFactory factory = RequiredServiceManager.getInstance().getEntityManagerFactory();
        EntityManager entityManager = factory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        List<Category> resultList = entityManager.createQuery("select c from category c where c.name=:name", Category.class)
                .setParameter("name", categoryDTO.getName())
                .getResultList();

        if (!resultList.isEmpty()) {
            Optional<Category> first = resultList.stream()
                    .filter(category -> {
                        if (Objects.isNull(category.getParentCategory()))
                            if (Objects.isNull(categoryDTO.getParentCategoryId()))
                                return true;
                        if (Objects.nonNull(category.getParentCategory()))
                            return Objects.nonNull(categoryDTO.getParentCategoryId()) && category.getParentCategory().getId().equals(categoryDTO.getParentCategoryId());
                        return false;
                    })
                    .findFirst();

            if (first.isPresent()) {
                resp.getWriter().write(gson.toJson(ApiResult.error("category already have")));
                return;
            }
        }

        List<Attachment> attachments = entityManager.createQuery("select a from attachment a where a.id =: id", Attachment.class)
                .setParameter("id", categoryDTO.getPhotoId())
                .getResultList();

        if (attachments.isEmpty()) {
            resp.getWriter().write(gson.toJson(ApiResult.error("photo no found")));
            return;
        }
        Attachment attachment = attachments.get(0);

        Category parentCategory = null;

        if (Objects.nonNull(categoryDTO.getParentCategoryId())) {
            List<Category> categories = entityManager.createQuery("select c from category c where c.id =: id", Category.class)
                    .setParameter("id", categoryDTO.getParentCategoryId())
                    .getResultList();

            if (categories.isEmpty()) {
                resp.getWriter().write(gson.toJson(ApiResult.error("parent category not found")));
                return;
            }

            parentCategory = categories.get(0);
        }


        Category category = new Category(
                categoryDTO.getName(),
                attachment,
                parentCategory
        );

        entityManager.persist(category);

        transaction.commit();
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        checkRole(List.of(RoleEnum.MODERATOR, RoleEnum.ADMIN), req);
        resp.setContentType("application/json");

        String collect = req.getReader().lines()
                .collect(Collectors.joining(System.lineSeparator()));

        CategoryDTO categoryDTO = gson.fromJson(collect, CategoryDTO.class);

        if (Objects.isNull(categoryDTO.getId())) {
            resp.getWriter().write(gson.toJson(ApiResult.error("category id is null")));
            return;
        }

        Validator validator = RequiredServiceManager.getInstance().getValidatorFactory().getValidator();
        Set<ConstraintViolation<CategoryDTO>> validate = validator.validate(categoryDTO);
        if (!validate.isEmpty()) {
            resp.getWriter().write(gson.toJson(ApiResult.error(validate)));
            return;
        }

        EntityManagerFactory factory = RequiredServiceManager.getInstance().getEntityManagerFactory();
        EntityManager entityManager = factory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        Category category = entityManager.find(Category.class, categoryDTO.getId());

        List<Category> resultList = entityManager.createQuery("select c from category c where c.name=:name", Category.class)
                .setParameter("name", category.getName())
                .getResultList();

        if (!resultList.isEmpty()) {
            Optional<Category> first = resultList.stream()
                    .filter(category1 -> {
                        if (Objects.isNull(category1.getParentCategory()))
                            if (Objects.isNull(categoryDTO.getParentCategoryId()))
                                return true;
                        if (Objects.nonNull(category1.getParentCategory()))
                            return Objects.nonNull(categoryDTO.getParentCategoryId()) &&
                                    category1.getParentCategory().getId().equals(categoryDTO.getParentCategoryId());
                        return false;
                    })
                    .findFirst();

            if (first.isPresent()) {
                resp.getWriter().write(gson.toJson(ApiResult.error("category already have")));
                return;
            }
        }

        if (Objects.nonNull(categoryDTO.getParentCategoryId())) {
            Category parentCategory = entityManager.find(Category.class, categoryDTO.getParentCategoryId());
            category.setParentCategory(parentCategory);
        }
        Attachment attachment = entityManager.find(Attachment.class, categoryDTO.getPhotoId());
        category.setPhoto(attachment);
        category.setName(categoryDTO.getName());

        entityManager.merge(category);

        transaction.commit();

        resp.getWriter().write(gson.toJson(ApiResult.success("category is changed")));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        checkRole(RoleEnum.ADMIN, req);

        resp.setContentType("application/json");

        EntityManagerFactory factory = RequiredServiceManager.getInstance().getEntityManagerFactory();
        EntityManager entityManager = factory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        long id = Long.parseLong(req.getParameter("id"));
        Category category = entityManager.find(Category.class, id);
        entityManager.remove(category);

        transaction.commit();

        List<Category> resultList = entityManager.createQuery("select c from category c where c.id=:id", Category.class)
                .setParameter("id", id)
                .getResultList();

        if (resultList.isEmpty()) {
            resp.getWriter().write(gson.toJson(ApiResult.success("category is deleted")));
            return;
        }
        resp.getWriter().write(gson.toJson(ApiResult.error("category don`t deleted")));
    }
}
