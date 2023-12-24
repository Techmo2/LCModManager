package brad.tillmann;

import javax.swing.*;
import java.awt.*;

public class ModListCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        LethalCompanyModDescriptor modDescriptor = (LethalCompanyModDescriptor) value;

        setText(String.format("%s by %s", modDescriptor.getName(), modDescriptor.getOwner()));

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setOpaque(true);
        return this;
    }
}
