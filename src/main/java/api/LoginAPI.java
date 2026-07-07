package api;

public class LoginAPI {
    public String login(String username, String password) {
        return "token-for-" + username;
    }
}
