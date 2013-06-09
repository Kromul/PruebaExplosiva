package CreacionMapas;

import Libreria3D.MiLibreria3D;
import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;
import figuras.BoxMDL;
import figuras.EsferaMDL;
import figuras.Personaje;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/**
 *
 * @author papa
 */
public class BigMind extends JFrame implements Runnable {

    SimpleUniverse universo;
    // Personaje
    //Constantes
    public final Point3d POS_CAMARA = new Point3d(0d, 5d, 10.3d);
    //Atributos
    public Canvas3D zonaDibujo;
    TransformGroup TGcamara = new TransformGroup();
    Thread hebra = new Thread(this);
    public ArrayList<CreacionMapas.Figura> listaObjetosFisicos = new ArrayList<Figura>();
    ArrayList<FiguraInteligente> listaObjetosFisicosInteligentes = new ArrayList<FiguraInteligente>();
    ArrayList<CreacionMapas.Figura> listaObjetosNoFisicos = new ArrayList<Figura>();
    DiscreteDynamicsWorld mundoFisico;
    BranchGroup conjunto;
    Figura personaje;
    public boolean actualizandoFisicas;
    public boolean mostrandoFisicas;
    // Constantes
    final String NO_EXISTE = "archivo no existente";
    final Float ESPACIO_Z = 3.0f; // espacio en el eje z entre los objetos
    // Escena
    String matrixScene[][];
    // Inteligencia Artificial
    BalaInteligente bala;
    // Posicion inicial del personaje
    float posX;
    float posY;
    float posZ;
    HebraCreadora creadora;
    BranchGroup casaVisual;
    boolean eliminarCasa;
    TransformGroup casaVisualTG;

    public BigMind() {
        CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
        Vector3f worldAabbMin = new Vector3f(-10000, -10000, -10000);
        Vector3f worldAabbMax = new Vector3f(10000, 10000, 10000);
        AxisSweep3 broadphase = new AxisSweep3(worldAabbMin, worldAabbMax);
        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
        mundoFisico = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        mundoFisico.setGravity(new Vector3f(0, -10, 0));

        Container GranPanel = getContentPane();
        JPanel Controles = new JPanel(new GridLayout(1, 4));
        Canvas3D zonaDibujo = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        GranPanel.add(zonaDibujo, BorderLayout.CENTER);
        zonaDibujo.setPreferredSize(new Dimension(780, 580));
        universo = new SimpleUniverse(zonaDibujo);
        BranchGroup escena = crearEscena();

        //Añadimos el seleccionador de ratón
        SeleccionadorRaton select = new SeleccionadorRaton(zonaDibujo, escena, this);
        escena.addChild(select);

        escena.compile();
        //This moves the ViewPlatform back a bit so objects can be viewed.
        universo.getViewingPlatform().setNominalViewingTransform();
        TGcamara = universo.getViewingPlatform().getViewPlatformTransform();
        universo.addBranchGraph(escena);
        pack();
        setVisible(true);

        MiLibreria3D.addMovimientoCamara(universo, zonaDibujo);
        MiLibreria3D.colocarCamara(universo, new Point3d(-15, 10, 18), new Point3d(0, 0, 0));
        
        hebra.start();
    }

