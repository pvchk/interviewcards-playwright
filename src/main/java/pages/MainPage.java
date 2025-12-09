package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import components.ModalComponent;
import config.Config;

public class MainPage {

    public static final String URL = "/en";

    private final Page page;

    private final String addCardButton = "#addCardBtn";

    public MainPage(Page page) {
        this.page = page;
    }

    public void open() {
        page.navigate(Config.BASE_URL + URL);
    }

    public void clickAddCard() {
        page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("+ New Card")
        ).click();
    }

    public ModalComponent modal() {
        return new ModalComponent(page);
    }
}