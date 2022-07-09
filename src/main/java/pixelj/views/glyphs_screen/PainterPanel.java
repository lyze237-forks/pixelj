package pixelj.views.glyphs_screen;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import pixelj.actions.Actions;
import pixelj.actions.PainterActions;
import pixelj.graphics.Snapshot;
import pixelj.models.DocumentSettings;
import pixelj.models.Glyph;
import pixelj.models.Project;
import pixelj.util.Checkerboard;
import pixelj.views.controls.GlyphPainter;
import pixelj.views.controls.Line;
import pixelj.views.controls.Orientation;

public final class PainterPanel extends PainterPanelBase {
    private static final Color BASELINE = new Color(45, 147, 173);
    private static final Color CAP_HEIGHT = new Color(41, 191, 18);
    private static final int INITIAL_ZOOM = 12;
    private static final int MAX_UNDO = 64;
    private static final Color X_HEIGHT = CAP_HEIGHT;

    private final PainterActions actions = new PainterActions();
    private final ArrayList<Snapshot> undoBuffer = new ArrayList<>();
    private BufferedImage overlay;

    public PainterPanel(final Project project, final JComponent root) {
        super(project, root, new InfoPanel(project));

        painter.setZoom(INITIAL_ZOOM);
        painter.setOverlayVisible(true);
        painter.setLinesVisible(true);
        painter.setShaded(true);
        painter.setSnapshotConsumer(snapshot -> {
            undoBuffer.add(snapshot);
            if (undoBuffer.size() > MAX_UNDO) {
                undoBuffer.remove(0);
            }
        });

        final var zoomSlider = zoomStrip.getSlider();
        zoomSlider.addChangeListener(e -> {
            if (zoomSlider.getValueIsAdjusting()) {
                painter.setZoom(zoomSlider.getValue());
            }
        });
        zoomStrip.setEnabled(false);

        infoPanel.gridVisibleProperty.addChangeListener((source, isVisible) -> 
                painter.setOverlayVisible(isVisible)
        );

        infoPanel.guidesVisibleProperty.addChangeListener((source, isVisible) -> {
            painter.setLinesVisible(isVisible);
            painter.setShaded(isVisible);
        });

        actions.setPainter(painter);
        // TODO: Use snapshot consumer to track changes.
        painter.setSnapshotConsumer(actions.snapshotConsumer);
        Actions.registerShortcuts(actions.all, root);
        fillToolbar(toolBar, actions);
  
        setMetrics(project.getDocumentSettings());
        project.documentSettingsProperty.addChangeListener((source, settings) -> setMetrics(settings));
        setEnabled(false);
    }

    public Glyph getModel() {
        return painter.getModel();
    }

    /**
     * @param value May be null
     */
    public void setModel(final Glyph value) {
        painter.setModel(value);
        infoPanel.setModel(value);
        if (value != null) {
            actions.setEnabled(true);
            zoomStrip.setEnabled(true);
            final var gw = value.getImage().getWidth();
            final var gh = value.getImage().getHeight();
            if (overlay == null || overlay.getWidth() != gw || overlay.getHeight() != gh) {
                overlay = Checkerboard.create(value.getImage().getWidth(), value.getImage().getHeight());
            }
            painter.setOverlay(overlay);
        } else {
            actions.setEnabled(false);
            zoomStrip.setEnabled(false);
            painter.setOverlay(null);
        }
    }

    public GlyphPainter getPainter() {
        return painter;
    }

    @Override
    public void setEnabled(final boolean value) {
        super.setEnabled(value);
        actions.setEnabled(value && painter.getModel() != null);
    }

    /**
     * Create the guides.
     */
    private void setMetrics(final DocumentSettings settings) {
        painter.removeLines();
        if (settings == null) {
            return;
        }
        painter.setTop(settings.descender() + settings.ascender());
        final var capHeight = new Line(
                Orientation.HORIZONTAL,
                settings.canvasHeight() - settings.descender() - settings.capHeight(),
                CAP_HEIGHT
        );
        final var xHeight = new Line(
                Orientation.HORIZONTAL,
                settings.canvasHeight() - settings.descender() - settings.xHeight(),
                X_HEIGHT
        );
        final var baseLine = new Line(
                Orientation.HORIZONTAL,
                settings.canvasHeight() - settings.descender(),
                BASELINE
        );
        painter.addLines(capHeight, xHeight, baseLine);
    }

    private static void fillToolbar(final JToolBar toolBar, final PainterActions actions) {
        toolBar.add(actions.historyUndoAction);
        toolBar.add(actions.historyRedoAction);
        toolBar.addSeparator();
        toolBar.add(actions.clipboardCutAction);
        toolBar.add(actions.clipboardCopyAction);
        toolBar.add(actions.clipboardPasteAction);
        toolBar.add(actions.clipboardImportAction);
        toolBar.addSeparator();
        toolBar.add(actions.flipHorizontallyAction);
        toolBar.add(actions.flipVerticallyAction);
        toolBar.add(actions.rotateLeftAction);
        toolBar.add(actions.rotateRightAction);
        toolBar.addSeparator();
        toolBar.add(actions.moveLeftAction);
        toolBar.add(actions.moveRightAction);
        toolBar.add(actions.moveUpAction);
        toolBar.add(actions.moveDownAction);
        toolBar.addSeparator();
        toolBar.add(new JToggleButton(actions.symmetryToggleAction));
        toolBar.addSeparator();
        toolBar.add(actions.eraseAction);
    }
}
