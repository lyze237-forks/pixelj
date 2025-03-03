package pixelj.resources;

import java.awt.Color;

public final class DarkColors implements Colors {
    private final Color accent = new Color(41, 121, 255);
    private final Color active = new Color(229, 115, 26);
    private final Color disabledIcon = new Color(68, 75, 89);
    private final Color divider = new Color(53, 58, 68);
    private final Color faintIcon = new Color(106, 112, 122);
    private final Color icon = new Color(151, 159, 173);

    @Override
    public Color accent() {
        return accent;
    }

    @Override
    public Color active() {
        return active;
    }

    @Override
    public Color disabledIcon() {
        return disabledIcon;
    }

    @Override
    public Color divider() {
        return divider;
    }

    @Override
    public Color icon() {
        return icon;
    }

    @Override
    public Color inactive() {
        return faintIcon;
    }

}
