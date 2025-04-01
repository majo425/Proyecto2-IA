package proyecto;
import java.util.ArrayList;
import java.util.List;

public class Regla {
    List<String> reglas; // Lista de reglas
    List<Regla> derivadoDe; // Lista de reglas de las que se deriva

    public Regla(List<String> reglas) {
        this.reglas = reglas;
        this.derivadoDe = new ArrayList<>();
    }

    // Método para agregar reglas utilizadas en la derivación
    public void agregarDerivacion(Regla otraDerivacion) {
        this.derivadoDe.add(otraDerivacion);
    }
}
