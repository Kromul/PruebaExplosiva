package CreacionMapas;

import javax.media.j3d.*;
import javax.vecmath.*;
import java.util.ArrayList;
import com.bulletphysics.collision.dispatch.*;
import com.bulletphysics.dynamics.*;
import com.bulletphysics.linearmath.*;

/**
 * @author Alejandro Ruiz
 */
public class Figura {

    public int identificadorFigura, identificadorFisico;
    public CollisionObject ramaFisica;
    public float masa, elasticidad;
    float[] velocidades = new float[3];
    public float[] posiciones = new float[3];
    public int[] posAnteriorMilimetros = new int[3];
    public BranchGroup ramaVisible = new BranchGroup();
    public TransformGroup desplazamientoFigura = new TransformGroup();
    public RigidBody cuerpoRigido;
    public boolean adelante, atras, izquierda, derecha, caminando, corriendo, quieto;
    public boolean esMDL;
    ArrayList<CreacionMapas.Figura> listaObjetosFisicos;
    public BranchGroup conjunto;
    DiscreteDynamicsWorld mundoFisico;
    Matrix3f matrizRotacionPersonaje = new Matrix3f();
    ProjectExplosion juego;
    //Atributos opcionales para dotar a la figura de cierta inteligencia
    public Vector3f localizacionObjetivo;
    public int estadoFigura;                    //Dependiendo del estado de la figura, su entorno, y del juego, la figura tiene un comportamiento dado.
    public int[] estadoEntornoFigura;      //El entorno alrededoar de la figura podria descrbirse con m�s de un estado. Lloviendo y tengo poca energia
    public Figura objetivo;                      //El objetivo puede ser: localizar otra figura,
    float aceleracionMuscular;
    public boolean parar = true;

    public Figura(BranchGroup conjunto, ArrayList<Figura> listaObjetosFisicos, ProjectExplosion juego) {
        this.listaObjetosFisicos = listaObjetosFisicos;
        this.conjunto = conjunto;
        this.juego = juego;
        desplazamientoFigura.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        desplazamientoFigura.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        desplazamientoFigura.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        ramaVisible.setCapability(BranchGroup.ALLOW_DETACH);
    }

    public void crearPropiedades(float masa, float elasticidad, float dumpingLineal, float posX, float posY, float posZ, DiscreteDynamicsWorld mundoFisico) {
        this.mundoFisico = mundoFisico;
        //Creacion de un cuerpoRigido (o RigidBody) con sus propiedades fisicas 
        this.masa = masa;
        Transform groundTransform = new Transform();
        groundTransform.setIdentity();
        groundTransform.origin.set(new Vector3f(posX, posY, posZ));
        boolean isDynamic = (masa != 0f);
        Vector3f inerciaLocal = new Vector3f(0, 1, 0);
        if (isDynamic && !esMDL) {
            this.ramaFisica.getCollisionShape().calculateLocalInertia(masa, inerciaLocal);
        }
        DefaultMotionState EstadoDeMovimiento = new DefaultMotionState(groundTransform);
        RigidBodyConstructionInfo InformacionCuerpoR = new RigidBodyConstructionInfo(masa, EstadoDeMovimiento, this.ramaFisica.getCollisionShape(), inerciaLocal);
        InformacionCuerpoR.restitution = elasticidad;

        cuerpoRigido = new RigidBody(InformacionCuerpoR);
        cuerpoRigido.setActivationState(RigidBody.DISABLE_DEACTIVATION);
        cuerpoRigido.setDamping(dumpingLineal, 0.1f);   //A�ade mas (1) o menos  (0) "friccion del aire" al desplazarse/caer o rotar
        cuerpoRigido.setFriction(0.3f);
        //A�adiendo el cuerpoRigido al mundoFisico
        mundoFisico.addRigidBody(cuerpoRigido); // Add the body to the dynamics world
        identificadorFisico = mundoFisico.getNumCollisionObjects() - 1;

        //A�adiendo objetoVisual asociado al grafo de escena y a la lista de objetos fisicos visibles y situandolo
        conjunto.addChild(ramaVisible);
        this.listaObjetosFisicos.add(this);
        identificadorFigura = listaObjetosFisicos.size() - 1;

        //Presentacion inicial de la  figura visual asociada al cuerpo rigido
        Transform3D inip = new Transform3D();
        inip.set(new Vector3f(posX, posY, posZ));
        desplazamientoFigura.setTransform(inip);

        //Actualizacion de posicion. La rotacion se empezara a actualizar en el primer movimiento (ver final del metodo mostrar(rigidBody))
        this.posiciones[0] = posX;
        this.posiciones[1] = posY;
        this.posiciones[2] = posZ;
    }

