package components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class ModalComponent {

    private final Page page;

    private final Locator modal;
    private final Locator title;
    private final Locator cancelBtn;
    private final Locator saveBtn;
    private final Locator questionInput;
    private final Locator answerInput;

    public ModalComponent(Page page) {
        this.page = page;
        this.modal = page.locator("#cardModal");
        this.title = page.locator("#modalTitle");
        this.cancelBtn = page.locator("#cancelBtn");
        this.saveBtn = page.locator("#saveBtn");
        this.questionInput = page.locator("#questionInput");
        this.answerInput = page.locator("#answerInput");
    }

    public boolean isVisible() {
        return modal.isVisible();
    }

    public Locator getTitle() {
        return title;
    }

    public void clickCancel() {
        cancelBtn.click();
    }

    public void clickSave() {
        saveBtn.click();
    }

    public void fillQuestion(String text) {
        questionInput.fill(text);
    }

    public void fillAnswer(String text) {
        answerInput.fill(text);
    }
}