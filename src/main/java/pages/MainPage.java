package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import components.ModalComponent;

public class MainPage {

    private final Page page;

    private final Locator openModalButton;
    private final ModalComponent modal;

    public MainPage(Page page) {
        this.page = page;
        this.openModalButton = page.locator("#open-modal-btn");
        this.modal = new ModalComponent(page);
    }

    public void openModal() {
        openModalButton.click();
    }

    public ModalComponent getModal() {
        return modal;
    }
}