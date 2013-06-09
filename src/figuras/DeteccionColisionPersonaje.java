package figuras;

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
 * @author Alejandro Ruiz
 */
public class DeteccionColisionPersonaje extends Behavior {

    Personaje personaje;
    protected Box objetoReferencia;
    protected WakeupCriterion[] Criterios;
    protected WakeupOr CriterioUnificador;

    public DeteccionColisionPersonaje(Personaje personaje) {
        this.personaje = personaje;
        objetoReferencia = personaje.hitBox;
        setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
    }

    @Override
    public void initialize() {
        Criterios = new WakeupCriterion[2];
        WakeupOnCollisionEntry chocaPersonaje = new WakeupOnCollisionEntry(objetoReferencia, WakeupOnCollisionEntry.USE_GEOMETRY);
        WakeupOnCollisionExit salePersonaje = new WakeupOnCollisionExit(objetoReferencia, WakeupOnCollisionEntry.USE_GEOMETRY);

        Criterios[0] = chocaPersonaje;
        Criterios[1] = salePersonaje;

        CriterioUnificador = new WakeupOr(Criterios);
        wakeupOn(CriterioUnificador);
    }

    @Override
    public void processStimulus(Enumeration criteria) {
        while (criteria.hasMoreElements()) {
            WakeupCriterion theCriterion = (WakeupCriterion) criteria.nextElement();
            if (theCriterion instanceof WakeupOnCollisionEntry) {
                String theLeaf = ((WakeupOnCollisionEntry) theCriterion).getTriggeringPath().getObject().getName();
                if (theLeaf != null) {
                    if (theLeaf.equalsIgnoreCase("esfera")) {
                        this.personaje.impactoEsfera = true;
                    }
                }
            } else if (theCriterion instanceof WakeupOnCollisionExit) {
                //Código en el que se comprueba la salida de la colisión y se realiza la acción necesaria 
            }
            wakeupOn(CriterioUnificador);
        }
    }
}
