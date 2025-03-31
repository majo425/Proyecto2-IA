package proyecto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResolverReglas {
    // Verifica si hay reglas pendientes por resolver
    public static boolean hayReglasPorResolver(List<Clausula> reglas) {
        for (Clausula r : reglas) {
            if (r.condiciones.size() <= 1 && r.derivadoDe.size() < reglas.size() - 1) {
                return true;
            }
        }
        return false;
    }

    // Encuentra dos reglas que pueden resolverse
    public static List<Clausula> encontrarReglasPorResolver(List<Clausula> reglas) {
        for (int i = 0; i < reglas.size(); i++) {
            for (int j = 0; j < reglas.size(); j++) {
                if (!reglas.get(i).derivadoDe.contains(reglas.get(j)) && i != j) { 
                    reglas.get(i).agregarDerivacion(reglas.get(j));
                    reglas.get(j).agregarDerivacion(reglas.get(i));
                    return List.of(reglas.get(i), reglas.get(j));
                }
            }
        }
        return null;
    }

    // Intenta resolver dos reglas y generar una nueva
    public static List<Clausula> resolver(Clausula regla1, Clausula regla2) {
        for (String c1 : regla1.condiciones) {
            for (String c2 : regla2.condiciones) {
                Map<String, String> sustitucion = unificar(c1, c2);
                if (sustitucion != null) {
                    System.out.println("Eliminando " + c1 + " y " + c2);
                    List<String> nuevaRegla = new ArrayList<>();
                    for (String l : regla1.condiciones) {
                        if (!l.equals(c1)) nuevaRegla.add(sustituir(l, sustitucion));
                    }
                    for (String l : regla2.condiciones) {
                        if (!l.equals(c2)) nuevaRegla.add(sustituir(l, sustitucion));
                    }
                    return List.of(new Clausula(nuevaRegla));
                }
            }
        }
        return new ArrayList<>();
    }

    // Intenta unificar dos condiciones
    private static Map<String, String> unificar(String condicion1, String condicion2) {
        String base1 = condicion1.replaceAll("\\(.*\\)", "");
        String base2 = condicion2.replaceAll("\\(.*\\)", "");
        if (!base1.equals(negacion(base2))) return null;

        if (!condicion1.contains("(")) {
            return new HashMap<>();
        }

        String argumentos1 = condicion1.replaceAll(".*\\((.*)\\)", "$1");
        String argumentos2 = condicion2.replaceAll(".*\\((.*)\\)", "$1");
        String[] args1 = argumentos1.split(",");
        String[] args2 = argumentos2.split(",");
        if (args1.length != args2.length) return null;

        Map<String, String> sustitucion = new HashMap<>();
        for (int i = 0; i < args1.length; i++) {
            if (!args1[i].equals(args2[i])) {
                if (Character.isLowerCase(args1[i].charAt(0))) {
                    sustitucion.put(args1[i], args2[i]);
                } else if (Character.isLowerCase(args2[i].charAt(0))) {
                    sustitucion.put(args2[i], args1[i]);
                } else {
                    return null;
                }
            }
        }
        return sustitucion;
    }

    // Aplica sustituciones a una condición
    private static String sustituir(String condicion, Map<String, String> sustitucion) {
        for (Map.Entry<String, String> entry : sustitucion.entrySet()) {
            condicion = condicion.replace(entry.getKey(), entry.getValue());
        }
        return condicion;
    }

    // Genera la negación de una condición
    private static String negacion(String condicion) {
        if (condicion.charAt(0) == '-') {
            return condicion.substring(1);
        } else {
            return "-" + condicion;
        }
    }

    // Agrega una nueva regla a la lista
    public static void agregarRegla(List<Clausula> reglas, Clausula nuevaRegla) {
        reglas.add(nuevaRegla);
    }

    // Verifica si hay una contradicción en las reglas
    public static boolean esReglaNula(List<Clausula> reglas) {
        for (Clausula r1 : reglas) {
            if (r1.condiciones.size() == 1) {
                for (Clausula r2 : reglas) {
                    if (r2.condiciones.size() == 1 && r1.condiciones.get(0).equals(negacion(r2.condiciones.get(0)))) {
                        System.out.println("\n Se encontró una contradicción entre:");
                        System.out.println("   " + r1.condiciones);
                        System.out.println("   " + r2.condiciones);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Implementa la refutación para comprobar la insatisfacibilidad
    public static boolean refutacion(List<String> axiomas, String sentencia) {
        System.out.println("\n🔹 Iniciando resolución por refutación...");
        List<String> listaAxiomas = new ArrayList<>(axiomas);  
        listaAxiomas.add(negacion(sentencia));

        List<Clausula> reglas = new ArrayList<>();
        for (String axioma : listaAxiomas) {
            reglas.add(new Clausula(List.of(axioma.split(" ∨ ")))); 
        }

        System.out.println("\n🔹 Reglas iniciales:");
        for (Clausula r : reglas) {
            System.out.println("   " + r.condiciones);
        }

        while (hayReglasPorResolver(reglas)) {
            List<Clausula> reglasPorResolver = encontrarReglasPorResolver(reglas);
            if (reglasPorResolver != null) {
                System.out.println("\n🔹 Resolviendo:");
                System.out.println("   " + reglasPorResolver.get(0).condiciones);
                System.out.println("   " + reglasPorResolver.get(1).condiciones);

                List<Clausula> resultados = resolver(reglasPorResolver.get(0), reglasPorResolver.get(1));
                for (Clausula resultado : resultados) {
                    if (!resultado.condiciones.isEmpty() && !reglas.contains(resultado)) {
                        agregarRegla(reglas, resultado);
                        System.out.println("Nueva regla generada: " + resultado.condiciones);
                    }
                }
            }

            if (esReglaNula(reglas)) {
                return true;
            }
        }

        System.out.println("\n No se pudo refutar la sentencia.");
        return false;
    }
}

