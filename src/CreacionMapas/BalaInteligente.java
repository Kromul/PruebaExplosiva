package CreacionMapas;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Vector3f;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.M5P;
import weka.core.Instance;
import weka.core.Instances;

/**
 * @author Pedro Reyes
 */
public class BalaInteligente {

    // Variables finales
    float radio = 0.1f;
    float elasticidad = 0.1f;
    float masaBalon = 1;
    float masa = 1f;
    // Variables del mundo
    ProjectExplosion juego;
    Vector3f posicion;
    boolean balaCreada;
    boolean lanzado;
    // Variables referentes a la inteligencia
    private Instances casosEntrenamiento;
    Classifier conocimiento = null;
    int maximoNumeroCasosEntrenamiento = 200;
    FiguraInteligente balaInteligente;
    // Variables referentes a resultados de la inteligencia
    // Creo 2 veces las mismas variables dado que el objetivo
    // del cañon es es el mismo, solo que hay que encontrar una
    // una fuerza y angulo para una distancia en Z y otra para 
    // una distancia en X
    float fuerzaZ;
    float anguloZ;
    double resultadoEsperadoZ;
    float fuerzaX;
    float anguloX;
    double resultadoEsperadoX;
    float fuerzaHorizontalZ = 0, fuerzaVerticalZ = 0;
    float fuerzaHorizontalX = 0, fuerzaVerticalX = 0;
    float fuerzaVerticalY = 0;

    public BalaInteligente(ProjectExplosion juego, Vector3f posicion) {
        this.juego = juego;
        cargarContenidoAprendizajeInicial();
        lanzado = false;
        this.posicion = new Vector3f(posicion);
        balaCreada = false;
        anguloZ = 0;
        fuerzaZ = 0;
        resultadoEsperadoZ = 0;
        anguloX = 0;
        fuerzaX = 0;
        resultadoEsperadoX = 0;
    }

