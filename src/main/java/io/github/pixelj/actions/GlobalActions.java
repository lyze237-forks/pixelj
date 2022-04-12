package io.github.pixelj.actions;

import io.github.pixelj.resources.Icons;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.List;

public class GlobalActions {
    public static final ApplicationAction exportAction = new ApplicationAction(
            "export",
            (e) -> System.out.println("Export action"),
            "exportAction",
            null,
            Icons.FILE_EXPORT,
            null,
            null,
            KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK)
    );

    public static final ApplicationAction newProjectAction = new ApplicationAction(
            "newProject",
            (e) -> System.out.println("New project action"),
            "newProjectAction",
            null,
            Icons.FILE_NEW,
            null,
            null,
            KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK)
    );

    public static final ApplicationAction openProjectAction = new ApplicationAction(
            "openProject",
            (e) -> System.out.println("Open project action"),
            "openProjectAction",
            null,
            Icons.FILE_NEW,
            null,
            null,
            KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK)
    );

    public static final ApplicationAction quitAction = new ApplicationAction(
            "quit",
            (e) -> System.exit(0),
            "quitAction",
            null,
            Icons.FILE_NEW,
            null,
            null,
            KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK)
    );

    public static final ApplicationAction saveAction = new ApplicationAction(
            "saveProject",
            (e) -> System.out.println("Save action"),
            "saveAction",
            null,
            Icons.FILE_NEW,
            null,
            null,
            KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK)
    );

    public static final ApplicationAction saveAsAction = new ApplicationAction(
            "saveAs",
            (e) -> System.out.println("Save as action"),
            "saveAsAction",
            null,
            Icons.FILE_NEW,
            null,
            null,
            KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK)
    );

    public static final ApplicationAction showHelpAction = new ApplicationAction(
            "showHelp",
            (e) -> System.out.println("Show help action"),
            "showHelpAction",
            null,
            Icons.FILE_NEW,
            null,
            null,
            KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0)
    );

    public static final ApplicationAction showMetricsAction = new ApplicationAction(
            "showMetrics",
            (e) -> System.out.println("Show metrics action"),
            "showMetricsAction",
            null,
            Icons.FILE_NEW,
            null,
            null,
            KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK)
    );

    public static final ApplicationAction showSettingsAction = new ApplicationAction(
            "showSettings",
            (e) -> System.out.println("Show settings action"),
            "showSettingsAction",
            null,
            Icons.FILE_NEW,
            null,
            null,
            KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, InputEvent.CTRL_DOWN_MASK)
    );

    public static final Collection<ApplicationAction> all = List.of(
            exportAction,
            newProjectAction,
            openProjectAction,
            quitAction,
            saveAction,
            saveAsAction,
            showMetricsAction,
            showSettingsAction,
            showHelpAction
    );
}
