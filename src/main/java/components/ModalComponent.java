package components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class ModalComponent {

    private final Page page;

    private final Locator root;
    private final Locator title;
    private final Locator cancelBtn;
    private final Locator saveBtn;
    private final Locator questionInput;
    private final Locator answerInput;

    public ModalComponent(Page page) {
        this.page = page;

        // Локатор на саму модалку по классу show
        this.root = page.locator(".modal.show");
        // Заголовок модалки
        this.title = root.locator("h2");
        // Кнопки и поля внутри модалки
        this.cancelBtn = root.locator("#cancelBtn");
        this.saveBtn = root.locator("#saveBtn");
        this.questionInput = root.locator("#questionInput");
        this.answerInput = root.locator("#answerInput");
    }

    public Locator getRoot() {
        return root;
    }

    public Locator getTitle() {
        return title;
    }

    public boolean isVisible() {
        return root.isVisible();
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