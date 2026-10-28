import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import fr.sejourfacile.view.FenetreConnexion;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        FlatLightLaf.setup();

        UIManager.put("Button.arc", 12);
        UIManager.put("Component.arc", 10);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("ProgressBar.arc", 10);
        UIManager.put("TabbedPane.showTabSeparators", true);
        UIManager.put("TabbedPane.selectedBackground", Color.WHITE);
        UIManager.put("Table.alternateRowColor", new Color(248, 250, 252));
        UIManager.put("Table.rowHeight", 34);
        UIManager.put("ScrollBar.thumbArc", 999);

        SwingUtilities.invokeLater(() -> {
            new FenetreConnexion().setVisible(true);
        });
    }
}
