package proyecto;
import java.util.ArrayList;
import java.util.List;

class Clausula {
    List<String> clausulas; //Lista de clausulas
    List<Clausula> resueltoCon; //Lista de clausulas que se resolvieron

    public Clausula(List<String> clausulas) {
        this.clausulas = clausulas;
        this.resueltoCon = new ArrayList<>();
    }

    //Metodo para agregar clausulas que se usan para resolver
    public void agregarResueltoCon(Clausula otra) {
        this.resueltoCon.add(otra);
    }
}