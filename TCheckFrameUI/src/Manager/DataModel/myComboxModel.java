package Manager.DataModel;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

public class myComboxModel  extends DefaultCellEditor { 
    private AncestorListener ancestorListener;
    private PropertyChangeListener focusPropertyListener;

    public myComboxModel(JComboBox comboBox) {
        super(comboBox);
    }

    /** 
     * Overridden to install an appriate listener which opens the
     * popup when actually starting an edit.
     * 
     * @inherited <p>
     */
    @Override
    public Component getTableCellEditorComponent(JTable table,
            Object value, boolean isSelected, int row, int column) {
        super.getTableCellEditorComponent(table, value, isSelected, row, column);
        installListener(table);
        return getComponent();
    }

    /**
     * Shows popup.
     */
    protected void showPopup() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getComponent().setPopupVisible(true);
            }
        });
    }


    /**
     * Dynamically install self-uninstalling listener, depending on JComboBox
     * and JTable state. 
     * @param table
     */
    private void installListener(JTable table) {
        if (getComponent().isEditable() && table.getSurrendersFocusOnKeystroke()) {
            installKeyboardFocusListener();
        } else {
            installAncestorListener();
        }
    }

    private void installAncestorListener() {
        if (ancestorListener == null) {
            ancestorListener = new AncestorListener() {

                @Override
                public void ancestorAdded(AncestorEvent event) {
                    getComponent().removeAncestorListener(ancestorListener);
                    showPopup();
                }

                @Override
                public void ancestorRemoved(AncestorEvent event) {
                }

                @Override
                public void ancestorMoved(AncestorEvent event) {
                }

            };
        }
        getComponent().addAncestorListener(ancestorListener);
    }

    private void installKeyboardFocusListener() {
        if (focusPropertyListener == null) {
            focusPropertyListener = new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                 
                    if (focusManager().getPermanentFocusOwner() != 
                        getComponent().getEditor().getEditorComponent()) return;
                    focusManager()
                       .removePropertyChangeListener("permanentFocusOwner", focusPropertyListener);
                    showPopup();
                }

            };
        }
        focusManager().addPropertyChangeListener("permanentFocusOwner", focusPropertyListener);
    }

    /**
     * Convience for less typing.
     * @return
     */
    protected KeyboardFocusManager focusManager() {
        return KeyboardFocusManager.getCurrentKeyboardFocusManager();
    }

    /** 
     * Convenience for type cast.
     * @inherited <p>
     */
    @Override
    public JComboBox getComponent() {
        return (JComboBox) super.getComponent();
    }

}