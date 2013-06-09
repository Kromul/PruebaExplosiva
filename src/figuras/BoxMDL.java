package figuras;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.BoxShape;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.image.TextureLoader;
import java.net.URL;
import java.util.ArrayList;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import net.sf.nwn.loader.AnimationBehavior;
import net.sf.nwn.loader.NWNLoader;
import CreacionMapas.Figura;
import CreacionMapas.ProjectExplosion;
import javax.media.j3d.Node;
import utilidades.CapabilitiesMDL;

/**
 * @author Pedro Reyes
 */
public class BoxMDL extends Figura {

    public Scene escenaPersonaje1;
    public AnimationBehavior ab = null;
    public String nombreAnimacionCorriendo, nombreAnimacionCaminando, nombreAnimacionQuieto, nombreAnimacionLuchando;
    Vector3d direccion = new Vector3d(0, 0, 10);
    public float alturaP, alturaDeOjos;
    public float ancho, alto, largo;

    public BoxMDL(String ficheroMDL, float ancho, float alto, float largo, BranchGroup conjunto, ArrayList<Figura> listaObjetos, ProjectExplosion juego, String textura, boolean mostrarMDL) {
        super(conjunto, listaObjetos, juego);
        esMDL = true;
        //Variables del objeto no visualizado
        this.ancho = ancho;
        this.alto = alto;
        this.largo = largo;
        //Creando una apariencia
        Appearance apariencia = new Appearance();
        Texture tex = new TextureLoader(System.getProperty("user.dir") + "/" + textura, juego).getTexture();
        apariencia.setTexture(tex);
        TextureAttributes texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.MODULATE);
        apariencia.setTextureAttributes(texAttr);

        //Creacion de la forma visual MDL
        //nombre = "figura_MDL_" + identificador;
        BoxShape figuraFisica = new BoxShape(new Vector3f(ancho, alto, largo));
        ramaFisica = new CollisionObject();
        ramaFisica.setCollisionShape(figuraFisica);
        ramaVisible.addChild(desplazamientoFigura);
        Node figuraVisual;
        if (!mostrarMDL) {
            figuraVisual = new Box(ancho, alto, largo, apariencia);
        } else {
            figuraVisual = crearObjetoMDL(ficheroMDL, ancho * 2);
        }
        desplazamientoFigura.addChild(figuraVisual);
    }

    TransformGroup crearObjetoMDL(String archivo, float multiplicadorEscala) {
        BranchGroup RamaMDL = new BranchGroup();
        float rotacionX = 0;
        float rotacionY = 0;
        float rotacionZ = 0;
        float escalaTamano = 1f;
        float desplazamientoY = 0;
        try {
            NWNLoader nwn2 = new NWNLoader();
            nwn2.enableModelCache(true);
            escenaPersonaje1 = nwn2.load(new URL("file://localhost/" + System.getProperty("user.dir") + "/" + archivo));
            RamaMDL = escenaPersonaje1.getSceneGroup();
            //Recorrido por los- objetos para darle capacidades a sus Shapes3D
            CapabilitiesMDL.setCapabilities(RamaMDL, this.identificadorFigura);
            //Para cada Objeto MDL dar nombre las animaciones de la figura. Dar rotaciones a la figuraMDL (suelen venir giradas)
            ab = (AnimationBehavior) escenaPersonaje1.getNamedObjects().get("AnimationBehavior");
            if (archivo.equals("objetosMDL/Iron_Golem.mdl")) {
                nombreAnimacionCorriendo = "iron_golem:cwalk";
                nombreAnimacionCaminando = "iron_golem:cwalk";
                nombreAnimacionQuieto = "iron_golem:cpause1";
                nombreAnimacionLuchando = "iron_golem:cwalk";//"iron_golem:ca1slashl";
                rotacionX = -1.5f;
                rotacionZ = 3.14f;
                escalaTamano = 0.65f;
                desplazamientoY = -1f;
                alturaP = (float) 3f * escalaTamano;
                alturaDeOjos = alturaP;
            }
            if (archivo.equals("objetosMDL/Doomknight.mdl")) {
                nombreAnimacionCaminando = "Doomknight:crun";
                nombreAnimacionCaminando = "Doomknight:cwalk";
                nombreAnimacionQuieto = "Doomknight:cpause1";
                rotacionX = -1.5f;
                rotacionZ = 3.14f;
                alturaP = 2f;
                escalaTamano = 0.8f;
                alturaDeOjos = 1.5f * escalaTamano;
                desplazamientoY = -1f;
            }
            if (archivo.equals("objetosMDL/Dire_Cat.mdl")) {
                nombreAnimacionCaminando = "dire_cat:crun";
                nombreAnimacionCaminando = "dire_cat:cwalk";
                nombreAnimacionQuieto = "dire_cat:cpause1";
                rotacionX = -1.5f;
                rotacionZ = 3.14f;
                alturaP = 2f;
                escalaTamano = 1f;
                alturaDeOjos = alturaP * escalaTamano;
            }
            if (archivo.equals("objetosMDL/pixie.mdl")) {
                nombreAnimacionCaminando = "pixie:crun";
                nombreAnimacionCaminando = "pixie:cwalk";
                nombreAnimacionQuieto = "pixie:cpause1";
                rotacionX = -1.5f;
                rotacionZ = 3.14f;
                alturaP = 1f;
                escalaTamano = 6.0f;
                desplazamientoY = -6.5f;
                alturaDeOjos = alturaP * escalaTamano;
            }
        } catch (Exception exc) {
            exc.printStackTrace();
            System.out.println("Error during load Dire_Cat.mdl");
        }

        //Ajustando rotacion inicial de la figura MLD y aplicando tamano
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