    BranchGroup crearEscena() {
        BranchGroup rootBG = new BranchGroup();
        BranchGroup escenaBG = new BranchGroup();

        //Añadimos conjunto
        conjunto = new BranchGroup();
        rootBG.addChild(conjunto);
        conjunto.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        conjunto.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

        //Añadimos mostrar
        ComportamientoMostrar mostrar = new ComportamientoMostrar(this);
        BoundingSphere limites = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        mostrar.setSchedulingBounds(limites);
        rootBG.addChild(mostrar);

        // Creamos el mundo 3D
        rootBG.addChild(crearMundo());

        // Elementos por defecto de la escena
        String rutaFondo = System.getProperty("user.dir") + "/" + "src/resources/texturas/textura_cielo.jpg";
        String rutaSonido = "file://localhost/" + System.getProperty("user.dir") + "/" + "src/resources/sonido/magic_bells.wav";
        String rutaSuelo = System.getProperty("user.dir") + "/" + "src/resources/texturas/textura_hielo.jpg";
        MiLibreria3D.setBackground(rootBG, rutaFondo, this, 1);
//        MiLibreria3D.addSound(universo, rootBG, rutaSonido);
        try {
//            rootBG.addChild(MiLibreria3D.crear(new Vector3f(0.0f, -1.0f, 0.0f),
//                    MiLibreria3D.tipoFigura.rectangulo, 20.0f, 1.0f, 20.0f,
//                    MiLibreria3D.getTexture(rutaSuelo, this),
//                    null,
//                    1.0f));
            float radio = 6;
            float masaConstruccion = 0;
            float elasticidad = 0.3f;           //Capacidad de rebotar. 1 para grandes rebote   0 para simular gotas de liquidos espesos
            float dumpingLineal = 0.99f;    //Perdidad de velodidad al desplazarse (friccion del aire): 0 para mantener velocidad. 0.99 para perder velocidad (liquidos espesos)

            Figura construccion = new BoxMDL("objetosMDL/Iron_Golem.mdl", 20.0f, 1.0f, 20.0f, conjunto, listaObjetosFisicos, this, "texturas//ladrillo.jpg", false);
            construccion.crearPropiedades(masaConstruccion, elasticidad, dumpingLineal, 0.0f, -1.0f, 0.0f, mundoFisico);
        } catch (Exception ex) {
            Logger.getLogger(BigMind.class.getName()).log(Level.SEVERE, null, ex);
        }
        rootBG.addChild(MiLibreria3D.CrearEjesCoordenada());
        rootBG.addChild(MiLibreria3D.getDefaultIlumination());
        rootBG.addChild(MiLibreria3D.trasladarEstatico(MiLibreria3D.CrearEjesCoordenada(), new Vector3f(10f, 0f, 0f)));
        rootBG.addChild(MiLibreria3D.trasladarEstatico(MiLibreria3D.CrearEjesCoordenada(), new Vector3f(0, 0f, 12f)));

        //Caja detección casa
        Appearance apariencia = new Appearance();
        TransparencyAttributes ta = new TransparencyAttributes();
        ta.setTransparencyMode(TransparencyAttributes.BLENDED);
        ta.setTransparency(1f);
        apariencia.setTransparencyAttributes(ta);
        ColorCube casa = new ColorCube(2.5f);
        casa.setAppearance(apariencia);
        casa.setName("Casa");
        TransformGroup tgCasa = new TransformGroup(MiLibreria3D.trasladarDinamico(new Vector3f(4f, 0, 11.3f)));
        tgCasa.addChild(casa);
        tgCasa.setName("Casa");
        rootBG.addChild(tgCasa);

        // hebra para lanzar una piedra sobre la casa de la escena
        creadora = new HebraCreadora(70, 0.9f, conjunto, listaObjetosFisicos, false, this, mundoFisico);

        // hago una variable para la casa para borrarla cuando el asteroide choque con ella
        float escala = 2.5f;
        eliminarCasa = false;
        casaVisualTG = new TransformGroup();
        casaVisual = new BranchGroup();
        casaVisual.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        casaVisual.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        casaVisual.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        casaVisual.setCapability(BranchGroup.ALLOW_DETACH);
        try {
            casaVisual = MiLibreria3D.crear(
                                new Vector3f(0.0f, 0.0f, 0.0f),
                                MiLibreria3D.tipoFigura.objetoOBJ, null, null, null,
                                null,
                                System.getProperty("user.dir") + "/" + "src/resources/objetosOBJ/" + "edificios" + "/" + "granero" + ".obj",
                                escala);
        } catch (Exception ex) {
            Logger.getLogger(BigMind.class.getName()).log(Level.SEVERE, null, ex);
        }
        float ancho = 0.75f;
        float alto = 0.70f;
        float largo = 1.0f;
        casaVisualTG.addChild(
                MiLibreria3D.trasladarEstatico(
                casaVisual,
                new Vector3f(2 + (ancho * escala), 0 + (alto * escala), 9 + (largo * escala))));
        rootBG.addChild(casaVisualTG);
        
        return rootBG;
    }

