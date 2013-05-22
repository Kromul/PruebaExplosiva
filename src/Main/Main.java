/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import CreacionMapas.BigMind;
import javax.swing.JFrame;

/**
 *
 * @author papa
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        BigMind x;
        try {
            x = new BigMind();
            x.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            x.setTitle("Hola");
            x.setSize(800, 600);
            x.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
