package io.github.mimoguz.pixelj.actions;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.function.BiConsumer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import io.github.mimoguz.pixelj.resources.Icons;
import io.github.mimoguz.pixelj.resources.Resources;

public class ApplicationAction extends AbstractAction {
    private static final long serialVersionUID = 5365300941571995035L;

    private final transient BiConsumer<ActionEvent, Action> consumer;
    private final String key;

    /**
     * @param key      A unique key. Must not be a null.
     * @param consumer This will be called in the actionPerformed method. Its second
     *                 parameter is the action itself. Must not be a null.
     */
    public ApplicationAction(final String key, final BiConsumer<ActionEvent, Action> consumer) {
        this.key = key;
        this.consumer = consumer;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        consumer.accept(e, this);
    }

    public String getKey() {
        return key;
    }

    public ApplicationAction setAccelerator(int key, int mask) {
        return setAccelerator(KeyStroke.getKeyStroke(key, mask));
    }

    public ApplicationAction setAccelerator(final KeyStroke value) {
        putValue(Action.ACCELERATOR_KEY, value);
        return this;
    }

    public ApplicationAction setIcon(final Icons iconVariant, final Color color, final Color disabledColor) {
        final var res = Resources.get();
        final var icon = res.getIcon(
                iconVariant,
                color != null ? color : res.colors.icon(),
                disabledColor != null ? disabledColor : res.colors.disabledIcon()
        );
        putValue(Action.SMALL_ICON, icon);
        putValue(Action.LARGE_ICON_KEY, icon);
        return this;
    }

    public ApplicationAction setTextKey(final String value) {
        putValue(Action.NAME, Resources.get().getString(value));
        return this;
    }

    public ApplicationAction setTooltipKey(final String value) {
        putValue(Action.SHORT_DESCRIPTION, Resources.get().getString(value));
        return this;
    }
}
