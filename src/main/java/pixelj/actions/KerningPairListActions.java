package pixelj.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;

import pixelj.models.Project;
import pixelj.resources.Resources;
import pixelj.views.projectwindow.kerningpairspage.AddDialog;

public class KerningPairListActions {
    /**
     * Show add kerning pair dialog.
     */
    public final ApplicationAction showAddDialogAction;
    /**
     * Show remove kerning pairs dialog.
     */
    public final ApplicationAction showRemoveDialogAction;
    /**
     * Collection of all actions.
     */
    public final Collection<ApplicationAction> all;

    private final AddDialog addDialog;
    private final Project project;
    private final JFrame window;
    private final ListSelectionModel selectionModel;

    public KerningPairListActions(
            final Project project,
            final ListSelectionModel selectionModel,
            final JFrame window
    ) {

        this.project = project;
        this.selectionModel = selectionModel;
        this.window = window;

        addDialog = new AddDialog(project.getGlyphs(), window);

        showAddDialogAction = new ApplicationAction("addKerningPairsAction", this::showAddDialog).withText()
                .setAccelerator(KeyEvent.VK_PLUS, InputEvent.ALT_DOWN_MASK);

        showRemoveDialogAction = new ApplicationAction("removeKerningPairsAction", this::showRemoveDialog)
                .withText()
                .setAccelerator(KeyEvent.VK_MINUS, InputEvent.ALT_DOWN_MASK);

        selectionModel.addListSelectionListener(
                e -> showRemoveDialogAction.setEnabled(selectionModel.getMinSelectionIndex() >= 0)
        );

        all = List.of(showAddDialogAction, showRemoveDialogAction);
    }

    private void showAddDialog(final ActionEvent event, final Action action) {
        addDialog.setVisible(true);
        final var result = addDialog.getResult();
        if (result != null) {
            project.getKerningPairs().add(result);
            project.setDirty(true);
        }
    }

    private void showRemoveDialog(final ActionEvent event, final Action action) {
        final var indices = selectionModel.getSelectedIndices();
        if (indices.length == 0) {
            return;
        }
        final var res = Resources.get();
        final var message = res.formatString("removingKerningPairsMessage", indices.length);
        final var result = JOptionPane.showConfirmDialog(
                window,
                message,
                res.getString("nonUndoable"),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (result != JOptionPane.OK_OPTION) {
            return;
        }
        final var listModel = project.getKerningPairs();
        listModel.removeAll(Arrays.stream(indices).mapToObj(listModel::getElementAt).toList());
        project.setDirty(true);
    }
}
