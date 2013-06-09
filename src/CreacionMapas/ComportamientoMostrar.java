package CreacionMapas;

import java.util.Enumeration;
import javax.media.j3d.Behavior;
import javax.media.j3d.WakeupOnElapsedFrames;

/**
 * @author Alejandro Ruiz Moyano
 */
public class ComportamientoMostrar extends Behavior {

    WakeupOnElapsedFrames framewake = new WakeupOnElapsedFrames(0, true);
    ProjectExplosion juego;

    public ComportamientoMostrar(ProjectExplosion juego_) {
        juego = juego_;
    }

    @Override
    public void initialize() {
        wakeupOn(framewake);
    }

    @Override
    public void processStimulus(Enumeration criteria) {
        try {
            juego.mostrar();
        } catch (Exception e) {
        }
        wakeupOn(framewake);
    }
}