package app.controllers;

import app.entities.Task;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.TaskMapper;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;

public class UserController {

    public static void addRouts(Javalin app, ConnectionPool connectionPool) {
        app.get("/createUser", ctx -> pageCreateUser(ctx, connectionPool));
        app.post("/login", ctx -> login(ctx, connectionPool));
        app.get("/login", ctx -> ctx.render("test.html"));
        app.get("/logout", UserController::logout);
        app.post("/createYourProfile", ctx -> createUser(ctx, connectionPool));
        app.post("/createTask", ctx -> createTask(ctx, connectionPool));
        app.post("/setToDone", ctx -> setToDone(ctx, connectionPool));
        app.post("/deleteTask", ctx -> deleteTask(ctx, connectionPool));
        app.post("/getTaskByID", ctx -> getTaskByID(ctx, connectionPool));
        app.post("/editTask", ctx -> updateTask(ctx, connectionPool));
        app.post("/search", ctx -> getTaskByID(ctx, connectionPool));
    }

    private static void pageCreateUser(Context ctx, ConnectionPool connectionPool) {
        ctx.render("/createUser.html");
    }

    private static void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/");
    }

    public static void login(Context ctx, ConnectionPool connectionPool) {
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");

        try {
            User user = UserMapper.login(username, password, connectionPool);
            ctx.sessionAttribute("currentUser", user);
            List<Task> taskList = TaskMapper.getAllTasksPerUser(user.getUserID(), connectionPool);
            loadTasks(ctx, taskList, connectionPool);
        } catch (DatabaseException e) {
            ctx.attribute("msg", e.getMessage());
            ctx.redirect("/");
        }
    }

    public static void createUser(Context ctx, ConnectionPool connectionPool) {
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");
        try {
            UserMapper.createuser(username, password, connectionPool);
            ctx.sessionAttribute("msg", "Bruger oprettelse successful");
            ctx.redirect("/");
        } catch (DatabaseException e) {
            ctx.sessionAttribute("msg", e.getMessage());
            ctx.redirect("/");
        }
    }

    public static void createTask(Context ctx, ConnectionPool connectionPool) {
        String task = ctx.formParam("inputTask");


        User user = ctx.sessionAttribute("currentUser");
        List<Task> taskList = new ArrayList<>();
        try {
            assert user != null;
            TaskMapper.addTask(user, task, connectionPool);
            taskList = TaskMapper.getAllTasksPerUser(user.getUserID(), connectionPool);
        } catch (DatabaseException e) {
            ctx.sessionAttribute("msg", e.getMessage());
        }
        loadTasks(ctx, taskList, connectionPool);
        ctx.render("/test");


    }

    public static boolean userIsValid(Context ctx) {
        return null != ctx.sessionAttribute("currentUser");
    }

    public static void setToDone(Context ctx, ConnectionPool connectionPool) {

        try {
            Task task = ctx.bodyAsClass(Task.class);
            int taskID = task.getTaskID();
            boolean done = task.isDone();
            TaskMapper.setDoneTo(done, taskID, connectionPool);
            User user = ctx.sessionAttribute("currentUser");
            assert user != null;
            List<Task> taskList = TaskMapper.getAllTasksPerUser(user.getUserID(), connectionPool);
            loadTasks(ctx, taskList, connectionPool);
        } catch (DatabaseException e) {
            ctx.sessionAttribute("msg", e.getMessage());
            ctx.redirect("/");
        }
    }

    public static void loadTasks(Context ctx, List<Task> taskList, ConnectionPool connectionPool) {
        ctx.attribute("taskList", taskList);
        ctx.render("/test.html");
    }

    public static void deleteTask(Context ctx, ConnectionPool connectionPool) {
        Task task = ctx.bodyAsClass(Task.class);
        int taskID = task.getTaskID();
        try {
            TaskMapper.delete(taskID, connectionPool);
            User user = ctx.sessionAttribute("currentUser");
            assert user != null;
            List<Task> taskList = TaskMapper.getAllTasksPerUser(user.getUserID(), connectionPool);
            loadTasks(ctx, taskList, connectionPool);
            ctx.render("/test");
        } catch (DatabaseException e) {
            ctx.sessionAttribute("msg", e.getMessage());
            ctx.redirect("/");
        }
    }

    public static void getTaskByID(Context ctx, ConnectionPool connectionPool) {
        int[] ids = ctx.bodyAsClass(int[].class);
        List<Task> taskList = new ArrayList<>();
        try {
            int i = 0;
            while (i < ids.length) {
                taskList.add(TaskMapper.getTaskById(ids[i], connectionPool));
                i++;
            }

            loadTasks(ctx, taskList, connectionPool);
            ctx.render("/test.html");
        } catch (DatabaseException e) {
            ctx.sessionAttribute("msg", e.getMessage());
            ctx.redirect("/");
        }
    }

    public static void updateTask(Context ctx, ConnectionPool connectionPool) {
        Task task = ctx.bodyAsClass(Task.class);
        int taskID = task.getTaskID();
        String taskName = task.getTaskName();
        try {
            TaskMapper.update(taskID, taskName, connectionPool);
            User user = ctx.sessionAttribute("currentUser");
            assert user != null;
            List<Task> taskList = TaskMapper.getAllTasksPerUser(user.getUserID(), connectionPool);
            loadTasks(ctx, taskList, connectionPool);
            ctx.render("/test.html");
        } catch (DatabaseException e) {
            ctx.sessionAttribute("msg", e.getMessage());
            ctx.redirect("/");
        }
    }
}
