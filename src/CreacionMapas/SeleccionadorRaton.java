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

public class SeleccionadorRaton extends PickMouseBehavior {

    BranchGroup conjunto;
    BigMind juego;
    
    //Atributos
    public SeleccionadorRaton(Canvas3D canvas, BranchGroup bg, BigMind juego) {
        super(canvas, bg, new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        pickCanvas.setMode(PickTool.GEOMETRY_INTERSECT_INFO);
        this.juego = juego;
        conjunto = bg;
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
//                HebraCreadora creadora = new HebraCreadora(70, 0.9f, conjunto, juego.listaObjetosFisicos, false, this, juego.mundoFisico);
                juego.creadora.start();
            }
        }
    }

    class HebraCreadora extends Thread {

        final BranchGroup conjunto;
        final ArrayList<CreacionMapas.Figura> listaObjetosFisicos;
        DiscreteDynamicsWorld mundoFisico = null;
        final BigMind juego;
        int maxEsferas;
        boolean mdl;
        float radio;

        public HebraCreadora(int maxEsferas, float radio, BranchGroup conjunto, ArrayList<CreacionMapas.Figura> listaObjetosFisicos, boolean mdl, BigMind j, DiscreteDynamicsWorld mundoFisico) {
            this.conjunto = conjunto;
            this.listaObjetosFisicos = listaObjetosFisicos;
            this.mundoFisico = mundoFisico;
            this.juego = j;
            this.maxEsferas = maxEsferas;
            this.mdl = mdl;
            this.radio = radio;
        }

        public void run() {
            int numEsferas = 0;
            try {
                Thread.sleep(10);
            } catch (Exception e) {
            }
//            while (numEsferas <= maxEsferas) {
            float elasticidad = 0.5f;
            float dumpingLineal = 0.5f;
            float masa = 5;
//                for (float x = 3; x >= -3; x = x - 2f) {
//                    numEsferas++;
            Figura fig;
            fig = new figuras.EsferaMDL("src/resources/objetosOBJ/ataques/war_axe.obj", radio, conjunto, listaObjetosFisicos, juego);
            if (!juego.actualizandoFisicas) {
                fig.crearPropiedades(masa, elasticidad, dumpingLineal, 4, 6, 11, mundoFisico);
            }
        }
    }
}