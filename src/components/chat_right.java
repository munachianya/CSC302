
package components;

import java.awt.Color;

public class chat_right extends javax.swing.JLayeredPane {

    public chat_right() {
        initComponents();
        chat_items.setOpaque(false);
        chat_items.setBackground(new Color(153,153,234));
    }

  public void setText(String text, String time){
      chat_items.setText(text);
      chat_items.setTime(time);
  }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        chat_items = new components.chat_items();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));
        add(chat_items);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private components.chat_items chat_items;
    // End of variables declaration//GEN-END:variables
}
