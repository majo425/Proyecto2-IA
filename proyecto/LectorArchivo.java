package proyecto;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LectorArchivo {
    public static class DatosArchivo {
        private final List<String> axiomas;
        private final String pregunta;

        public DatosArchivo(List<String> axiomas, String pregunta) {
            this.axiomas = axiomas;
            this.pregunta = pregunta;
        }

        public List<String> getAxiomas() {
            return axiomas;
        }

        public String getPregunta() {
            return pregunta;
        }
    }

    //Metodo de lectura del archivo
    public static DatosArchivo leerArchivo(String nombreArchivo) {
        List<String> axiomas = new ArrayList<>();
        String pregunta = null;

        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (!linea.isEmpty()) {
                    //Validar la linea con la sentencia de pregunta
                    if (linea.startsWith("¿") && linea.endsWith("?")) {
                        pregunta = linea.substring(1, linea.length() - 1);
                    } else {
                        axiomas.add(linea);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }

        return new DatosArchivo(axiomas, pregunta);
    }
}
