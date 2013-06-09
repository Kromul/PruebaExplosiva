package CreacionMapas;

import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import java.util.ArrayList;
import javax.media.j3d.BranchGroup;

/**
 * @author Alejandro Ruiz
 */
public class HebraCreadora extends Thread {

    final BranchGroup conjunto;
    final ArrayList<CreacionMapas.Figura> listaObjetosFisicos;
    DiscreteDynamicsWorld mundoFisico = null;
    final ProjectExplosion juego;
    int maxEsferas;
    boolean mdl;
    float radio;
    Figura fig;

    public HebraCreadora(int maxEsferas, float radio, BranchGroup conjunto, ArrayList<CreacionMapas.Figura> listaObjetosFisicos, boolean mdl, ProjectExplosion j, DiscreteDynamicsWorld mundoFisico) {
        this.conjunto = conjunto;
        this.listaObjetosFisicos = listaObjetosFisicos;
        this.mundoFisico = mundoFisico;
        this.juego = j;
        this.maxEsferas = maxEsferas;
        this.mdl = mdl;
        this.radio = radio;
        fig = new figuras.EsferaMDL("src/resources/objetosOBJ/ataques/war_axe.obj", radio, conjunto, listaObjetosFisicos, juego);
        fig.posiciones[1] = 100;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(10);
        } catch (Exception e) {
        }
        float elasticidad = 0.5f;
        float dumpingLineal = 0.5f;
        float masa = 5;
        if (!juego.actualizandoFisicas) {
            fig.crearPropiedades(masa, elasticidad, dumpingLineal, 4, 5, 11, mundoFisico);
        }
    }

    public Figura getFigura() {
        return fig;
    }

    public float getRadio() {
        return radio;
    }
}
