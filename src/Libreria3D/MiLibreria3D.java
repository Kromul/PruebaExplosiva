package Libreria3D;

import java.awt.Component;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BackgroundSound;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.LineArray;
import javax.media.j3d.MediaContainer;
import javax.media.j3d.Node;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TriangleArray;
import javax.media.j3d.TriangleStripArray;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.audioengines.javasound.JavaSoundMixer;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.geometry.Text2D;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.Font;
import javax.media.j3d.PolygonAttributes;

/**
 * @author Pedro Reyes
 * @author Alejandro Ruiz
 */
public class MiLibreria3D {

    public static enum tipoTrans {

        enX, enY, enZ
    };

    public static enum tipoFigura {

        rectangulo, esfera, cilindro, objetoOBJ
    };
    static tipoTrans transformacion;
    public static Color3f rojo = new Color3f(1.0f, 0.0f, 0.0f);
    public static Color3f verde = new Color3f(0.0f, 1.0f, 0.0f);
    public static Color3f azul = new Color3f(0.0f, 0.0f, 1.0f);
    public static Color3f amarillo = new Color3f(1.0f, 1.0f, 0.0f);
    public static Color3f cian = new Color3f(0.0f, 1.0f, 1.0f);
    public static Color3f violeta = new Color3f(1.0f, 0.0f, 1.0f);

    public static enum Direccion {

        adelante, atras, izquierda, derecha, adDer, adIzq, atDer, atIzq
    };

    /**
     * *******************
     * Metodos de Pedro ******************
     */
    /**
     * Rota el objeto indicado en el eje X,Y,Z que se indica
     *
     * @param objeto
     * @param grados
     * @param tipoRot
     * @return
     */
    public static TransformGroup rotarEstatico(Node objeto, float grados, tipoTrans tipoRot) {
        /* Transformacion a realizar sobre la esfera*/
        Transform3D rotacion = new Transform3D();
        //Datos de rotaci�n en X,Y,Z //Math.PI/4.0d); 
        rotacion.rotX(Math.toRadians(0));
        try {
            if (tipoRot.equals(tipoTrans.enX)) {
                rotacion.rotX(Math.toRadians(grados));
            }
            if (tipoRot.equals(tipoTrans.enY)) {
                rotacion.rotY(Math.toRadians(grados));
            }
            if (tipoRot.equals(tipoTrans.enZ)) {
                rotacion.rotZ(Math.toRadians(grados));
            }
        } catch (NullPointerException e) {
        }
        // Se asocia al objeto la transformacion
        TransformGroup objetoRotado = new TransformGroup(rotacion);
        objetoRotado.addChild(objeto);

        return objetoRotado;
    }

    /**
     * Rota el objeto indicado en el eje X,Y,Z que se indica
     *
     * @param objeto
     * @param rotX
     * @param rotY
     * @param rotZ
     * @return
     */
    public static TransformGroup trasladarEstatico(Node objeto, Vector3f posicion) {
        /* Transformacion a realizar sobre la esfera*/
        Transform3D traslacion = new Transform3D();
        //Datos de rotacion en X,Y,Z //Math.PI/4.0d); 
        traslacion.set(posicion);
        // Se asocia al objeto la transformacion
        TransformGroup objetoRotado = new TransformGroup(traslacion);
        objetoRotado.addChild(objeto);

        return objetoRotado;
    }

    /**
     * Dado un BranchGroup bajo el cual se encuentran los objetos que se quieren
     * escalar este metodo devolvera un BranchGroup el cual cubre a al BG
     * inicial y, evidentemente, a todos los objetos que este tenga por debajo
     * suya escalandolos el valor indicado
     *
     * @param objRoot
     * @return
     */
    public static BranchGroup escalarEstatico(BranchGroup objRoot, float escala) {
        BranchGroup escaladoBG = new BranchGroup();
        Transform3D escalado = new Transform3D();
        escalado.setScale(escala);
        TransformGroup transform = new TransformGroup(escalado);
        transform.addChild(objRoot);
        escaladoBG.addChild(transform);
        return escaladoBG;
    }

