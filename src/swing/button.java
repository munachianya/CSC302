/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package swing;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 *
 * @author munachianya
 */
public class button extends JButton {

    /**
     * @return the icon1
     */
    public Icon getIcon1() {
        return icon1;
    }

    /**
     * @param icon1 the icon1 to set
     */
    public void setIcon1(Icon icon1) {
        this.icon1 = icon1;
    }

    /**
     * @return the icon2
     */
    public Icon getIcon2() {
        return icon2;
    }

    /**
     * @param icon2 the icon2 to set
     */
    public void setIcon2(Icon icon2) {
        this.icon2 = icon2;
    }

    public button(Icon icon1, Icon icon2) {
        this.icon1 = icon1;
        this.icon2 = icon2;
    }
    private Icon icon1;
    private Icon icon2;

    public button() {
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR) {
        });
    }

    @Override
    public void setSelected(boolean bn) {
        super.setSelected(bn); 
        if(bn){
            setIcon(icon1);
        }else{
            setIcon(icon2);
        }
    }
    
    
}
