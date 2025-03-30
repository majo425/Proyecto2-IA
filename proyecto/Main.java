package proyecto;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        //Lectura del archivo con las sentencias
        String nombreArchivo = "proyecto/casoSinVariables.txt";
        LectorArchivo.DatosArchivo datos = LectorArchivo.leerArchivo(nombreArchivo);

        //Valida que halla una sentencia pregunta
        if (datos.getPregunta() == null) {
            System.out.println("No se encontró una pregunta en el archivo.");
            return;
        }

        List<String> axiomas = datos.getAxiomas();
        String sentencia = datos.getPregunta();

        System.out.println("Axiomas en la lista:");
        for (String axioma : axiomas) {
            System.out.println(axioma);
        }

        System.out.println("\nSentencia pregunta: ¿" + sentencia + " ?");
        System.out.println("\n ---------------- \n");

        //Imprime la solucion
        System.out.println(ResolverConjuntiva.refutacion(axiomas, sentencia));
    }
}