    /**
     * Este metodo crea un tipo de figura con los parametr
     *
     * @param posInicial
     * @param tipoFigura
     * @param ancho
     * @param alto
     * @param largo
     * @param apariencia
     * @return
     * @throws Exception
     */
    public static BranchGroup crear(Vector3f posInicial,
            tipoFigura tipoFigura,
            Float ancho, Float alto, Float largo,
            Appearance apariencia,
            String urlObjeto, Float escala) throws Exception {
        // Creamos el BranchGroup que vamos a devolver
        BranchGroup objetoBG = new BranchGroup();
        BranchGroup auxBG = new BranchGroup();

        if (tipoFigura.equals(tipoFigura.rectangulo)) {
            Box cubo = new Box(ancho, alto, largo, apariencia);
            cubo.setAppearance(apariencia);
            auxBG.addChild(cubo);
            objetoBG.addChild(MiLibreria3D.trasladarEstatico(MiLibreria3D.escalarEstatico(auxBG, escala), posInicial));
        } else if (tipoFigura.equals(tipoFigura.esfera)) {
            Sphere esfera = new Sphere(ancho, apariencia);
            esfera.setAppearance(apariencia);
            auxBG.addChild(esfera);
            objetoBG.addChild(MiLibreria3D.trasladarEstatico(MiLibreria3D.escalarEstatico(auxBG, escala), posInicial));
        } else if (tipoFigura.equals(tipoFigura.cilindro)) {
            Cylinder cilindro = new Cylinder(ancho, alto, apariencia);
            cilindro.setAppearance(apariencia);
            auxBG.addChild(cilindro);
            objetoBG.addChild(MiLibreria3D.trasladarEstatico(MiLibreria3D.escalarEstatico(auxBG, escala), posInicial));
        } else if (tipoFigura.equals(tipoFigura.objetoOBJ)) {
            Scene scene = getOBJ(urlObjeto);
            objetoBG.addChild(MiLibreria3D.trasladarEstatico(MiLibreria3D.escalarEstatico(scene.getSceneGroup(), escala), posInicial));
        } else {
            throw new Exception("Error al crear la figura");
        }

        return objetoBG;
    }

    public static Scene getOBJ(String urlObjeto) {
        // Creando un objeto OBJ
        ObjectFile file = new ObjectFile(ObjectFile.RESIZE);
        Scene scene = null;
        try {
            scene = file.load(urlObjeto);
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
        return scene;
    }

    /**
     * Este metodo establece un background a toda la escena
     *
     * @param rootBG
     * @param url
     * @param context
     * @param escala
     */
    public static void setBackground(BranchGroup rootBG, String url, Component context, int escala) {

        TextureLoader bgTexture = new TextureLoader(url, context);
        Background bg = new Background(bgTexture.getImage());
        BoundingSphere limites = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        bg.setApplicationBounds(limites);
        bg.setImageScaleMode(escala);
        BranchGroup backGeoBranch = new BranchGroup();
        bg.setGeometry(backGeoBranch);
        rootBG.addChild(bg);
    }

    /**
     * Este metodo añade el sonido a una rama de ejecucion del arbol haciendo
     * uso de dos metodos auxiliares que hay mas abajo llamados
     * configurarUniverso y anadirSonidoARama
     *
     * @param universo
     * @param rootBG
     * @param ruta
     */
    public static void addSound(SimpleUniverse universo, BranchGroup rootBG, String ruta) {
        // Configuramos el universo para que pueda ejecutar sonido
        configurarUniverso(universo);

        // Le indicamos a la rama cual es el sonido a ejecutar
        anadirSonidoARama(rootBG, ruta);
    }

    private static void configurarUniverso(SimpleUniverse universo) {
        try {
            PhysicalEnvironment pe = universo.getViewer().getPhysicalEnvironment();
            JavaSoundMixer objetoMezcladorSonidos = new JavaSoundMixer(pe);
            pe.setAudioDevice(objetoMezcladorSonidos);
            objetoMezcladorSonidos.initialize();
            universo.getViewer().getView().setPhysicalEnvironment(pe);
        } catch (Exception e) {
            System.out.println("problema de audio");
        }
    }

    private static void anadirSonidoARama(BranchGroup b, String soundFile) {
        //Create a media container to load the file
        MediaContainer droneContainer = new MediaContainer(soundFile);
        //Create the background sound from the media container
        BackgroundSound drone = new BackgroundSound(droneContainer, 1.0f);
        //Activate the sound
        Bounds limites = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        drone.setSchedulingBounds(limites);
        drone.setEnable(true);
        //Set the sound to loop forever
        drone.setLoop(BackgroundSound.INFINITE_LOOPS);
        b.addChild(drone);
    }

    /**
     * Consigue la apariencia de la imagen o textura que se indique en el
     * parametro de entrada URL
     *
     * @param url
     * @return
     */
    public static Appearance getTexture(String url, Component contexto) {
        Texture tex = new TextureLoader(url, contexto).getTexture();
        Appearance apariencia = new Appearance();
        apariencia.setTexture(tex);
        TextureAttributes texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.MODULATE);
        apariencia.setTextureAttributes(texAttr);
        return apariencia;
    }

