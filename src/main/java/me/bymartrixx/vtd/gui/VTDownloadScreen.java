package me.bymartrixx.vtd.gui;

import me.bymartrixx.vtd.VTDMod;
import me.bymartrixx.vtd.data.Category;
import me.bymartrixx.vtd.gui.widget.CategorySelectionWidget;
import me.bymartrixx.vtd.gui.widget.PackSelectionHelper;
import me.bymartrixx.vtd.gui.widget.PackSelectionListWidget;
import me.bymartrixx.vtd.gui.widget.SelectedPacksListWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.ScreenTexts;
import net.minecraft.text.Text;

import java.util.List;

public class VTDownloadScreen extends Screen {
    private static final int BUTTON_HEIGHT = 20;
    private static final int DONE_BUTTON_WIDTH = 80;
    private static final int DONE_BUTTON_MARGIN = 10;

    private static final int PACK_SELECTOR_TOP_HEIGHT = 66;
    private static final int PACK_SELECTOR_BOTTOM_HEIGHT = 32;
    private static final int SELECTED_PACKS_WIDTH = 160;
    private static final int SELECTED_PACKS_TOP_HEIGHT = PACK_SELECTOR_TOP_HEIGHT + 20;
    private static final int SELECTED_PACKS_BOTTOM_HEIGHT = PACK_SELECTOR_BOTTOM_HEIGHT + 20;

    private final Screen parent;
    private final Text subtitle;
    private final List<Category> categories;

    private Category currentCategory;

    private CategorySelectionWidget categorySelector;
    private PackSelectionListWidget packSelector;
    private SelectedPacksListWidget selectedPacksList;

    private int leftWidth;

    private final PackSelectionHelper selectionHelper = new PackSelectionHelper();

    public VTDownloadScreen(Screen parent, Text subtitle) {
        super(Text.literal("VTDownloader"));
        this.parent = parent;
        this.subtitle = subtitle;

        this.categories = VTDMod.rpCategories.getCategories();
        this.currentCategory = this.categories.size() > 0 ? this.categories.get(0) : null;
    }

    public boolean selectCategory(Category category) {
        if (this.currentCategory != category) {
            this.currentCategory = category;
            this.categorySelector.setSelectedCategory(category);
            this.packSelector.setCategory(category);
            return true;
        }

        return false;
    }

    @SuppressWarnings("ConstantConditions") // client is marked as nullable
    @Override
    public void closeScreen() {
        this.client.setScreen(this.parent);
    }

    @Override
    protected void init() {
        this.leftWidth = this.width;
        this.packSelector = this.addDrawableChild(new PackSelectionListWidget(this.client, this, this.width,
                this.height, PACK_SELECTOR_TOP_HEIGHT, this.height - PACK_SELECTOR_BOTTOM_HEIGHT,
                this.currentCategory, this.selectionHelper));
        this.packSelector.updateCategories(this.categories);

        this.selectedPacksList = this.addDrawableChild(new SelectedPacksListWidget(this, this.client,
                SELECTED_PACKS_WIDTH, SELECTED_PACKS_TOP_HEIGHT,
                this.height - SELECTED_PACKS_BOTTOM_HEIGHT,
                this.width - SELECTED_PACKS_WIDTH, this.selectionHelper));

        // TODO: Implement better extend button
        this.addDrawableChild(new ButtonWidget(
                this.width - DONE_BUTTON_WIDTH - DONE_BUTTON_MARGIN * 2 - 40, this.height - BUTTON_HEIGHT - DONE_BUTTON_MARGIN, 40, 20, Text.literal("Ext"),
                button -> this.toggleSelectedPacksListExtended()
        ));

        this.addDrawableChild(new ButtonWidget(
                this.width - DONE_BUTTON_WIDTH - DONE_BUTTON_MARGIN, this.height - BUTTON_HEIGHT - DONE_BUTTON_MARGIN,
                DONE_BUTTON_WIDTH, BUTTON_HEIGHT,
                ScreenTexts.DONE, button -> this.closeScreen()
        ));

        this.categorySelector = this.addDrawableChild(new CategorySelectionWidget(this, 32));
        this.categorySelector.setCategories(this.categories);
        this.categorySelector.initCategoryButtons();
        this.categorySelector.setSelectedCategory(this.currentCategory);
    }

    private void toggleSelectedPacksListExtended() {
        boolean extended = this.selectedPacksList.toggleExtended();
        this.leftWidth = extended ? this.width - SELECTED_PACKS_WIDTH : this.width;

        this.categorySelector.updateScreenWidth();
        this.packSelector.updateScreenWidth();
    }

    public int getLeftWidth() {
        return this.leftWidth;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(0);
        super.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        drawCenteredText(matrices, this.textRenderer, this.subtitle, this.width / 2, 20, 0xFFFFFF);

        this.packSelector.renderTooltips(matrices, mouseX, mouseY);
    }
}
