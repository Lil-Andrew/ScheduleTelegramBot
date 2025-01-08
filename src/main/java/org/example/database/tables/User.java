package org.example.database.tables;

import jakarta.persistence.*;
import org.example.Main;
import org.example.database.DataBase;
import org.hibernate.Session;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", unique = true)
    private Long userId;

    @Column(name = "chat_id", unique = true)
    private Long chatId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "user_group")
    private String group;

    @Column(name = "group_id")
    private String groupId;

    @Column(name = "username")
    private String username;

    @Column(name = "state_with_bot")
    private String stateWithBot;

    public static boolean isInDatabase(Long userId) {
        try (Session session = DataBase.getSession()) {
            User user = session.createQuery("From User Where userId = :userId", User.class)
                    .setParameter("userId", userId).uniqueResult();
            return user != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isInDatabase(User user1) {
        try (Session session = DataBase.getSession()) {
            User user = session.createQuery("From User Where userId = :userId", User.class).setParameter("userId", user1.getUserId()).uniqueResult();
            return user != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static void setStateWithBotForUser(Long id, String stateWithBot) {
        try(Session session = DataBase.getSession()) {
            session.beginTransaction();
            User user = findById(id);
            user.setStateWithBot(stateWithBot);
            session.merge(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            Main.LOGGER.error(e.getMessage());
        }
    }

    public static User findById(Long id) {
        try (Session session = DataBase.getSession()) {
            return session.createQuery("FROM User WHERE userId = :userId", User.class).setParameter("userId", id).uniqueResult();
        } catch (Exception e) {
            Main.LOGGER.error(e.getMessage());
        }
        return null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getStateWithBot() {
        return stateWithBot;
    }

    public void setStateWithBot(String stateWithBot) {
        this.stateWithBot = stateWithBot;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
