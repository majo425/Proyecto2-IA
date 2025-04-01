package proyecto;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        //Lectura del archivo con las sentencias
        String nombreArchivo = "proyecto/casoWestCriminalFNC.txt";
        LectorArchivo.DatosArchivo datos = LectorArchivo.leerArchivo(nombreArchivo);

        //Valida que halla una sentencia pregunta
        if (datos.getPregunta() == null) {
            System.out.println("No se encontró una pregunta en el archivo.");
            return;
        }

        List<String> axiomas = datos.getAxiomas();
        String pregunta = datos.getPregunta(); //sentencia

        System.out.println("Axiomas en la lista:");
        for (String axioma : axiomas) {
            System.out.println(axioma);
        }

        System.out.println("\nSentencia pregunta: ¿" + pregunta + " ?");
        System.out.println("\n ---------------- \n");

        //Imprime la solucion
        System.out.println(ResolverReglas.comprobarContradiccion(axiomas, pregunta));
    }
}
