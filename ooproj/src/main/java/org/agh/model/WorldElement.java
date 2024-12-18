package org.agh.model;

abstract public class WorldElement {
    protected Vector2d position;

    public Vector2d getPosition() {
        return this.position;
    }

    public abstract String toString();
}
