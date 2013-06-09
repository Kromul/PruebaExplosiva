package figuras;

import CreacionMapas.Figura;
import com.sun.j3d.utils.geometry.Box;
import java.util.Enumeration;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnCollisionEntry;
import javax.media.j3d.WakeupOnCollisionExit;
import javax.media.j3d.WakeupOr;
import javax.vecmath.Point3d;

/**
 * @author Alejandro Ruiz Moyano
 */
public class DeteccionColisionFigura extends Behavior {

    Figura figura;
    protected Box objetoReferencia;
    protected WakeupCriterion[] Criterios;
    protected WakeupOr CriterioUnificador;

    public DeteccionColisionFigura(Figura fig) {
        this.figura = fig;
        //Hay que añadir el objeto referencia por el que encontraremos colisiones
        //objetoReferencia = personaje.hitBox;
        setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
    }

    public void initialize() {
        Criterios = new WakeupCriterion[2];
        WakeupOnCollisionEntry chocaFigura = new WakeupOnCollisionEntry(objetoReferencia, WakeupOnCollisionEntry.USE_GEOMETRY);
        WakeupOnCollisionExit saleFigura = new WakeupOnCollisionExit(objetoReferencia, WakeupOnCollisionEntry.USE_GEOMETRY);

        Criterios[0] = chocaFigura;
        Criterios[1] = saleFigura;

        CriterioUnificador = new WakeupOr(Criterios);
        wakeupOn(CriterioUnificador);
    }

    public void processStimulus(Enumeration criteria) {
        while (criteria.hasMoreElements()) {
            WakeupCriterion theCriterion = (WakeupCriterion) criteria.nextElement();
            if (theCriterion instanceof WakeupOnCollisionEntry) {
                String theLeaf = ((WakeupOnCollisionEntry) theCriterion).getTriggeringPath().getObject().getParent().getName();
                if (theLeaf != null) {
                    //Código en el que se comprueba la entrada de la colisión y se realiza la acción necesaria
                } else if (theCriterion instanceof WakeupOnCollisionExit) {
                    //Código en el que se comprueba la salida de la colisión y se realiza la acción necesaria
                }
                wakeupOn(CriterioUnificador);
            }
        }
    }
}