package org.terpg.workdir;

public record Position(int x, int y) {
    public Position move(int dx, int dy) {
        return new Position(x + dx, y + dy);
    }
}
