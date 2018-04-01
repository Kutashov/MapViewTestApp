package ru.alexandrkutashov.mapviewtestapp.mapview;

/**
 * Класс ячейки на карте
 *
 * @author Alexandr Kutashov
 *         on 01.04.2018
 */
final class Tile {

    final int x;
    final int y;

    Tile(int x, int y) {
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