    public void mostrar(RigidBody cuerpoRigido) {
        //Actualiacin de posicon y rotacion de la figura visual, en base a la reciente posicion/rotacion del cuerpo rigido
        Transform trans = new Transform();
        if (cuerpoRigido != null && cuerpoRigido.getMotionState() != null) {
            cuerpoRigido.getMotionState().getWorldTransform(trans);
            Quat4f orientacion = new Quat4f();
            cuerpoRigido.getOrientation(orientacion);
            Transform3D rot = new Transform3D(orientacion, new Vector3f((float) trans.origin.x, (float) trans.origin.y, (float) trans.origin.z), 1);
            desplazamientoFigura.setTransform(rot);

            //Actualizacion de Matriz de rotacion y posiciones
            rot.get(this.matrizRotacionPersonaje);
            this.posiciones[0] = trans.origin.x;
            this.posiciones[1] = trans.origin.y;
            this.posiciones[2] = trans.origin.z;
        }
    }

    public void actualizar() {
        //REGLAS DE MOVIMIENTO A CORTO PLAZO DE LA FIGURA DEPENDIENDO DE SU ESTADO, DEL ENTORNO Y DEL ESTADO DEL JUEGO
        if (localizacionObjetivo != null) {
            Vector3f direccion = new Vector3f(localizacionObjetivo.x - posiciones[0], 0f, localizacionObjetivo.z - posiciones[2]);
            direccion.normalize();                                                                           //El vector se normaliza con 1 para que indique solo la direccion.
            Vector3f fuerzaDePersecucion;
            fuerzaDePersecucion = new Vector3f(direccion.x * masa * aceleracionMuscular / 2f, 0, direccion.z * masa * aceleracionMuscular / 2f);  //Crea vector fuerza
            cuerpoRigido.applyCentralForce(fuerzaDePersecucion);
        }
    }

    public void asignarObjetivo(Figura Objetivo, float aceleracionMuscular) {
        this.objetivo = Objetivo;
        this.localizacionObjetivo = new Vector3f(this.objetivo.posiciones[0], this.objetivo.posiciones[1], this.objetivo.posiciones[2]);
        this.aceleracionMuscular = aceleracionMuscular;
    }

    public void asignarObjetivo(Vector3f localizacionObjetivo, float aceleracionMuscular) {
        this.localizacionObjetivo = localizacionObjetivo;
        this.aceleracionMuscular = aceleracionMuscular;
    }

    Vector3d conseguirDireccionFrontal() {
        Transform3D t3dPersonaje = new Transform3D();
        this.desplazamientoFigura.getTransform(t3dPersonaje);
        Transform3D copiat3dPersonaje = new Transform3D(t3dPersonaje);
        Transform3D t3dSonar = new Transform3D();
        t3dSonar.set(new Vector3f(0.0f, 0, 10f));
        copiat3dPersonaje.mul(t3dSonar);
        Vector3d posSonar = new Vector3d(0, 0, 0);
        copiat3dPersonaje.get(posSonar);
        Vector3f posPersonaje = new Vector3f(0, 0, 0);
        t3dPersonaje.get(posPersonaje);
        return new Vector3d(posSonar.x - posPersonaje.x, posSonar.y - posPersonaje.y, posSonar.z - posPersonaje.z);
    }
}