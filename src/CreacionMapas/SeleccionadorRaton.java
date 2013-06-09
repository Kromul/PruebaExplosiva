package CreacionMapas;

/**
 * @author Alejandro Ruiz
 */
import javax.media.j3d.*;
import com.sun.j3d.utils.picking.behaviors.*;
import com.sun.j3d.utils.picking.*;
import javax.vecmath.Point3d;

public class SeleccionadorRaton extends PickMouseBehavior {

    //Atributos


    public SeleccionadorRaton(Canvas3D canvas, BranchGroup bg) {
        super(canvas, bg, new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        pickCanvas.setMode(PickTool.GEOMETRY_INTERSECT_INFO);  
    }

    @Override
    public void updateScene(int xpos, int ypos) {
        pickCanvas.setShapeLocation(xpos, ypos);
        PickResult pickResult = pickCanvas.pickClosest();
        if (pickResult != null) {
            Node nd = pickResult.getObject();
            String nombre = nd.getName();
            if (nombre != null) {
                System.out.println("Objeto seleccionado:" + nombre);
            }
        }
    }
}