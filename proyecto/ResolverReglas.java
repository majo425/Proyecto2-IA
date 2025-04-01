package proyecto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResolverReglas {
    // Verifica si hay reglas pendientes por resolver
    public static boolean hayReglasPorResolver(List<Regla> reglas) {
        //Recorre la lista de reglas
        for (Regla r : reglas) {
            if (r.reglas.size() <= 1 && r.derivadoDe.size() < reglas.size() - 1) {
                return true;
            }
        }
        return false;
    }

    // Encuentra dos reglas que pueden resolverse
    public static List<Regla> encontrarReglasPorResolver(List<Regla> reglas) {
        //Se recorren todas las combinaciones posibles de dos reglas en la lista
        for (int i = 0; i < reglas.size(); i++) {
            for (int j = 0; j < reglas.size(); j++) {
                //Se valida en derivadoDe que la regla no se haya resuelto con la actual, y se asegura que no se compare con si misma
                if (!reglas.get(i).derivadoDe.contains(reglas.get(j)) && i != j) {
                    //Se agregan las reglas a derivadoDe para que no se vuelvan a comparar en futuras iteraciones
                    reglas.get(i).agregarDerivacion(reglas.get(j));
                    reglas.get(j).agregarDerivacion(reglas.get(i));
                    //Se devuelven las reglas que no han sido resueltas
                    return List.of(reglas.get(i), reglas.get(j));
                }
            }
        }
        return null;
    }

    // Intenta resolver dos reglas y generar una nueva
    public static List<Regla> resolver(Regla regla1, Regla regla2) {
        //Busca dos reglas (condicionales) que puedan cancelarse usando unificación
        for (String c1 : regla1.reglas) {
            for (String c2 : regla2.reglas) {
                //Llamado al metodo de unificación para ver si se pueden cancelar y hace la sustitución (x->p)
                Map<String, String> sustitucion = unificar(c1, c2);
                if (sustitucion != null) {
                    //Si se pueden cancelar, se sustituyen los condicionales
                    System.out.println(" Resolviendo: " + regla1.reglas + " con " + regla2.reglas);

                    List<String> nuevaRegla = new ArrayList<>(); //Lista para la nueva regla con los condicionales que quedan

                    //Recorre las reglas originales y guarda en la lista aquellos condicionales que no fueron eliminados
                    for (String l : regla1.reglas) {
                        if (!l.equals(c1)) nuevaRegla.add(sustituir(l, sustitucion));
                    }
                    for (String l : regla2.reglas) {
                        if (!l.equals(c2)) nuevaRegla.add(sustituir(l, sustitucion));
                    }
                    System.out.println("Eliminando " + c1 + " y " + c2 );
                    System.out.println(" Nueva regla generada: " + nuevaRegla + "\n") ;

                    return List.of(new Regla(nuevaRegla));
                }
            }
        }
        return new ArrayList<>(); //Si no hay solución
    }

    // Intenta unificar dos condiciones
    private static Map<String, String> unificar(String condicion1, String condicion2) {
        //Extrae el predicado del condicional, eliminado lo que este entre parentesis
        String base1 = condicion1.replaceAll("\\(.*\\)", "");
        String base2 = condicion2.replaceAll("\\(.*\\)", "");
        //Valida que los predicados sean cancelables entre sí
        if (!base1.equals(negacion(base2))) return null;

        if (!condicion1.contains("(")) {
            return new HashMap<>();
        }
        //Extrae los argumentos del condicional, lo que este entre parentesis
        String argumentos1 = condicion1.replaceAll(".*\\((.*)\\)", "$1");
        String argumentos2 = condicion2.replaceAll(".*\\((.*)\\)", "$1");

        //Separa los argumentos en un array
        String[] args1 = argumentos1.split(",");
        String[] args2 = argumentos2.split(",");
        //Valida que tengan la misma cantidad de argumentos
        if (args1.length != args2.length) return null;
        //El map almacena las sustituciones
        Map<String, String> sustitucion = new HashMap<>();
        for (int i = 0; i < args1.length; i++) { //Comparar los argumentos uno por uno
            if (!args1[i].equals(args2[i])) { //Si no son iguales
                if (Character.isLowerCase(args1[i].charAt(0))) { //Si el argumento 1 es una variable
                    sustitucion.put(args1[i], args2[i]);
                } else if (Character.isLowerCase(args2[i].charAt(0))) { //Si el argumento 2 es una variable
                    sustitucion.put(args2[i], args1[i]);
                } else {
                    return null;
                }
            }
            //Si son iguales, ya están unificados
        }
        return sustitucion;
    }

    // Aplica sustituciones a una condición - Metodo para realizar la sustitución que se genera en la unificación
    private static String sustituir(String condicion, Map<String, String> sustitucion) {
        //Devuelve la variable a reemplazar y la constante asignada
        for (Map.Entry<String, String> entry : sustitucion.entrySet()) {
            //Reemplaza la variable por la constante
            condicion = condicion.replace(entry.getKey(), entry.getValue());
        }
        return condicion;
    }

    // Genera la negación de una condición - invierte el signo
    private static String negacion(String condicion) {
        //Si la condicion es negativa, lo pasa a positiva y viceversa
        if (condicion.charAt(0) == '-') {
            return condicion.substring(1);
        } else {
            return "-" + condicion;
        }
    }

    // Agrega una nueva regla a la lista
    public static void agregarRegla(List<Regla> reglas, Regla nuevaRegla) {
        reglas.add(nuevaRegla);
    }

    // Verifica si hay una contradicción en las reglas
    public static boolean esReglaNula(List<Regla> reglas) {
        //Verifica que ambas reglas solo tengan una condicion
        for (Regla r1 : reglas) {
            if (r1.reglas.size() == 1) {
                for (Regla r2 : reglas) {
                    if (r2.reglas.size() == 1 && r1.reglas.get(0).equals(negacion(r2.reglas.get(0)))) { //Si la clausula 1 es negación de la clausula 2
                        System.out.println("\n Se encontró una contradicción entre:");
                        System.out.println("   " + r1.reglas);
                        System.out.println("   " + r2.reglas);
                        return true; //Retorna verdadero porque se encontro una contradiccion
                    }
                }
            }
        }
        return false;
    }

    // Implementa la refutación para comprobar la insatisfacibilidad
    public static boolean comprobarContradiccion(List<String> axiomas, String sentencia) {
        System.out.println("\n🔹 Iniciando resolución por contradiccion...");
        //Crear una lista mutable a partir de axiomas
        List<String> listaAxiomas = new ArrayList<>(axiomas);  
        listaAxiomas.add(negacion(sentencia)); // Agregar la sentencia pregunta negada

        //Cada axioma se convierte en una regla
        List<Regla> reglas = new ArrayList<>();
        for (String axioma : listaAxiomas) {
            reglas.add(new Regla(List.of(axioma.split(" ∨ "))));
        }

        System.out.println("\n🔹 Reglas iniciales:");
        for (Regla r : reglas) {
            System.out.println("   " + r.reglas);
        }

        //Mientras haya reglas por resolver
        while (hayReglasPorResolver(reglas)) {
            //Encuentra dos reglas que puedan ser resueltas
            List<Regla> reglasPorResolver = encontrarReglasPorResolver(reglas);
            if (reglasPorResolver != null) {
                //Realiza la resolución
                List<Regla> resultados = resolver(reglasPorResolver.get(0), reglasPorResolver.get(1));

                for (Regla resultado : resultados) {
                    //Agrega las nuevas reglas a la lista, se asegura que no se agregue una cláusula ya existente
                    if (!resultado.reglas.isEmpty() && !reglas.contains(resultado)) {
                        agregarRegla(reglas, resultado);
                    }
                }
            }
            //Verifica si se encontro una contradiccion
            if (esReglaNula(reglas)) {
                return true;
            }
        }

        System.out.println("\n No se pudo refutar la sentencia.");
        return false;
    }
}
