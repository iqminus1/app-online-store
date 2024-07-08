package uz.pdp.apponlinestore.servlet;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import uz.pdp.apponlinestore.entity.User;
import uz.pdp.apponlinestore.payload.ApiResult;
import uz.pdp.apponlinestore.payload.UserDTO;
import uz.pdp.apponlinestore.service.RequiredServiceManager;
import uz.pdp.apponlinestore.utils.AppConstant;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static uz.pdp.apponlinestore.utils.CommonUtils.gson;

@WebServlet(AppConstant.BASE_PATH + "/auth")
public class AuthServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        resp.setContentType("application/json");

        String collect = req.getReader().lines()
                .collect(Collectors.joining(System.lineSeparator()));

        UserDTO userDTO = gson.fromJson(collect, UserDTO.class);

        EntityManagerFactory factory = RequiredServiceManager.getInstance().getEntityManagerFactory();
        EntityManager entityManager = factory.createEntityManager();

        List<User> resultList = entityManager.createQuery("select t from users t where t.email=:email and t.password=:password", User.class)
                .setParameter("email", userDTO.getEmail())
                .setParameter("password", userDTO.getPassword())
                .getResultList();

        if (resultList.isEmpty()) {
            resp.getWriter().write(gson.toJson(ApiResult.error("email or password don`t equals")));
            return;
        }
        User user = resultList.get(0);

        HttpSession session = req.getSession();
        session.setAttribute("user", user);
        session.setMaxInactiveInterval(7200);
        resp.getWriter().write(gson.toJson(ApiResult.success("Welcome")));
    }
}