    /**
     * Actualizamos las fuerzas pasandole la posicion desde la que disparamos y
     * la posicion a la que queremos disparar
     *
     * @param posicionDisparo
     * @param posicionObjetivo
     */
    public void actualizar(Vector3f posDisparo, Vector3f posObjetivo) {
//        System.out.println("Que comience el juego");
        /**
         * ************************
         * INTELIGENCIA ARTIFICIAL ************************
         */
        if (!balaCreada) {
//            System.out.println("SE CREA LA BOLA");
            balaCreada = true;
            if (balaInteligente != null) {
                balaInteligente.remover();
            }
            balaInteligente = crearBala(radio, masa, elasticidad, posicion);
        }

//            System.out.println("=========Vuelta a empezar=========");
        Random r = new Random();
//        System.out.println(posDisparo.z);
//        System.out.println(posObjetivo.z);
        float distanciaObjetivoZ = posObjetivo.z < posDisparo.z ? Math.abs(Math.abs(posObjetivo.z) + Math.abs(posDisparo.z)) : Math.abs(Math.abs(posDisparo.z) - Math.abs(posObjetivo.z));
        float distanciaObjetivoX = posObjetivo.x < posDisparo.x ? Math.abs(Math.abs(posObjetivo.x) + Math.abs(posDisparo.x)) : Math.abs(Math.abs(posDisparo.x) - Math.abs(posObjetivo.x));
//        System.out.println(distanciaObjetivoX);
//        float distanciaObjetivoZ = 20;//r.nextInt(20) + 1;
//        float distanciaObjetivoX = 20;

        Instance casoAdecidir = new Instance(casosEntrenamiento.numAttributes());
        casoAdecidir.setDataset(casosEntrenamiento);
        //--------------------------------------------------------------------
        //ACTUALIZAR DATOS DE FUERZAS DEL PERSONAJE CONTROLADO POR EL JUGADOR
        //--------------------------------------------------------------------
        if (balaCreada && !lanzado) {
//            System.out.println("Se lanza la bola");
            // Consigo las fuerzas y angulos en X y Z para las dos distancias que tengo
//            System.out.println("Distancia objetivo Z:" + distanciaObjetivoZ);
            boolean encontradoResultadoZ = buscarFuerzaAnguloZ(casoAdecidir, distanciaObjetivoZ, r);
            boolean encontradoResultadoX = buscarFuerzaAnguloX(casoAdecidir, distanciaObjetivoX, r);

            // Establezco el caso a decidir
            if (!encontradoResultadoZ) {
                //System.out.println("No encontrado");
                // Proponemos nosotros unos valores aleatorios
                casoAdecidir.setValue(0, fuerzaZ = r.nextInt(450) + 50);
                casoAdecidir.setValue(1, anguloZ = r.nextInt(69)+20);
            }

            // Calculas fuerzas
            fuerzaVerticalZ = (float) (fuerzaZ * Math.sin(Math.toRadians(anguloZ))) * balaInteligente.masa * 10f;
            fuerzaVerticalZ = (float) (fuerzaX * Math.sin(Math.toRadians(anguloX))) * balaInteligente.masa * 10f;
            fuerzaHorizontalZ = (float) (fuerzaZ * Math.cos(Math.toRadians(anguloZ))) * balaInteligente.masa * 10f;
            fuerzaHorizontalX = (float) (fuerzaX * Math.cos(Math.toRadians(anguloX))) * balaInteligente.masa * 10f;

            // Consigo la fuerza final en Y que le dare la cual sera la mayor
            // de las que necesite para llegar a la distancia X o Z sugeridas
            fuerzaVerticalY = fuerzaVerticalZ > fuerzaVerticalX ? fuerzaVerticalZ : fuerzaVerticalX;
            // Indicamos la fuerza real que aplicamos para despues entrenar con valores
            // reales a la inteligencia
            fuerzaVerticalZ = fuerzaVerticalY;

            // Comprobamos si tenemos que aplicar la fuerza en X hacia derecha o hacia izquierda
            fuerzaHorizontalX = posicion.x > posObjetivo.x ? -fuerzaHorizontalX : fuerzaHorizontalX;

            // Aplico las fuerzas
            balaInteligente.cuerpoRigido.applyCentralForce(new Vector3f(fuerzaHorizontalX, fuerzaVerticalY, -fuerzaHorizontalZ));
            lanzado = true;
            // Indicamos la fuerza real que aplicamos para despues entrenar con valores
            // reales a la inteligencia
            fuerzaZ = fuerzaVerticalZ > fuerzaVerticalX ? fuerzaZ : fuerzaX;
            anguloZ = fuerzaVerticalZ > fuerzaVerticalX ? anguloZ : anguloX;
        }

        if (lanzado && balaInteligente.posiciones[1] < radio) {
//            System.out.println("Se buscan las nuevas fuerzas");
            lanzado = false;
            float posX;
            float posY, posZ, masa;
            radio = 1f;
            posX = posicion.x;
            posY = posicion.y;
            posZ = posicion.z;
            posX = balaInteligente.posiciones[0];
            posY = balaInteligente.posiciones[1];
            posZ = balaInteligente.posiciones[2];

            // Conseguimos la distancia entre los dos puntos
            // Realmente la distancia que decimos si ha cubierto respecto a lo esperado
            // es la distancia en Z ya que no podemos indicar a weka que dada
            // una fuerza en un eje y en otro de un resultado, al menos yo no he sabido.

            float distanciaRealCubierta = (float) Math.sqrt(Math.pow(posicion.x - posX, 2) + Math.pow(posicion.z - posZ, 2));
            //posZ < 0 ? posicion.z + Math.abs(posZ) : posicion.z - Math.abs(posZ);
//            System.out.println("Fuerza: " + fuerzaZ + ", Angulo " + anguloZ + " Distancia real: " + distanciaRealCubierta);
            //System.out.println(fuerzaZ + "," + anguloZ + "," + distanciaRealCubierta);
            // Cuando volvamos a actualizar se creara la bola de neuvo
            balaCreada = false;
            //=============================================================================
            // Aqui habria que indicarle a la IA artificial cual es el dato real
            // para el ultimo par fuerzaZ-anguloZ que ha dado
            //APRENDIZAJE
            try {
                //TOMA DE DECISION  con la mejor combinacion de valores de variables
                //Si ya se puede leer el resultado final, se puede aprender dicha experiencia
                float resultadoRealObservado = distanciaRealCubierta;   //... por ejemplo..

                casoAdecidir.setValue(0, fuerzaZ);
                casoAdecidir.setValue(1, anguloZ);
                casoAdecidir.setClassValue(resultadoRealObservado);
                casosEntrenamiento.add(casoAdecidir);
                for (int i = 0; i < casosEntrenamiento.numInstances() - this.maximoNumeroCasosEntrenamiento; i++) {
                    casosEntrenamiento.delete(0);  //Si hay muchos ejemplos borrar el más antiguo
                }
                conocimiento.buildClassifier(casosEntrenamiento);
                Evaluation evaluador = new Evaluation(casosEntrenamiento);
                evaluador.crossValidateModel(conocimiento, casosEntrenamiento, 10, new Random(1));

//                System.out.println("Distancia objetivo:" + distanciaObjetivoZ + ", Resultado real: " + distanciaRealCubierta + ", Resultado esperado: " + resultadoEsperadoZ + ",( " + fuerzaZ + ", " + anguloZ + ")");
            } catch (Exception e) {
                e.printStackTrace();
            }
            //=============================================================================
        }
    }

