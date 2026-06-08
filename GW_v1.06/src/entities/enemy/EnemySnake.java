package entities.enemy;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Rectangle;
import java.util.ArrayList;

import entities.Entity;
import helpers.Texture;
import helpers.TextureHelper;

/**
 * EnemySnake
 * Head is the only damageable part.
 * Body follows the head trail with a sinusoidal oscillation.
 */
public class EnemySnake extends EnemyEntity {

    private Texture headTex;
    private Texture bodyTex;

    private static final int BODY_SEGMENTS = 15;
    private static final double SEGMENT_DISTANCE = 14.0;
    private static final double BODY_OSC_AMPLITUDE = 8.0;
    private static final double HEAD_WIGGLE_ANGLE = 0.00; // radians
    private static final int MAX_TRAIL_POINTS = 320;

    // If the head sprite faces the wrong direction, 90.0 or 180.0
    private static final double ROTATION_OFFSET = 270.0;

    private double time = 0.0;

    private ArrayList<SnakeTrailPoint> trail = new ArrayList<SnakeTrailPoint>();

    public EnemySnake(double x, double y, double width, double height) {
        super(x, y, width, height);

        texHelper = new TextureHelper();

        headTex = texHelper.loadTexture("res/EnemySnake_Head.png", "PNG");
        bodyTex = texHelper.loadTexture("res/EnemySnake_Body.png", "PNG");

        super.enemyTex = headTex;
        super.setEnemyID("EnemySnake");

        // Current Game collision logic kills when health <= 0 before the hit.
        // health = 2 means roughly 3 bullet impacts.
        setHealth(1);

        // Slightly slower than basic follower, because the body makes it more threatening.
        setXVelocity(0.14);
        setYVelocity(0.14);

        for (int i = 0; i < MAX_TRAIL_POINTS; i++) {
            trail.add(new SnakeTrailPoint(x, y, 0.0));
        }
    }

    @Override
    public void update(int delta, double player_x, double player_y) {
        time += delta * 0.015;

        double distance_x = player_x - this.x;
        double distance_y = player_y - this.y;

        double baseAngle = Math.atan2(distance_y, distance_x);

        // Small detour / wiggle around the direct follower direction.
        double wiggle = Math.sin(time) * HEAD_WIGGLE_ANGLE;
        double moveAngle = baseAngle + wiggle;

        setEnemyAngle(Math.toDegrees(moveAngle) + ROTATION_OFFSET);

        comp_x = vel_x * Math.cos(-moveAngle);
        this.x += delta * comp_x;

        comp_y = vel_y * Math.sin(-moveAngle);
        this.y -= delta * comp_y;

        addTrailPoint(this.x, this.y, moveAngle);
    }

    private void addTrailPoint(double x, double y, double angle) {
        trail.add(0, new SnakeTrailPoint(x, y, angle));

        while (trail.size() > MAX_TRAIL_POINTS) {
            trail.remove(trail.size() - 1);
        }
    }

    @Override
    public void draw() {
        glColor3f(1.0f, 1.0f, 1.0f);
        glEnable(GL_BLEND);

        // Draw tail first, then head on top.
        for (int i = BODY_SEGMENTS; i >= 1; i--) {
            SnakeTrailPoint point = getPointAtDistance(i * SEGMENT_DISTANCE);

            double dirX = Math.cos(-point.angle);
            double dirY = -Math.sin(-point.angle);

            double perpX = -dirY;
            double perpY = dirX;

            double osc = Math.sin(time - i * 0.75) * BODY_OSC_AMPLITUDE;

            float bodyX = (float)(point.x + perpX * osc);
            float bodyY = (float)(point.y + perpY * osc);

            float bodyRot = (float)(Math.toDegrees(point.angle) + ROTATION_OFFSET);

            drawQuadTex(
                    bodyX,
                    bodyY,
                    (float)(width * 0.82),
                    (float)(height * 0.82),
                    bodyRot,
                    bodyTex
            );
        }

        drawQuadTex(
                (float)x,
                (float)y,
                (float)width,
                (float)height,
                (float)super.enemy_angle,
                headTex
        );
    }

    private SnakeTrailPoint getPointAtDistance(double targetDistance) {
        if (trail.isEmpty()) {
            return new SnakeTrailPoint(x, y, 0.0);
        }

        double travelled = 0.0;

        for (int i = 0; i < trail.size() - 1; i++) {
            SnakeTrailPoint a = trail.get(i);
            SnakeTrailPoint b = trail.get(i + 1);

            double dx = b.x - a.x;
            double dy = b.y - a.y;
            double segmentLength = Math.sqrt(dx * dx + dy * dy);

            if (segmentLength <= 0.0001) {
                continue;
            }

            if (travelled + segmentLength >= targetDistance) {
                double t = (targetDistance - travelled) / segmentLength;

                double px = a.x + dx * t;
                double py = a.y + dy * t;

                return new SnakeTrailPoint(px, py, a.angle);
            }

            travelled += segmentLength;
        }

        return trail.get(trail.size() - 1);
    }

    public boolean intersectsHead(Entity other) {
        Rectangle headRect = new Rectangle(
                (int)(x - width / 2.0),
                (int)(y - height / 2.0),
                (int)width,
                (int)height
        );

        Rectangle otherRect = new Rectangle(
                (int)(other.getX() - other.getWidth() / 2.0),
                (int)(other.getY() - other.getHeight() / 2.0),
                (int)other.getWidth(),
                (int)other.getHeight()
        );

        return headRect.intersects(otherRect);
    }

    @Override
    public void drawQuadTex(float x, float y, float width, float height, float rot, Texture texture) {
        texture.bind();

        glLoadIdentity();

        glPushMatrix();
        {
            width /= 2;
            height /= 2;

            glTranslatef(x, y, 0);
            glRotatef(rot, 0, 0, 1);

            glBegin(GL_QUADS);
            {
                glTexCoord2f(0, 0);
                glVertex2f(-width, -height);

                glTexCoord2f(1, 0);
                glVertex2f(-width, height);

                glTexCoord2f(1, 1);
                glVertex2f(width, height);

                glTexCoord2f(0, 1);
                glVertex2f(width, -height);
            }
            glEnd();
        }
        glPopMatrix();
    }

    private static class SnakeTrailPoint {
        double x;
        double y;
        double angle;

        SnakeTrailPoint(double x, double y, double angle) {
            this.x = x;
            this.y = y;
            this.angle = angle;
        }
    }
}