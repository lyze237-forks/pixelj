package pixelj.util.packer;

import java.awt.Dimension;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

// TODO: Write tests
public final class GridPacker implements Packer {
    @Override
    public List<List<Rectangle>> pack(final List<Rectangle> input, final int boxWith, final int boxHeight)
            throws IllegalStateException {
        if (input == null) {
            throw new IllegalStateException("GridPacker input is not set yet");
        }
        final var cellSz = cellSize(input);
        final var columnsPerBox = boxWith / cellSz.width;
        final var rowsPerBox = boxHeight / cellSz.height;
        final var rectanglesPerBox = columnsPerBox * rowsPerBox;
        final var boxCount = Math.ceilDiv(input.size(), rectanglesPerBox);
        final var result = IntStream.range(0, boxCount)
                .mapToObj(
                        box -> input.subList(
                                box * rectanglesPerBox,
                                Math.min((box + 1) * rectanglesPerBox, input.size())
                        )
                )
                .parallel()
                .map(segment -> {
                    arrange(segment, rowsPerBox, columnsPerBox, cellSz);
                    return segment;
                })
                .toList();
        return result;
    }

    private Dimension cellSize(final Collection<Rectangle> rectangles) {
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

    private void arrange(
            final List<Rectangle> input,
            final int rowCount,
            final int columnCount,
            final Dimension cellSize
    ) {
        for (var row = 0; row < rowCount; row++) {
            for (var column = 0; column < columnCount; column++) {
                final var index = row * columnCount + column;
                if (index >= input.size()) {
                    return;
                }
                input.get(index).moveTo(column * cellSize.width, row * cellSize.height);
            }
        }
    }
}
