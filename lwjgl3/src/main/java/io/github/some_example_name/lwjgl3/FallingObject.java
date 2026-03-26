package io.github.some_example_name.lwjgl3;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * FallingObject — Abstract base class for all falling game objects.
 */
public abstract class FallingObject extends MobileEntity {

    /** Shared texture cache so each PNG is loaded only once. */
    private static final Map<String, Texture> TEXTURE_CACHE = new HashMap<>();

    /** The type of this falling object — determines game effects. */
    private final ObjectType type;

    /** Optional sprite asset path inside the shared assets folder. */
    private final String texturePath;

    protected FallingObject(float x, float y, float size, float fallSpeed,
                            ObjectType type, String texturePath) {
        super(x, y, size, size, 0f, -fallSpeed);
        this.type = type;
        this.texturePath = texturePath;
        setBorderBound(false);
    }

    protected abstract void drawFallingShape(ShapeRenderer sr);
    protected abstract Color getShapeColor();

    @Override
    public void drawShape(ShapeRenderer sr) {
        if (sr == null) return;
        if (getTexture() != null) return; // sprite handles rendering
        sr.setColor(getShapeColor());
        drawFallingShape(sr);
    }

    @Override
    public void drawSprite(SpriteBatch batch) {
        if (batch == null) return;
        Texture texture = getTexture();
        if (texture == null) return;
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());
    }

    public boolean isOffScreen() {
        return getY() + getHeight() < 0f;
    }

    public ObjectType getType() { return type; }

    private Texture getTexture() {
        if (texturePath == null || texturePath.trim().isEmpty()) return null;
        Texture cached = TEXTURE_CACHE.get(texturePath);
        if (cached != null) return cached;

        if (!Gdx.files.internal(texturePath).exists()) {
            System.out.println("[FallingObject] Missing texture: " + texturePath);
            return null;
        }

        Texture loaded = new Texture(Gdx.files.internal(texturePath));
        TEXTURE_CACHE.put(texturePath, loaded);
        return loaded;
    }

    public static void disposeSharedTextures() {
        for (Texture texture : TEXTURE_CACHE.values()) {
            if (texture != null) texture.dispose();
        }
        TEXTURE_CACHE.clear();
    }
}