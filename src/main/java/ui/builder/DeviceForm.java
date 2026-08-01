package ui.builder;

import javax.swing.*;
import java.awt.*;

/** Small form for entering a device's id, label and (optionally) type. */
public class DeviceForm extends JPanel {
    private final JTextField idField = new JTextField(8);
    private final JTextField labelField = new JTextField(10);
    private final JComboBox<String> typeBox =
        new JComboBox<>(new String[]{"Server", "Workstation", "Router", "Firewall", "IoT Device"});

    public DeviceForm() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(new JLabel("ID:"));
        add(idField);
        add(new JLabel("Label:"));
        add(labelField);
        add(new JLabel("Type:"));
        add(typeBox);
    }

    public String getDeviceId() {
         return idField.getText().trim(); 
    }
    public String getDeviceLabel() {
         return labelField.getText().trim(); 
    }
    public String getDeviceType() {
         return (String) typeBox.getSelectedItem();
    }

    public boolean isInputValid() {
    return !getDeviceId().isEmpty();
    }

    public void clear() {
        idField.setText("");
        labelField.setText("");
        typeBox.setSelectedIndex(0);
    }
}