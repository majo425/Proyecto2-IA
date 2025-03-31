
package proyecto;
import java.util.ArrayList;
import java.util.List;

public class Clausula {
    List<String> condiciones; // Lista de condiciones
    List<Clausula> derivadoDe; // Lista de reglas de las que se deriva

    public Clausula(List<String> condiciones) {
        this.condiciones = condiciones;
        this.derivadoDe = new ArrayList<>();
    }

    // Método para agregar reglas utilizadas en la derivación
    public void agregarDerivacion(Clausula otraDerivacion) {
        this.derivadoDe.add(otraDerivacion);
    }
}
