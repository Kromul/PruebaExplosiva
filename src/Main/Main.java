package Main;

import CreacionMapas.ProjectExplosion;
import javax.swing.JFrame;

/*
 * @author papa
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        ProjectExplosion x;
        try {
            x = new ProjectExplosion();
            x.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            x.setTitle("Project Explosion");
            x.setSize(800, 600);
            x.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
