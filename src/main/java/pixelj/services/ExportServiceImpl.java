package pixelj.services;

import java.awt.image.BufferedImage;
import java.awt.Dimension;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.imageio.ImageIO;

import pixelj.models.DocumentSettings;
import pixelj.models.KerningPair;
import pixelj.models.Project;
import pixelj.util.IOExceptionWrapper;
import pixelj.util.packer.Packer;
import pixelj.util.packer.Rectangle;

// TODO: How can I make the export method testable?
// TODO: Make this testable.
// TODO: Not finished yet.
// TODO: Space size?
public final class ExportServiceImpl implements ExportService {
    private final Packer<GlyphImageData> packer;
    private final ImageWriter imageWriter;

    public ExportServiceImpl(final Packer<GlyphImageData> packer, final ImageWriter imageWriter) {
        this.packer = packer;
        this.imageWriter = imageWriter; 
    }

    @Override
    public void export(
            final Project project, 
            final Path path, 
            final int textureWidth,
            final int textureHeight
    ) throws IOException {
        final var settings = project.getDocumentSettings();
        final var glyphs = project.getGlyphs();
        final var packedRectangles = pack(project, textureWidth, textureHeight);

        // Get images
        final var imageSize = new Dimension(textureWidth, textureHeight);
        final var images = IntStream.range(0, packedRectangles.size())
                .parallel()
                .mapToObj(index -> {
                    final var segment = packedRectangles.get(index);
                    return new PageImage(
                            index,
                            imageWriter.getImage(imageSize, segment, glyphs, settings)
                    );
                });

        // Get out path
        final var dir = path.getParent();
        final var dirStr = dir.toAbsolutePath().toString();
        final var baseName = extensionRemoved(path.getFileName().toString());

        // Save images
        try {
            images.forEach(img -> {
                try {
                    final var file = new File(Paths.get(dirStr, imageName(img.page, baseName)).toString());
                    ImageIO.write(img.image, "png", file);
                } catch (IOException e) {
                    throw new IOExceptionWrapper(e);
                }
            });
        } catch (IOExceptionWrapper e) {
            throw new IOException(e.getMessage(), e.getCause());
        }

        // Create and save fnt file
        try (var writer = new FileWriter(Paths.get(dirStr, baseName + "." + EXTENSION).toFile())) {
            fnt(project, baseName, packedRectangles, imageSize).forEach(block -> {
                try {
                    writer.write(block);
                    writer.write('\n');
                } catch (IOException e) {
                    throw new IOExceptionWrapper(e);
                }
            });
        } catch (IOExceptionWrapper e) {
            throw new IOException(e);
        }
    }

    private static String extensionRemoved(final String fileName) {
        final var dotPosition = fileName.lastIndexOf('.');
        return dotPosition > 0 ? fileName.substring(0, dotPosition) : fileName;
    }

    // TODO: Better rectangle generation
    private List<List<Rectangle<GlyphImageData>>> pack(
            final Project project,
            final int textureWidth, 
            final int textureHeight
    ) {
        final var settings = project.getDocumentSettings();
        final var glyphHeight = settings.ascender() + settings.descender();
        final var height = glyphHeight + 1;
        final var rectangles = project.getGlyphs().getElements().stream().map(glyph -> {
            final var glyphWidth = settings.isMonospaced() 
                    ? Math.min(glyph.getWidth(), settings.defaultWidth())
                    : glyph.getWidth();
            final var metadata = new GlyphImageData(glyphWidth, glyphHeight, glyphWidth, glyphHeight, 0, 0);
            return new Rectangle<>(glyph.getCodePoint(), glyphWidth + 1, height, metadata);
        }).toList();
        return packer.pack(rectangles, textureWidth, textureHeight);
    }

