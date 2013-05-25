package CreacionMapas;

import java.util.Enumeration;
import javax.media.j3d.Behavior;
import javax.media.j3d.WakeupOnElapsedFrames;

public class ComportamientoMostrar extends Behavior {

    WakeupOnElapsedFrames framewake = new WakeupOnElapsedFrames(0, true);
    BigMind juego;

    public ComportamientoMostrar(BigMind juego_) {
        juego = juego_;
    }

    public void initialize() {
        wakeupOn(framewake);
    }

    public void processStimulus(Enumeration criteria) {
        try {
            juego.mostrar();
        } catch (Exception e) {
        }
        wakeupOn(framewake);
    }
}