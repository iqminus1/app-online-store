package uz.pdp.apponlinestore.servlet;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import uz.pdp.apponlinestore.entity.Attachment;
import uz.pdp.apponlinestore.entity.User;
import uz.pdp.apponlinestore.enums.RoleEnum;
import uz.pdp.apponlinestore.payload.ApiResult;
import uz.pdp.apponlinestore.service.RequiredServiceManager;
import uz.pdp.apponlinestore.utils.AppConstant;
import uz.pdp.apponlinestore.utils.CommonUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

import static uz.pdp.apponlinestore.utils.CommonUtils.checkRole;
import static uz.pdp.apponlinestore.utils.CommonUtils.gson;

@MultipartConfig(maxFileSize = 1024 * 1024 * 1024)
@WebServlet(AppConstant.BASE_PATH + "/attachment")
public class AttachmentServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = CommonUtils.currentUser(req, resp);

        try (ServletOutputStream outputStream = resp.getOutputStream()) {

            long id = Long.parseLong(req.getParameter("id"));

            EntityManagerFactory factory = RequiredServiceManager.getInstance().getEntityManagerFactory();
            EntityManager entityManager = factory.createEntityManager();
            List<Attachment> attachments = entityManager.createQuery("select t from attachment t where t.id=:id", Attachment.class)
                    .setParameter("id", id)
                    .getResultList();

            if (attachments.isEmpty()) {
                resp.setContentType("application/json");
                resp.getWriter().write(gson.toJson(ApiResult.error("photo not found")));
            }

            Attachment attachment = attachments.get(0);
            String path = attachment.getPath();
            String contentType = attachment.getContentType();
            String originalName = attachment.getOriginalName();

            InputStream inputStream = new FileInputStream(path);
            byte[] bytes = inputStream.readAllBytes();

            resp.setContentType(contentType);
            resp.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", originalName));
            outputStream.write(bytes);

            inputStream.close();
        } catch (Exception e) {
            resp.setContentType("application/json");
            resp.getWriter().write(gson.toJson(ApiResult.error(e.getMessage())));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        checkRole(List.of(RoleEnum.MODERATOR, RoleEnum.ADMIN), req);

        resp.setContentType("application/json");


        Part part = req.getPart("file");
        try (InputStream inputStream = part.getInputStream()) {
            EntityManagerFactory factory = RequiredServiceManager.getInstance().getEntityManagerFactory();
            EntityManager entityManager = factory.createEntityManager();
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();


            String contentType = part.getContentType();//text.img
            String originalName = part.getSubmittedFileName();
            long size = part.getSize();

            String[] split = originalName.split("\\.");
            String s = split[split.length - 1];

            UUID uuid = UUID.randomUUID();

            String changedName = uuid + "." + s;

            String path = "C:\\Users\\user\\Desktop\\app-online-store\\files/" + changedName;

            Path path1 = Path.of(path);

            Files.copy(inputStream, path1);

            Attachment attachment = new Attachment(
                    originalName,
                    changedName,
                    contentType,
                    size,
                    path
            );

            entityManager.persist(attachment);

            transaction.commit();
        } catch (Exception e) {
            resp.getWriter().write(gson.toJson(ApiResult.error(e.getMessage())));
        }
    }
}