    /**
     * Crea un suelo partiendo de la posicion (0,0,0)
     *
     * @param rootBG
     */
    public static BranchGroup CrearSuelo() {
        BranchGroup rootBG = new BranchGroup();
        int contadorTira[] = {3, 3, 3, 3};
        TriangleStripArray cuadrado = new TriangleStripArray(12, TriangleArray.COORDINATES | TriangleArray.COLOR_3, contadorTira);
        float limite = 100.0f;
        float posicionSuelo = -0.0f;
        // Primera tira
        cuadrado.setCoordinate(0, new Point3f(-limite, posicionSuelo, 0.0f));
        cuadrado.setCoordinate(1, new Point3f(0.0f, posicionSuelo, -limite));
        cuadrado.setCoordinate(2, new Point3f(limite, posicionSuelo, 0.0f));

        cuadrado.setCoordinate(3, new Point3f(limite, posicionSuelo, 0.0f));
        cuadrado.setCoordinate(4, new Point3f(0.0f, posicionSuelo, limite));
        cuadrado.setCoordinate(5, new Point3f(-limite, posicionSuelo, 0.0f));

        cuadrado.setCoordinate(6, new Point3f(limite, posicionSuelo, 0.0f));
        cuadrado.setCoordinate(7, new Point3f(0.0f, posicionSuelo, -limite));
        cuadrado.setCoordinate(8, new Point3f(-limite, posicionSuelo, 0.0f));

        cuadrado.setCoordinate(9, new Point3f(-limite, posicionSuelo, 0.0f));
        cuadrado.setCoordinate(10, new Point3f(0.0f, posicionSuelo, limite));
        cuadrado.setCoordinate(11, new Point3f(limite, posicionSuelo, 0.0f));

        float r = 58 / 255f;
        float g = 173 / 255f;
        float b = 167 / 255f;
        Color3f verde = new Color3f(r, g, b); // verde
        cuadrado.setColor(0, verde);
        cuadrado.setColor(1, verde);
        cuadrado.setColor(2, verde);
        cuadrado.setColor(3, verde);
        cuadrado.setColor(4, verde);
        cuadrado.setColor(5, verde);

        cuadrado.setColor(6, verde);
        cuadrado.setColor(7, verde);
        cuadrado.setColor(8, verde);
        cuadrado.setColor(9, verde);
        cuadrado.setColor(10, verde);
        cuadrado.setColor(11, verde);

        Shape3D forma = new Shape3D(cuadrado);

        rootBG.addChild(forma);

        return rootBG;
    }

    /**
     * Permite que podamos mover la camara en el universo en el que nos
     * encontramos
     *
     * @param universo
     * @param zonaDibujo
     */
    public static void addMovimientoCamara(SimpleUniverse universo, Canvas3D zonaDibujo) {
        // A�adimos el codigo para poder rotar nosotros mismos el objeto
        OrbitBehavior B = new OrbitBehavior(zonaDibujo);
        B.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        universo.getViewingPlatform().setViewPlatformBehavior(B);
    }

