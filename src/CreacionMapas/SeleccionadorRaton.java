/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CreacionMapas;

import javax.media.j3d.*;
import com.sun.j3d.utils.picking.behaviors.*;
import com.sun.j3d.utils.picking.*;
import javax.vecmath.Point3d;

class SeleccionadorRaton extends PickMouseBehavior {

    //Atributos
    public boolean jugar;
    public boolean controles;
    public boolean salir;
    public boolean atras;

    public SeleccionadorRaton(Canvas3D canvas, BranchGroup bg) {
        super(canvas, bg, new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        pickCanvas.setMode(PickTool.GEOMETRY_INTERSECT_INFO);
        jugar = false;
        controles = false;
        salir = false;
        atras = false;
    }

    @Override
    public void updateScene(int xpos, int ypos) {
        pickCanvas.setShapeLocation(xpos, ypos);
        PickResult pickResult = pickCanvas.pickClosest();
        if (pickResult != null) {
            Node nd = pickResult.getObject();
            if (nd.getName() != null) {
                if (nd.getName().equals("jugar")) {
                    jugar = true;
                } else if (nd.getName().equals("controles")) {
                    controles = true;
                } else if (nd.getName().equals("salir")) {
                    salir = true;
                } else if (nd.getName().equals("atras")) {
                    atras = true;
                }
            }
        }
    }
}