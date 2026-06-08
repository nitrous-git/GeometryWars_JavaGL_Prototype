package game;

import helpers.FontRenderer;

import static org.lwjgl.opengl.GL11.*;

public class GameOverPanel {

    private final int panelWidth = 460;
    private final int panelHeight = 260;

    private final int buttonWidth = 220;
    private final int buttonHeight = 56;

    private final int panelX;
    private final int panelY;

    private final int buttonX;
    private final int buttonY;

    public GameOverPanel() {
        panelX = Game.WIDTH / 2 - panelWidth / 2;
        panelY = Game.HEIGHT / 2 - panelHeight / 2;

        buttonX = Game.WIDTH / 2 - buttonWidth / 2;
        buttonY = panelY + 160;
    }

    public void draw(FontRenderer fontRenderer) {
        glPushMatrix();
        glLoadIdentity();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glDisable(GL_TEXTURE_2D);

        // Dark screen overlay.
        drawRect(0, 0, Game.WIDTH, Game.HEIGHT, 0.0f, 0.0f, 0.0f, 0.55f);

        // Main panel.
        drawRect(panelX, panelY, panelWidth, panelHeight, 0.03f, 0.04f, 0.08f, 0.92f);

        // Panel border.
        drawBorder(panelX, panelY, panelWidth, panelHeight, 0.2f, 0.9f, 1.0f, 0.9f);

        // Replay button.
        drawRect(buttonX, buttonY, buttonWidth, buttonHeight, 0.08f, 0.14f, 0.22f, 0.95f);
        drawBorder(buttonX, buttonY, buttonWidth, buttonHeight, 0.4f, 1.0f, 1.0f, 1.0f);

        glEnable(GL_TEXTURE_2D);

        fontRenderer.drawString(panelX + 118, panelY + 52, "GAME OVER", 1.0f, 0.2f, 0.2f, 1.0f);
        fontRenderer.drawString(panelX + 116, panelY + 105, "All enemies destroyed", 0.8f, 0.9f, 1.0f, 1.0f);
        fontRenderer.drawString(buttonX + 58, buttonY + 36, "REPLAY", 1.0f, 1.0f, 1.0f, 1.0f);

        glPopMatrix();
    }

    public boolean isReplayButtonHit(double mouseX, double mouseY) {
        return mouseX >= buttonX
                && mouseX <= buttonX + buttonWidth
                && mouseY >= buttonY
                && mouseY <= buttonY + buttonHeight;
    }

    private void drawRect(int x, int y, int width, int height, float r, float g, float b, float a) {
        glColor4f(r, g, b, a);

        glBegin(GL_QUADS);
        glVertex2f(x, y);
        glVertex2f(x + width, y);
        glVertex2f(x + width, y + height);
        glVertex2f(x, y + height);
        glEnd();
    }

    private void drawBorder(int x, int y, int width, int height, float r, float g, float b, float a) {
        glColor4f(r, g, b, a);
        glLineWidth(2.0f);

        glBegin(GL_LINE_LOOP);
        glVertex2f(x, y);
        glVertex2f(x + width, y);
        glVertex2f(x + width, y + height);
        glVertex2f(x, y + height);
        glEnd();
    }
}