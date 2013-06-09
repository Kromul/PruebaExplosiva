package figuras;

import CreacionMapas.Figura;
import CreacionMapas.BigMind;
import utilidades.CapabilitiesMDL;
import com.bulletphysics.collision.dispatch.*;
import com.bulletphysics.collision.shapes.*;
import com.sun.j3d.loaders.Scene;
import java.net.URL;
import java.util.ArrayList;
import javax.media.j3d.*;
import javax.vecmath.*;
import net.sf.nwn.loader.AnimationBehavior;
import net.sf.nwn.loader.NWNLoader;

/**
 * @author Alejandro Ruiz Moyano
 */
public class Personaje extends Figura {

    public Scene escenaPersonaje;
    public AnimationBehavior ab = null;
    public String animacionCorrer, animacionCaminar, animacionParar, animacionAtacar;
    Vector3d direccion = new Vector3d(0, 0, 10);
    public float radio, alturaP, alturaDeOjos;
    boolean esPersonaje;
    SphereShape figuraFisica;

    public Personaje(float radio, BranchGroup conjunto, ArrayList<Figura> listaObjetos, BigMind juego, boolean esPersonaje) {
        super(conjunto, listaObjetos, juego);
        esMDL = true;
        this.esPersonaje = esPersonaje;

        //Creacion de la forma visual MDL
        //nombre = "figura_MDL_" + identificador;
        TransformGroup figuraVisual = crearObjetoMDL(radio * 2);
        figuraFisica = new SphereShape(radio*4);
        ramaFisica = new CollisionObject();
        ramaFisica.setCollisionShape(figuraFisica);
        ramaVisible.addChild(desplazamientoFigura);
        desplazamientoFigura.addChild(figuraVisual);

        //Creacion del detector de teclas asociado
        if (esPersonaje) {
            DeteccionControlPersonaje mueve = new DeteccionControlPersonaje(this);
            mueve.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
            ramaVisible.addChild(mueve);
        }
    }

    private TransformGroup crearObjetoMDL(float multiplicadorEscala) {
        String archivo = "objetosMDL/Iron_Golem.mdl";
        BranchGroup RamaMDL = new BranchGroup();
        float rotacionX = 0;
        float rotacionY = 0;
        float rotacionZ = 0;
        float escalaTamano = 1f;
        float desplazamientoY = 0;
        try {
            NWNLoader nwn2 = new NWNLoader();
            nwn2.enableModelCache(true);
            escenaPersonaje = nwn2.load(new URL("file://localhost/" + System.getProperty("user.dir") + "/" + archivo));
            RamaMDL = escenaPersonaje.getSceneGroup();
            //Recorrido por los objetos para darle capacidades a sus Shapes3D
            CapabilitiesMDL.setCapabilities(RamaMDL, this.identificadorFigura);
            //Para cada Objeto MDL dar nombre las animaciones de la figura. Dar rotaciones a la figuraMDL (suelen venir giradas)
            ab = (AnimationBehavior) escenaPersonaje.getNamedObjects().get("AnimationBehavior");
            animacionCorrer = "iron_golem:crun";
            animacionCaminar = "iron_golem:cwalk";
            animacionParar = "iron_golem:cpause1";
            animacionAtacar = "iron_golem:ca1slashl";
            rotacionX = -1.5f;
            rotacionZ = 3.14f;
            escalaTamano = 0.65f;
            desplazamientoY = -1f;
            alturaP = (float) 3f * escalaTamano;
            alturaDeOjos = alturaP;
        } catch (Exception exc) {
        }

        //Ajustando rotacion inicial de la figura MLD y aplicando tama�o
        Transform3D rotacionCombinada = new Transform3D();
        rotacionCombinada.set(new Vector3f(0, desplazamientoY, 0));
        Transform3D correcionTemp = new Transform3D();
        correcionTemp.rotX(rotacionX);
        rotacionCombinada.mul(correcionTemp);
        correcionTemp.rotZ(rotacionZ);
        rotacionCombinada.mul(correcionTemp);
        correcionTemp.rotY(rotacionY);
        rotacionCombinada.mul(correcionTemp);
        correcionTemp.setScale(escalaTamano * multiplicadorEscala);
        rotacionCombinada.mul(correcionTemp);
        TransformGroup rotadorDeFIguraMDL = new TransformGroup(rotacionCombinada);
        rotadorDeFIguraMDL.addChild(RamaMDL);
        return rotadorDeFIguraMDL;
    }
}
