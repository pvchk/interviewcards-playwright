package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import components.ModalComponent;
import config.Config;

public class MainPage {

    public static final String URL = "/en";

    private final Page page;

    private final String logoutButton = "//button[normalize-space()='Logout']";

    public static final String INVALID_USERNAME_PASSWORD = "Invalid username or password. Please try again.";

    public MainPage(Page page) {
        this.page = page;
    }

    public void open() {
        page.navigate(Config.BASE_URL + URL);
    }

    public ModalComponent clickAddCard() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("+ New Card"))
                .waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("+ New Card")).click();
        return new ModalComponent(page);
    }

    public ModalComponent modal() {
        return new ModalComponent(page);
    }

    public MainPage title() {
        return new MainPage(page);
    }

    public boolean isLogoutButtonDisplayed() {
        return page.locator(logoutButton).isVisible();
    }

}