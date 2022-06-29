package pixelj.util.packer;

import java.awt.Dimension;

// TODO: Write tests
public class GridPacker extends AbstractPacker {

    @Override
    public Dimension packInPlace(final boolean forceSquare, final boolean forcePowerOfTwo)
            throws IllegalStateException {
        if (rectangles == null) {
            throw new IllegalStateException("GridPacker input is not set yet");
        }
        final var cellSz = cellSize();
        final var imageSz = boxSize(cellSz);
        var col = 0;
        var row = 0;
        for (var rect : rectangles) {
            if (col * cellSz.width >= imageSz.width) {
                row += 1;
                col = 0;
            }
            rect.moveTo(cellSz.width * col, cellSz.height * row);
            col += 1;
        }
        return fixedSize(imageSz, forceSquare, forcePowerOfTwo);
    }

    private Dimension cellSize() {
        final var result = new Dimension(0, 0);
        for (var rect : rectangles) {
            if (rect.width() > result.width) {
                result.width = rect.width();
            }
            if (rect.height() > result.height) {
                result.height = rect.height();
            }
        }
        return result;
    }

    // TODO: This is a terrible algorithm.
    private Dimension boxSize(final Dimension cellSize) {
        final var result = new Dimension(cellSize.width * rectangles.size(), cellSize.height);
        var cols = rectangles.size();
        var rows = 1;
        while (true) {
            final var newCols = (cols + 1) / 2;
            final var newRows = rows * 2;
            final var width = newCols * cellSize.width;
            final var height = newRows * cellSize.height;
            if (newRows > 0 && distance(width, height) < distance(result.width, result.height)) {
                rows = newRows;
                cols = newCols;
                result.setSize(cols * cellSize.width, rows * cellSize.height);
            } else {
                break;
            }
        }
        return result;
    }

    private double distance(final int w, final int h) {
        return Math.abs(((double) w) / ((double) h) - 1.0);
    }
}
