package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import config.Config;

public class LoginPage {
    private final Page page;
    public static final String LOGIN_URL = "/login";
    private static final String PASSWORD_INPUT = "#password";
    private static final String USERNAME_INPUT = "#username";

    public LoginPage(Page page) {
        this.page = page;
    }

    public void open() {
        page.navigate(Config.BASE_URL + LOGIN_URL);
    }

    public void login(String username, String password) {
        page.fill(USERNAME_INPUT, username);
        page.fill(PASSWORD_INPUT, password);
        page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Login")
        ).click();

    }
}
