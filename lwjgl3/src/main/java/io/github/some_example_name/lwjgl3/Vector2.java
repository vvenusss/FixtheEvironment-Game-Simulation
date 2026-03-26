package io.github.some_example_name.lwjgl3;


public class Vector2 {

    public float x;
    public float y;

    public Vector2() {
        this.x = 0;
        this.y = 0;
    }

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(Vector2 other) {
        this.x = other.x;
        this.y = other.y;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(Vector2 other) {
        this.x = other.x;
        this.y = other.y;
    }

    public void add(Vector2 other) {
        this.x += other.x;
        this.y += other.y;
    }

    public void subtract(Vector2 other) {
        this.x -= other.x;
        this.y -= other.y;
    }


    public Vector2 multiply(float scalar) {
        return new Vector2(this.x * scalar, this.y * scalar);
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public Vector2 normalise() {
        float len = length();
        if (len == 0) return new Vector2(0, 0);
        return new Vector2(x / len, y / len);
    }

    public float dot(Vector2 other) {
        return this.x * other.x + this.y * other.y;
    }

    public float distance(Vector2 other) {
        float dx = this.x - other.x;
        float dy = this.y - other.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public String toString() {
        return "Vector2(" + x + ", " + y + ")";
    }
}