    private static Stream<String> fnt(
            final Project project, 
            final String baseName,
            final List<List<Rectangle<GlyphImageData>>> rectangles, 
            final Dimension imageSize
    ) {
        final var info = infoLine(project);
        final var common = commonLine(project, imageSize, rectangles.size());
        final var chars = charsLine(project);
        final var pages = pageStream(rectangles, baseName);
        final var characters = characterStream(rectangles, project.getDocumentSettings());
        final var kerningPairs = project.getKerningPairs().getElements().stream()
                .map(ExportServiceImpl::kerningPairLine);
        return Stream.of(
                Stream.of(info), 
                Stream.of(common), 
                pages, 
                Stream.of(chars), 
                characters,
                kerningPairs
        ).flatMap(a -> a);
    }

    private static String infoLine(final Project project) {
        final var title = project.getTitle();
        final var settings = project.getDocumentSettings();
        return new StringBuilder(120 + title.length())
                .append("info face=\"")
                .append(title)
                .append("\" size=")
                .append(-settings.capHeight())
                .append(" bold=")
                .append(settings.isBold() ? 1 : 0)
                .append(" italic=")
                .append(settings.isItalic() ? 1 : 0)
                .append(" unicode=1 stretchH=100 smooth=0 aa=1 padding=0,0,0,0 spacing=1,1 outline=0")
                .toString();
    }

    private static String commonLine(
            final Project project, 
            final Dimension imageSize,
            final int pageCount
    ) {
        final var settings = project.getDocumentSettings();
        return new StringBuilder(120)
                .append("common lineHeight=")
                .append(settings.ascender() + settings.descender() + settings.lineSpacing())
                .append(" base=")
                .append(settings.ascender())
                .append(" scaleW=")
                .append(imageSize.width)
                .append(" scaleH=")
                .append(imageSize.height)
                .append(" pages=")
                .append(pageCount)
                .append(" packed=0 alphaChnl=0 redChnl=4 greenChnl=4 blueChnl=4")
                .toString();
    }

    private static String pageLine(final int page, final String baseName) {
        return "page id=" + page + " file=\"" + imageName(page, baseName) + '"';
    }

    private static String charsLine(final Project project) {
        return "chars count=" + project.getGlyphs().countWhere(a -> true);
    }

    private static String characterLine(
            final DocumentSettings settings,
            final Rectangle<GlyphImageData> rect, 
            final int page
    ) {
        final var md = rect.getMetadata();
        return new StringBuilder(100)
                .append("char id=")
                .append(rect.getId())
                .append(" x=")
                .append(rect.getX())
                .append(" y=")
                .append(rect.getY())
                .append(" width=")
                .append(md.clipWidth())
                .append(" height=")
                .append(md.clipHeight())
                .append(" xoffset=")
                .append(md.xOffset())
                .append(" yoffset=")
                .append(md.yOffset())
                .append(" xadvance=")
                .append(md.glyphWidth() + settings.letterSpacing())
                .append(" page=")
                .append(page)
                .append(" chnl=15")
                .toString();
    }

    private static String kerningPairLine(final KerningPair pair) {
        return new StringBuilder(50)
                .append("kerning first=")
                .append(pair.getLeft().getCodePoint())
                .append(" second=")
                .append(pair.getRight().getCodePoint())
                .append(" amount=")
                .append(pair.getKerningValue())
                .toString();
    }

    private static Stream<String> pageStream(final List<List<Rectangle<GlyphImageData>>> rectangles,
            final String baseName) {
        final var pageCount = rectangles.size();
        return IntStream.range(0, pageCount).mapToObj(page -> pageLine(page, baseName));
    }

    private static Stream<String> characterStream(
            final List<List<Rectangle<GlyphImageData>>> rectangles,
            final DocumentSettings settings) {
        final var pageCount = rectangles.size();
        return IntStream.range(0, pageCount)
                .mapToObj(page -> 
                        rectangles.get(page)
                                .stream()
                                .map(rect -> characterLine(settings, rect, page))
                )
                .flatMap(a -> a);
    }

    private static String imageName(final int page, final String base) {
        return base + "_" + page + ".png";
    }

    private record PageImage(int page, BufferedImage image) {
    }
}
