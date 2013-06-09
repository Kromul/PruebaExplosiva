package CreacionMapas;

/**
 * @author Alejandro Ruiz
 */
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import javax.media.j3d.*;
import com.sun.j3d.utils.picking.behaviors.*;
import com.sun.j3d.utils.picking.*;
import java.util.ArrayList;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class SeleccionadorRaton extends PickMouseBehavior {

    BranchGroup conjunto;
    BigMind juego;
    boolean lanzado;

    //Atributos
    public SeleccionadorRaton(Canvas3D canvas, BranchGroup bg, BigMind juego) {
        super(canvas, bg, new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        pickCanvas.setMode(PickTool.GEOMETRY_INTERSECT_INFO);
        this.juego = juego;
        conjunto = bg;
        lanzado = false;
    }

    @Override
    public void updateScene(int xpos, int ypos) {
        pickCanvas.setShapeLocation(xpos, ypos);
        PickResult pickResult = pickCanvas.pickClosest();
        if (pickResult != null) {
            Node nd = pickResult.getObject();
            String nombre = nd.getName();
            if (nombre != null && !lanzado) {
                if (nombre.equalsIgnoreCase("Casa")) {
                    System.out.println("Objeto seleccionado:" + nombre);
//                HebraCreadora creadora = new HebraCreadora(70, 0.9f, juego.conjunto, juego.listaObjetosFisicos, false, this, juego.mundoFisico);
                    juego.creadora.run();
                    lanzado = true;
                    this.juego.eliminarCasa = true;
                }
            }
        }
    }
}
