package org.agh.utils;

import org.agh.model.Vector2d;

import java.util.Set;

public interface Fields {

    public boolean isPreferredField(Vector2d position);

    public Set<Vector2d> getPreferredFields();

    public Set<Vector2d> getRegularFields();

}