    void cargarContenido() {
        //Creando el personaje del juego, controlado por teclado. Tambien se pudo haber creado en CrearEscena()
        float masa = 1f;
        float radio = 0.25f;
        posX = -11f;
        posY = 5f;
        posZ = -12f;
        float elasticidad = 0.5f;
        float dumpingLineal = 0.5f;
        personaje = new Personaje(radio, conjunto, listaObjetosFisicos, this, true);
        personaje.crearPropiedades(masa, elasticidad, 0.5f, posX, posY, posZ, mundoFisico);
        personaje.cuerpoRigido.setDamping(0.7f, 0.9f);

        //--------
//        creadora = new HebraCreadora(70, 0.9f, conjunto, listaObjetosFisicos, false, this, mundoFisico);
//        creadora.start();

        // Creamos el cañon
        // Nota: la posicion de inicio de la bola ha de ser positiva en todos los ejes
        posInicialBala = new Vector3f(-10f, 1f, 11f);//(0f, 1f, 12f);
        bala = new BalaInteligente(this, posInicialBala);
//        bala.crearBala(1f, 1, elasticidad, new Vector3f(0f, 1f, 10f));//14f));
        float distanciaBuscada = (float) Math.sqrt(Math.pow(posInicialBala.x - posX, 2) + Math.pow(posInicialBala.z - posZ, 2));
        System.out.println("Distancia buscada: " + distanciaBuscada);
    }
    Vector3f posInicialBala;

    void actualizar(float dt) {
        //ACTUALIZAR EL ESTADO DEL JUEGO
//        try{
        if(eliminarCasa){
            eliminarCasa = false;
            System.out.println("==================");
            System.out.println("CASA ELIMINADA");
            System.out.println("==================");
            
            Transform3D t = new Transform3D();
            t.set(new Vector3d(0d,-10d,0d));
            casaVisualTG.setTransform(t);
        }
        bala.actualizar(posInicialBala, new Vector3f(personaje.posiciones));
//        }catch(Exception e){
//            e.printStackTrace();
//        }

        //ACTUALIZAR DATOS DE FUERZAS DEL PERSONAJE CONTROLADO POR EL JUGADOR
        if (personaje != null) {
            float fuerzaElevacion = 0, fuerzaLateral = 0;
            float fuerzaPedroZ = 0, fuerzaPedroX = 0;
            float factorFuerza = 4;
            if (personaje.adelante) {
                //personaje.rotarAdelante();
                fuerzaElevacion = personaje.masa * 2f * 2.5f;
                fuerzaPedroZ = personaje.masa * factorFuerza * 2.5f;
            }
            if (personaje.atras) {
                fuerzaElevacion = -personaje.masa * 2f * 2.5f;
                /*
                 personaje.rotarAtras();
                 fuerzaElevacion = personaje.masa * 2f * 2.5f;
                 */
                fuerzaPedroZ = -personaje.masa * factorFuerza * 2.5f;
            }
            if (personaje.derecha) {
                fuerzaLateral = -personaje.masa * 4f;
                /*
                 personaje.rotarDerecha();
                 fuerzaElevacion = personaje.masa * 2f * 2.5f;
                 */
                fuerzaPedroX = -personaje.masa * factorFuerza * 2.5f;
            }
            if (personaje.izquierda) {
                //fuerzaLateral = personaje.masa * 4f;

                personaje.rotarIzquierda();
                fuerzaElevacion = personaje.masa * 2f * 2.5f;
                fuerzaPedroX = personaje.masa * factorFuerza * 2.5f;
            }
            if (personaje.corriendo) {
                fuerzaElevacion *= 2;
            }
            if (!personaje.parar) {
                Vector3d direccionFrente = personaje.conseguirDireccionFrontal();
//Alex-->                personaje.cuerpoRigido.applyCentralForce(new Vector3f((float) direccionFrente.x * fuerzaElevacion * 0.1f, 0, (float) direccionFrente.z * fuerzaElevacion * 0.1f));
                personaje.cuerpoRigido.applyCentralForce(new Vector3f(fuerzaPedroX, 0, fuerzaPedroZ));
//                personaje.cuerpoRigido.applyTorque(new Vector3f(0, fuerzaLateral, 0));
            } else {
                //System.out.println("parado");
                personaje.cuerpoRigido.clearForces();
            }
        }

        //ACTUALIZAR DATOS DE FUERZAS DE LAS FIGURAS AUTONOMAS  (ej. para que cada figura pueda persiguir su objetivo)
        for (int i = 0; i < this.listaObjetosFisicos.size(); i++) {
            listaObjetosFisicos.get(i).actualizar();
        }
        // ACTUALIZO LA FIGURA INTELIGENTE
        for (int i = 0; i < this.listaObjetosFisicosInteligentes.size(); i++) {
            listaObjetosFisicosInteligentes.get(i).actualizar();
        }

        //ACTUALIZAR DATOS DE LOCALIZACION DE FIGURAS FISICAS
        this.actualizandoFisicas = true;
        try {
            mundoFisico.stepSimulation(dt);    //mundoFisico.stepSimulation ( dt  ,50000, dt*0.2f);
        } catch (Exception e) {
            System.out.println("JBullet forzado. No debe crearPropiedades de solidoRigidos durante la actualizacion stepSimulation");
        }
        this.actualizandoFisicas = false;
    }

