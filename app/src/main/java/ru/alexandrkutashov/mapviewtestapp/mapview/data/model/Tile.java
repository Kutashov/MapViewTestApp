package ru.alexandrkutashov.mapviewtestapp.mapview.data.model;

import java.io.Serializable;

/**
 * Класс ячейки на карте
 *
 * @author Alexandr Kutashov
 *         on 01.04.2018
 */
public final class Tile implements Serializable {

    public final int x;
    public final int y;

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tile)) {
            return false;
        }

        Tile tile = (Tile) o;

        return x == tile.x && y == tile.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}