    /**
     * Coloca la camara existente del universo en la posicion indicado y mirando
     * hacia la posicion objetivo indicada
     *
     * @param universo
     * @param posici�nCamara
     * @param objetivoCamara
     */
    public static void colocarCamara(SimpleUniverse universo, Point3d posicioonCamara, Point3d objetivoCamara) {
        Point3d posicionCamara = new Point3d(posicioonCamara.x + 0.001, posicioonCamara.y + 0.001d, posicioonCamara.z + 0.001);
        Transform3D datosConfiguracionCamara = new Transform3D();
        datosConfiguracionCamara.lookAt(posicionCamara, objetivoCamara, new Vector3d(0.001, 1.001, 0.001));
        try {
            datosConfiguracionCamara.invert();
            TransformGroup TGcamara = universo.getViewingPlatform().getViewPlatformTransform();
            TGcamara.setTransform(datosConfiguracionCamara);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    /**
     * Retorna un nodo con los ejes de coordenadas situadas en el origen
     *
     * @return
     */
    public static BranchGroup CrearEjesCoordenada() {
        BranchGroup rootBG = new BranchGroup();
        LineArray linea = new LineArray(6, TriangleArray.COORDINATES | TriangleArray.COLOR_3);
        float alcance = 100.0f;
        // Eje x
        linea.setCoordinate(0, new Point3f(-alcance, 0.0f, 0.0f));
        linea.setCoordinate(1, new Point3f(alcance, 0.0f, 0.0f));
        // Eje y
        linea.setCoordinate(2, new Point3f(0.0f, -alcance, 0.0f));
        linea.setCoordinate(3, new Point3f(0.0f, alcance, 0.0f));
        // Eje z
        linea.setCoordinate(4, new Point3f(0.0f, 0.0f, -alcance));
        linea.setCoordinate(5, new Point3f(0.0f, 0.0f, alcance));

        linea.setColor(0, new Color3f(1.0f, 0.0f, 0.0f)); // Rojo
        linea.setColor(1, new Color3f(1.0f, 1.0f, 0.0f)); // Amarillo
        linea.setColor(2, new Color3f(0.0f, 1.0f, 0.0f)); // Verde
        linea.setColor(3, new Color3f(0.0f, 1.0f, 1.0f)); // Ci�n
        linea.setColor(4, new Color3f(0.0f, 0.0f, 1.0f)); // Azul
        linea.setColor(5, new Color3f(1.0f, 0.0f, 1.0f)); // Violeta

        Shape3D forma = new Shape3D(linea);
        rootBG.addChild(forma);

        return rootBG;
    }

    /**
     * Devuelve un DirectionalLight
     *
     * @return
     */
    public static DirectionalLight getDefaultIlumination() {
        DirectionalLight LuzDireccional = new DirectionalLight(new Color3f(1f, 1f, 1f), new Vector3f(1f, 0f, -1f));
        BoundingSphere limites = new BoundingSphere(new Point3d(-5, 0, 5), 100.0); //Localizacion de fuente/paso de luz
        LuzDireccional.setInfluencingBounds(limites);
        return LuzDireccional;
    }

    /**
     * ****************
     * Metodos de Alex ****************
     */
    /**
     * Devuele un Transform3D con la rotación pasada como parámetro (eje y
     * ángulo).
     */
    public static Transform3D rotarDinamico(tipoTrans tipoRot, float angulo) {
        angulo = (float) Math.toRadians(angulo);
        Transform3D rotarObj = new Transform3D();
        if (tipoRot.equals(tipoTrans.enX)) {
            rotarObj.rotX(angulo);
        } else if (tipoRot.equals(tipoTrans.enY)) {
            rotarObj.rotY(angulo);
        } else if (tipoRot.equals(tipoTrans.enZ)) {
            rotarObj.rotZ(angulo);
        }
        return rotarObj;
    }

    /**
     * Devuele un Transform3D con la escala pasada como parámetro.
     */
    public static Transform3D escalarDinamico(float escala) {
        Transform3D escalarObj = new Transform3D();
        escalarObj.setScale(escala);
        return escalarObj;
    }

    /**
     * Devuele un Transform3D con el desplazamiento pasado como parámetro.
     */
    public static Transform3D trasladarDinamico(Vector3f distancia) {
        Transform3D trasladarObj = new Transform3D();
        trasladarObj.setTranslation(distancia);
        return trasladarObj;
    }

    /**
     * Devuelve la longitud de los catetos de un triángulo isosceles a partir de
     * la hipotenusa pasada como parámetro.
     */
    public static float pitagorasIsosceles(float hipotenusa) {
        float resultado;

        resultado = (float) Math.pow(hipotenusa, 2);
        resultado /= 2;
        resultado = (float) Math.sqrt(resultado);

        return resultado;
    }

    public static Color3f obtenerColor(String hex) {
        Color3f resultado;
        float color1, color2, color3;
        color1 = (float) Integer.parseInt(hex.substring(0, 2), 16) / 255f;
        color2 = (float) Integer.parseInt(hex.substring(2, 4), 16) / 255f;
        color3 = (float) Integer.parseInt(hex.substring(4, 6), 16) / 255f;

        resultado = new Color3f(color1, color2, color3);

        return resultado;
    }

    public static TransformGroup crearTextoFinal(String texto, boolean victoria) {
        TransformGroup tgTexto = new TransformGroup();
        tgTexto.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tgTexto.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        //Creación del texto
        Text2D text;
        if (victoria) {
            text = new Text2D(texto, obtenerColor("001eff"), "Onyx", 500, Font.PLAIN);
        } else {
            text = new Text2D(texto, obtenerColor("ff0000"), "Onyx", 500, Font.PLAIN);
        }
        text.setName(texto.toLowerCase());
        PolygonAttributes polyAttrib = new PolygonAttributes();
        polyAttrib.setCullFace(PolygonAttributes.CULL_NONE);
        polyAttrib.setBackFaceNormalFlip(true);
        text.getAppearance().setPolygonAttributes(polyAttrib);
        tgTexto.addChild(text);

        return tgTexto;
    }
}