    void mostrar() throws Exception {
        //MOSTRAR FIGURAS FISICAS (muestra el componente visual de la figura, con base en los datos de localizacion del componente fisico)
        try {
            if ((mundoFisico.getCollisionObjectArray().size() != 0) && (listaObjetosFisicos.size() != 0)) {
                for (int idFigura = 0; idFigura <= this.listaObjetosFisicos.size() - 1; idFigura++) {     // Actualizar posiciones fisicas y graficas de los objetos.
                    try {
                        int idFisico = listaObjetosFisicos.get(idFigura).identificadorFisico;
                        CollisionObject objeto = mundoFisico.getCollisionObjectArray().get(idFisico); //
                        RigidBody cuerpoRigido = RigidBody.upcast(objeto);
                        listaObjetosFisicos.get(idFigura).mostrar(cuerpoRigido);
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
        }

        this.mostrandoFisicas = true;
        try {
            if ((mundoFisico.getCollisionObjectArray().size() != 0) && (listaObjetosFisicosInteligentes.size() != 0)) {
                for (int idFigura = 0; idFigura <= this.listaObjetosFisicosInteligentes.size() - 1; idFigura++) {     // Actualizar posiciones fisicas y graficas de los objetos.
                    try {
                        int idFisico = listaObjetosFisicosInteligentes.get(idFigura).identificadorFisico;
                        CollisionObject objeto = mundoFisico.getCollisionObjectArray().get(idFisico); //
                        RigidBody cuerpoRigido = RigidBody.upcast(objeto);
                        listaObjetosFisicosInteligentes.get(idFigura).mostrar(cuerpoRigido);
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
        }
        this.mostrandoFisicas = false;
        //MOSTRAR CÁMARA
        Point3d objetivo = new Point3d(personaje.posiciones[0], personaje.posiciones[1], personaje.posiciones[2]);
        Point3d posicion = new Point3d(personaje.posiciones[0], personaje.posiciones[1] + 5, -(personaje.posiciones[2] + 10.3));
//        colocarCamaraDinamico(posicion, objetivo);
    }

    public void run() {
        cargarContenido();
        float dt = 3f / 100f;
        int tiempoDeEspera = (int) (dt * 1000);
        while (true) {
            try {
                actualizar(dt);
            } catch (Exception e) {
                System.out.println("Error durante actualizar. Estado del juego ");
            }
            try {
                Thread.sleep(tiempoDeEspera);
            } catch (Exception e) {
            }
        }
    }

    public void colocarCamaraDinamico(Point3d posiciónCamara, Point3d objetivoCamara) {
        Point3d posicionCamara = new Point3d(posiciónCamara.x + 0.001, posiciónCamara.y + 0.001d, posiciónCamara.z + 0.001);
        Transform3D datosConfiguracionCamara = new Transform3D();
        datosConfiguracionCamara.lookAt(posicionCamara, objetivoCamara, new Vector3d(0.001, 1.001, 0.001));
        try {
            datosConfiguracionCamara.invert();
            TGcamara.setTransform(datosConfiguracionCamara);
        } catch (Exception e) {
        }
    }

    public String leerEscena(String escena, String archivo) {
        try {
            // Abrimos el archivo
            FileInputStream fstream = new FileInputStream(System.getProperty("user.dir") + "//" + "src//" + archivo);
            // Creamos el objeto de entrada
            DataInputStream entrada = new DataInputStream(fstream);
            // Creamos el Buffer de Lectura
            BufferedReader buffer = new BufferedReader(new InputStreamReader(entrada));
            String strLinea;
            // Leer el archivo linea por linea
            while ((strLinea = buffer.readLine()) != null) {
                // Imprimimos la línea por pantalla
                //System.out.println (strLinea);
                escena = escena + strLinea;
            }
            // Cerramos el archivo
            entrada.close();
        } catch (Exception e) { //Catch de excepciones
            System.err.println("Ocurrio un error: " + e.getMessage());
        }
        return escena;
    }

    private BranchGroup crearMundo() {
        BranchGroup mundoBG = new BranchGroup();
        try {
            // Leemos el fichero de texto a partir del cual leemos la escena
            String escena = "";
            escena = escena + leerEscena(escena, "resources//escenarios//EscenaBasica.txt");

            // Leer informacion de OBJ
            String info_obj = "";
            info_obj = info_obj + leerEscena(escena, "CreacionMapas//info_obj.txt");

            StringTokenizer str = new StringTokenizer(escena, "\t");

            // Configuramos los rectangulos que crearan la escena
            float ancho = 0.5f;
            float largo = 0.5f;
            float alto = 0.1f;
            float posInicialX = -13.0f;
            float posInicialY = 0.0f;
            float posInicialZ = -14.0f;
            float posSiguienteX = posInicialX;
            float posSiguienteY = posInicialY;
            float posSiguienteZ = posInicialZ;
            float posAnteriorX = posInicialX;
            float posAnteriorY = posInicialY;
            float posAnteriorZ = posInicialZ;
            float escala = 1;
            MiLibreria3D.tipoTrans[] transformacion = new MiLibreria3D.tipoTrans[3];
            float[] grados = new float[3];
            Float[] posManual = new Float[3];
            boolean esObjetoInicial = true; // esto nos sirve para colocar justo en (0,0,0) el comienzo del mapa
            MiLibreria3D.tipoFigura tipoFigura = MiLibreria3D.tipoFigura.rectangulo;

            String elemento = str.nextToken();
            // Creamos el escenario
            while (str.hasMoreTokens() && !elemento.contains("FIN")) {
                ancho = alto = largo = 0;
                //System.out.println(elemento + "(" + posSiguienteX + ", " + posSiguienteY + ", " + posSiguienteZ + ")");

                // Creo el espacio el cual es un comando que se puede realizar en cualquier sitio
                if (elemento.contains("espacio")) {
                    if (elemento.contains("espacioX")) {
                        posSiguienteX = posSiguienteX + Float.parseFloat(elemento.substring(elemento.indexOf("espacioX") + 8, elemento.indexOf("/", elemento.indexOf("espacioX") + 8)));
                    }
                    // No esta implementado crear espacios en Y, si se desea situar un objeto
                    if (elemento.contains("espacioZ")) {
                        posSiguienteZ = posSiguienteZ + Float.parseFloat(elemento.substring(elemento.indexOf("espacioZ") + 8, elemento.indexOf("/", elemento.indexOf("espacioZ") + 8)));
                    }
                }

                // Conseguimos la escala
                if (elemento.contains("esc")) {
                    escala = Float.parseFloat(elemento.substring(elemento.indexOf("esc") + 3, elemento.indexOf("/", elemento.indexOf("esc") + 3)));
                } else {
                    escala = 1;
                }

                // Conseguimos la rotacion
                if (elemento.contains("rotX")) {
                    transformacion[0] = MiLibreria3D.tipoTrans.enX;
                    grados[0] = Float.parseFloat(elemento.substring(elemento.indexOf("rotX") + 4, elemento.indexOf("/", elemento.indexOf("rotX") + 4)));
                }
                if (elemento.contains("rotY")) {
                    transformacion[1] = MiLibreria3D.tipoTrans.enY;
                    grados[1] = Float.parseFloat(elemento.substring(elemento.indexOf("rotY") + 4, elemento.indexOf("/", elemento.indexOf("rotY") + 4)));
                }
                if (elemento.contains("rotZ")) {
                    transformacion[2] = MiLibreria3D.tipoTrans.enZ;
                    grados[2] = Float.parseFloat(elemento.substring(elemento.indexOf("rotZ") + 4, elemento.indexOf("/", elemento.indexOf("rotZ") + 4)));
                }

                // Conseguimos la posicion en caso de qeu se vaya a realizar un posicionamiento manual
                posManual = null;
                if (elemento.contains("posX") || elemento.contains("posY") || elemento.contains("posZ")) {
                    posManual = new Float[3];
                    posManual[0] = posManual[1] = posManual[2] = 0.0f;
                    if (elemento.contains("posX")) {
                        posManual[0] = Float.parseFloat(elemento.substring(elemento.indexOf("posX") + 4, elemento.indexOf("/", elemento.indexOf("posX") + 4)));
                    }
                    if (elemento.contains("posY")) {
                        posManual[1] = Float.parseFloat(elemento.substring(elemento.indexOf("posY") + 4, elemento.indexOf("/", elemento.indexOf("posY") + 4)));
                    }
                    if (elemento.contains("posZ")) {
                        posManual[2] = Float.parseFloat(elemento.substring(elemento.indexOf("posZ") + 4, elemento.indexOf("/", elemento.indexOf("posZ") + 4)));
                    }
                }

                // Situo la posicion X en el punto inicial X desde cual comenzamos a crear objetos
                if (elemento.contains("final")) {
                    posSiguienteX = posInicialX;
                    esObjetoInicial = true;
                }


                // Pasamos a la colocacion y creacion de los objetos, ya sean Java3D o .OBJ
                if (elemento.contains("java")) {
                    boolean elementoExiste = true;
                    if (elemento.contains("esf")) {
                        tipoFigura = MiLibreria3D.tipoFigura.esfera;
                    } else if (elemento.contains("cil")) {
                        tipoFigura = MiLibreria3D.tipoFigura.cilindro;
                    } else if (elemento.contains("caja")) {
                        tipoFigura = MiLibreria3D.tipoFigura.rectangulo;
                    } else {
                        elementoExiste = false;
                    }

                    if (elementoExiste) {
                        // Conseguimos el ancho, alto y largo
                        if (elemento.contains("ancho")) {
                            ancho = Float.parseFloat(elemento.substring(elemento.indexOf("ancho") + 5, elemento.indexOf("/", elemento.indexOf("ancho") + 5)));
                        }
                        if (elemento.contains("alto")) {
                            alto = Float.parseFloat(elemento.substring(elemento.indexOf("alto") + 4, elemento.indexOf("/", elemento.indexOf("alto") + 4)));
                        }
                        if (elemento.contains("largo")) {
                            largo = Float.parseFloat(elemento.substring(elemento.indexOf("largo") + 5, elemento.indexOf("/", elemento.indexOf("largo") + 5)));
                        }

                        // Conseguimos la textura del objeto
                        String textura = "";
                        if (elemento.contains("text")) {
                            textura = elemento.substring(elemento.indexOf("text") + 4, elemento.indexOf("/", elemento.indexOf("text") + 4));
                        }

                        Appearance apariencia = null;
                        try {
                            apariencia = MiLibreria3D.getTexture(System.getProperty("user.dir") + "/" + "src/resources/texturas/" + "textura_" + textura, this);
                        } catch (Exception e) {
                        }

                        // Colcamos el proximo objeto en su posicion
                        // cambiando la X y la Y ya que son las unicas variables
                        // que se ven afectadas al rellenar nuestro mapa de izquierda
                        // a derecha
                        posAnteriorX = posSiguienteX + (ancho * escala);
                        posAnteriorY = alto * escala;
                        Vector3f posicion;
                        // Si el posicionamiento no es manual situamos el objeto en la posicion
                        // siguiente situandolo al lado del anterior objeto
                        if (posManual == null) {
                            posicion = new Vector3f(posAnteriorX, posAnteriorY, (posSiguienteZ + (largo * escala)));
                        } else {
                            posicion = new Vector3f(posManual[0] + (ancho * escala), posManual[1] + (alto * escala), posManual[2] + (largo * escala));
                        }
                        // Lo introducimos dentro del arbol y lo trasladamos al lugar correcto
                        // Esto seria para objetos no fisicos
//                        mundoBG.addChild(
//                                MiLibreria3D.trasladarEstatico(
//                                MiLibreria3D.rotarEstatico(
//                                MiLibreria3D.rotarEstatico(
//                                MiLibreria3D.rotarEstatico(
//                                MiLibreria3D.crear(new Vector3f(0.0f, 0.0f, 0.0f),
//                                tipoFigura, ancho, alto, largo,
//                                apariencia,
//                                null,
//                                escala),
//                                grados[0], transformacion[0]),
//                                grados[1], transformacion[1]),
//                                grados[2], transformacion[2]),
//                                posicion));

                        float masaConstruccion = 0;
                        float elasticidad = 0.3f;           //Capacidad de rebotar. 1 para grandes rebote   0 para simular gotas de liquidos espesos
                        float dumpingLineal = 0.99f;    //Perdidad de velodidad al desplazarse (friccion del aire): 0 para mantener velocidad. 0.99 para perder velocidad (liquidos espesos)

                        ancho = ancho * escala;
                        alto = alto * escala;
                        largo = largo * escala;
                        //System.out.println("POSICION FIGURA:" + posicion.x);
                        Figura construccion = new BoxMDL("objetosMDL/Iron_Golem.mdl", ancho, alto, largo, conjunto, listaObjetosFisicos, this, "src/resources/texturas/textura_hielo.jpg", false);
                        construccion.crearPropiedades(masaConstruccion, elasticidad, dumpingLineal, posicion.x, posicion.y, posicion.z, mundoFisico);

                        // Actualizamos la posicion siguiente
                        posSiguienteX = posAnteriorX + ancho * escala;
                        transformacion[0] = transformacion[1] = transformacion[2] = null;
                    }
                } else if (elemento.contains("obj")) {
                    // Una vez que sabemos que es un OBJ vemos en que carpeta esta
                    // y que archivo debemos de coger
                    String carpeta = NO_EXISTE;
                    String archivo = NO_EXISTE;
                    if (elemento.contains("natur")) {
                        carpeta = "naturaleza";
                        if (elemento.contains("asteroid")) {
                            archivo = "asteroid";
                        } else if (elemento.contains("palm")) {
                            archivo = "palm";
                        } else if (elemento.contains("tree")) {
                            archivo = "tree";
                        } else if (elemento.contains("tree_dry")) {
                            archivo = "tree_dry";
                        } else if (elemento.contains("tree_conifer")) {
                            archivo = "tree_conifer";
                        } else if (elemento.contains("druid_morning_star")) {
                            archivo = "druid_morning_star";
                        } else if (elemento.contains("elephant")) {
                            archivo = "elephant";
                        } else if (elemento.contains("turtle")) {
                            archivo = "turtle";
                        } else if (elemento.contains("mossy_rock")) {
                            archivo = "mossy_rock";
                        }
                    } else if (elemento.contains("ataq")) {
                        carpeta = "ataques";
                        if (elemento.contains("mine")) {
                            archivo = "mine";
                        } else if (elemento.contains("microbio")) {
//                            archivo = "microbio";
                        } else if (elemento.contains("war_axe")) {
                            archivo = "war_axe";
                        }
                    } else if (elemento.contains("edif")) {
                        carpeta = "edificios";
                        if (elemento.contains("house")) {
                            archivo = "house";
                        } else if (elemento.contains("fence")) {
                            archivo = "fence";
                        } else if (elemento.contains("granja")) {
                            archivo = "granja";
                        } else if (elemento.contains("granero")) {
                            archivo = "granero";
                        } else if (elemento.contains("rural_stall")) {
                            archivo = "rural_stall";
                        } else if (elemento.contains("bar_concreto2")) {
                            archivo = "bar_concreto2";
                        } else if (elemento.contains("bar_concreto")) {
                            archivo = "bar_concreto";
                        } else if (elemento.contains("brick_shader")) {
                            archivo = "brick_shader";
                        }
                    } else if (elemento.contains("otros")) {
                        carpeta = "otros";
                        if (elemento.contains("dragon")) {
                            archivo = "dragon";
                        }
                    }

                    // Buscamos cual es la posicion de inicio del objeto en el archivo info_obj.txt
                    StringTokenizer str_info = new StringTokenizer(info_obj, "\t");
                    String elemento_info = "";
                    boolean encontradoPosicion = false;
                    while (str_info.hasMoreTokens() && !encontradoPosicion) {
                        elemento_info = str_info.nextToken();
                        if (elemento_info.equalsIgnoreCase(archivo)) {
                            encontradoPosicion = true;
                        }
                    }

                    // Si encontramos los datos que necesitamos para situar el objeto
                    // pasamos al else y sino decimos que algo ha ido mal al intentar
                    // encontrar estos datos en el fichero info_obj.txt
                    if (!encontradoPosicion || carpeta.equalsIgnoreCase(NO_EXISTE) || archivo.equalsIgnoreCase(NO_EXISTE)) {
                        try {
                            ancho = alto = largo = 0;
                            throw new IllegalArgumentException("La carpeta/archivo OBJ señalado no existe");
                        } catch (IllegalArgumentException e) {
                            System.err.println("No se encontro la carpeta, archivo o datos de " + elemento);
                        }
                    } else {
                        // Conseguimos el ancho, alto y largo del modelo
                        ancho = Float.parseFloat(str_info.nextToken());
                        alto = Float.parseFloat(str_info.nextToken());
                        largo = Float.parseFloat(str_info.nextToken());

                        // Colcamos el proximo objeto en su posicion
                        // cambiando la X y la Y ya que son las unicas variables
                        // que se ven afectadas al rellenar nuestro mapa de izquierda
                        // a derecha
                        posAnteriorX = posSiguienteX + (ancho * escala);
                        posAnteriorY = alto * escala;

                        Vector3f posicion;
                        // Si el posicionamiento no es manual situamos el objeto en la posicion
                        // siguiente situandolo al lado del anterior objeto
                        if (posManual == null) {
                            posicion = new Vector3f(posAnteriorX, posAnteriorY, (posSiguienteZ + (largo * escala)));
                        } else {
                            posicion = new Vector3f(posManual[0] + (ancho * escala), posManual[1] + (alto * escala), posManual[2] + (largo * escala));
                        }
                        if (elemento.contains("click")) {
                            System.out.println("elemento: " + elemento + " clickable");
                        }
                        // Lo introducimos dentro del arbol y lo trasladamos al lugar correcto
                        Node obj = MiLibreria3D.crear(new Vector3f(0.0f, 0.0f, 0.0f),
                                MiLibreria3D.tipoFigura.objetoOBJ, null, null, null,
                                null,
                                System.getProperty("user.dir") + "/" + "src/resources/objetosOBJ/" + carpeta + "/" + archivo + ".obj",
                                escala);
                        mundoBG.addChild(MiLibreria3D.trasladarEstatico(
                                MiLibreria3D.rotarEstatico(
                                MiLibreria3D.rotarEstatico(
                                MiLibreria3D.rotarEstatico(
                                obj,
                                grados[0], transformacion[0]),
                                grados[1], transformacion[1]),
                                grados[2], transformacion[2]),
                                posicion));

//                        float masaConstruccion = 0;
//                        float elasticidad = 0.3f;           //Capacidad de rebotar. 1 para grandes rebote   0 para simular gotas de liquidos espesos
//                        float dumpingLineal = 0.99f;    //Perdidad de velodidad al desplazarse (friccion del aire): 0 para mantener velocidad. 0.99 para perder velocidad (liquidos espesos)
//
//                        Figura construccion = new BoxMDL("objetosMDL/Iron_Golem.mdl", 1, 1, 1, conjunto, listaObjetosFisicos, this, "src/resources/texturas/ladrillo.jpg",false);
//                        construccion.crearPropiedades(masaConstruccion, elasticidad, dumpingLineal, posicion.x, posicion.y, posicion.z, mundoFisico);

                        // Actualizamos la posicion siguiente
                        posSiguienteX = posAnteriorX + ancho * escala;
                        transformacion[0] = transformacion[1] = transformacion[2] = null;
                    }
                }
                elemento = str.nextToken();
            }


        } catch (Exception ex) {
            Logger.getLogger(BigMind.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return mundoBG;
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
            fig = new EsferaMDL("src/resources/objetosOBJ/ataques/war_axe.obj", radio, conjunto, listaObjetosFisicos, juego);
            if (!juego.actualizandoFisicas) {
                fig.crearPropiedades(masa, elasticidad, dumpingLineal, 4, 10, 11, mundoFisico);
            }
        }
    }
}