    public synchronized void cargarContenidoAprendizajeInicial() {
        try {
            String FicheroCasosEntrenamiento = System.getProperty("user.dir") + "/inteligencia.arff";
            casosEntrenamiento = new Instances(new BufferedReader(new FileReader(FicheroCasosEntrenamiento)));
            casosEntrenamiento.setClassIndex(casosEntrenamiento.numAttributes() - 1);
            conocimiento = new M5P();   //àra regresión, o J48 para clasificación
            conocimiento.buildClassifier(casosEntrenamiento);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized EsferaMDL crearBala(float radio, float masa, float elasticidad, Vector3f posicion) {
        EsferaMDL balaInteligente = new EsferaMDL("objetosMDL/Iron_Golem.mdl", radio, juego.conjunto, juego.listaObjetosFisicosInteligentes, juego, true, posicion);
        balaInteligente.crearPropiedades(masa, elasticidad, 0.9f, posicion.x, posicion.y, posicion.z, juego.mundoFisico);//0f, 1f, 1f, mundoFisico);
//        System.out.println(balaInteligente.posiciones[0] + ", " + balaInteligente.posiciones[1] + ", " + balaInteligente.posiciones[2]);
        return balaInteligente;
    }

    private boolean buscarFuerzaAnguloZ(Instance casoAdecidir, float distanciaObjetivoZ, Random r) {
        //=============================================================================
        // Aqui habria que capturar los valores fuerzaZ-anguloZ para los cuales
        // la IA cree que son los correctos para llegar a la distancia que le indiquemos
        //ENTRENAMIENTO
        //creando la consulta a la base de conocimiento
        //============================================================
        // Busco las fuerzas para lanzar una pelota a una distancia Z
        //============================================================
        boolean encontradoResultadoZ = false;
        for (fuerzaZ = 1; fuerzaZ < 500 && !encontradoResultadoZ; fuerzaZ++) {// Poner en el jeugo mas de 50 de fuerzaZ no tiene sentido
            for (anguloZ = 89; anguloZ > 20 && !encontradoResultadoZ; anguloZ--) {
                casoAdecidir.setValue(0, fuerzaZ);
                casoAdecidir.setValue(1, anguloZ);
                try {
                    resultadoEsperadoZ = conocimiento.classifyInstance(casoAdecidir);
                    if (resultadoEsperadoZ > distanciaObjetivoZ && resultadoEsperadoZ < distanciaObjetivoZ + 2) {
                        encontradoResultadoZ = true;
                        //System.out.println("Resultado esperado: " + resultadoEsperadoZ);
//                        System.out.println("¡encontrado! con un resultado esperado de " + resultadoEsperadoZ);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(BalaInteligente.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        if (!encontradoResultadoZ) {
            // Proponemos nosotros unos valores aleatorios
            casoAdecidir.setValue(0, fuerzaZ = r.nextInt(500) + 1);
            casoAdecidir.setValue(1, anguloZ = r.nextInt(89));
        }
        fuerzaVerticalZ = (float) (fuerzaZ * Math.sin(Math.toRadians(anguloZ))) * balaInteligente.masa * 10f;
        fuerzaHorizontalZ = (float) (fuerzaZ * Math.cos(Math.toRadians(anguloZ))) * balaInteligente.masa * 10f;
        return encontradoResultadoZ;
    }

    private boolean buscarFuerzaAnguloX(Instance casoAdecidir, float distanciaObjetivoX, Random r) {
        //=============================================================================
        // Aqui habria que capturar los valores fuerzaZ-anguloZ para los cuales
        // la IA cree que son los correctos para llegar a la distancia que le indiquemos
        //ENTRENAMIENTO
        //creando la consulta a la base de conocimiento
        //============================================================
        // Busco las fuerzas para lanzar una pelota a una distancia Z
        //============================================================
        boolean encontradoResultadoX = false;
        for (fuerzaX = 1; fuerzaX < 500 && !encontradoResultadoX; fuerzaX++) {// Poner en el jeugo mas de 50 de fuerzaZ no tiene sentido
            for (anguloX = 89; anguloX > 20 && !encontradoResultadoX; anguloX--) {
                casoAdecidir.setValue(0, fuerzaX);
                casoAdecidir.setValue(1, anguloX);
                try {
                    resultadoEsperadoX = conocimiento.classifyInstance(casoAdecidir);
                    if (resultadoEsperadoX > distanciaObjetivoX && resultadoEsperadoX < distanciaObjetivoX + 2) {
                        encontradoResultadoX = true;
//                        System.out.println("¡encontrado! con un resultado esperado de " + resultadoEsperadoZ);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(BalaInteligente.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        if (!encontradoResultadoX) {
            // Proponemos nosotros unos valores aleatorios
            casoAdecidir.setValue(0, fuerzaZ = r.nextInt(500) + 1);
            casoAdecidir.setValue(1, anguloZ = r.nextInt(89));
        }
        fuerzaVerticalZ = (float) (fuerzaZ * Math.sin(Math.toRadians(anguloZ))) * balaInteligente.masa * 10f;
        fuerzaHorizontalZ = (float) (fuerzaZ * Math.cos(Math.toRadians(anguloZ))) * balaInteligente.masa * 10f;
        return encontradoResultadoX;
    }
}
