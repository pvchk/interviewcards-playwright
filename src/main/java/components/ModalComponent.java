package components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import assertions.ModalAssertions;

public class ModalComponent {

    private final Page page;
    private final Locator modalRoot;
    private final Locator modalTitle;
    private final Locator closeButton;

    public ModalComponent(Page page) {
        this.page = page;
        this.modalRoot = page.locator("#modal-dialog");
        this.modalTitle = modalRoot.locator(".modal-title");
        this.closeButton = modalRoot.locator(".close-btn");
    }

    public boolean isVisible() {
        return modalRoot.isVisible();
    }

    public void close() {
        closeButton.click();
    }

    public ModalAssertions should() {
        return new ModalAssertions(this);
    }

    public Locator getRoot() {
        return modalRoot;
    }

    public Locator getTitle() {
        return modalTitle;
    }
}