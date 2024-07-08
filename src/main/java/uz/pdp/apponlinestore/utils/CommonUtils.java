package uz.pdp.apponlinestore.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import uz.pdp.apponlinestore.config.TypeAdapterTimestamp;
import uz.pdp.apponlinestore.entity.User;
import uz.pdp.apponlinestore.enums.RoleEnum;
import uz.pdp.apponlinestore.payload.ApiResult;
import uz.pdp.apponlinestore.service.RequiredServiceManager;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

public class CommonUtils {

    public static Gson gson = new GsonBuilder()
            .registerTypeAdapter(Timestamp.class, new TypeAdapterTimestamp())
            .setPrettyPrinting()
            .create();

    public static void checkRole(RoleEnum role, HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (Objects.isNull(session))
            throw new RuntimeException("Forbidden");
        User currentUser = (User) session.getAttribute("user");
        if (Objects.isNull(currentUser))
            throw new RuntimeException("Forbidden");
        if (!Objects.equals(currentUser.getRole(), role))
            throw new RuntimeException("Forbidden");
    }

    public static void checkRole(List<RoleEnum> roles, HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (Objects.isNull(session))
            throw new RuntimeException("Forbidden");
        User currentUser = (User) session.getAttribute("user");
        if (Objects.isNull(currentUser))
            throw new RuntimeException("Forbidden");

        if (!roles.contains(currentUser.getRole()))
            throw new RuntimeException("Forbidden");
    }

    public static User currentUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (Objects.isNull(session)) {
            resp.getWriter().write(gson.toJson(ApiResult.error("Unauthorized")));
            throw new RuntimeException("user not found");
        } else {
            return (User) session.getAttribute("user");
        }
    }

    public static EntityManager getEntityManager(){
        EntityManagerFactory factory = RequiredServiceManager.getInstance().getEntityManagerFactory();
        return factory.createEntityManager();
    }

    public static void successResponse(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(ApiResult.success(data)));
    }
}
