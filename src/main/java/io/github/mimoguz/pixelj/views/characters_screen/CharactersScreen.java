package io.github.mimoguz.pixelj.views.characters_screen;

import io.github.mimoguz.pixelj.models.Metrics;
import io.github.mimoguz.pixelj.models.Project;
import io.github.mimoguz.pixelj.util.Detachable;
import io.github.mimoguz.pixelj.views.controls.GlyphView;
import io.github.mimoguz.pixelj.views.shared.Dimensions;

import javax.swing.*;

public class CharactersScreen extends JSplitPane implements Detachable {
    private final ListPanel listPanel;
    private final PainterPanel painterPanel;
    private final ListSelectionModel selectionModel;

    public CharactersScreen(final Project project, final JComponent root) {
        selectionModel = new DefaultListSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        listPanel = new ListPanel(project, selectionModel, project.getMetrics(), root);

        painterPanel = new PainterPanel(project, root);
        painterPanel.setMetrics(project.getMetrics());

        // Connect the listModel to the painter
        selectionModel.addListSelectionListener(e -> {
            if (
                    selectionModel.getMinSelectionIndex() == selectionModel.getMaxSelectionIndex()
                            && selectionModel.getMinSelectionIndex() >= 0
            ) {
                painterPanel.setModel(listPanel.getListModel().getElementAt(selectionModel.getMinSelectionIndex()));
            } else {
                painterPanel.setModel(null);
            }
        });

        // Connect the painter to the listModel
        painterPanel.getPainter().addChangeListener((sender, event) -> {
            if (event == GlyphView.ViewChangeEvent.GLYPH_MODIFIED) {
                final var index = selectionModel.getMinSelectionIndex();
                if (index >= 0 && painterPanel.getModel() == listPanel.getListModel().getElementAt(index)) {
                    listPanel.getListModel().requestEvent(index);
                }
            }
        });

        setMaximumSize(Dimensions.MAXIMUM);
        setLeftComponent(painterPanel);
        setRightComponent(listPanel);
        setResizeWeight(1.0);
    }

    @Override
    public void detach() {
        painterPanel.detach();
        listPanel.detach();
    }

    @Override
    public void setEnabled(final boolean value) {
        listPanel.setEnabled(value);
        painterPanel.setEnabled(value);
        super.setEnabled(value);
    }

    public void updateMetrics(final Metrics metrics) {
        painterPanel.setMetrics(metrics);
        listPanel.getActions().updateMetrics(metrics);
    }
}
