package proyecto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResolverConjuntiva {
    
    // Verifica si hay cláusulas pendientes por resolver
    public static boolean hayClausulasPorResolver(List<Clausula> clausulas) {
        for (Clausula c : clausulas) {
            if (c.clausulas.size() <= 1 && c.resueltoCon.size() < clausulas.size() - 1) {
                return true;
            }
        }
        return false;
    }

    // Encuentra dos cláusulas que pueden resolverse
    public static List<Clausula> encontrarClausulasPorResolver(List<Clausula> clausulas) {
        for (int i = 0; i < clausulas.size(); i++) {
            for (int j = 0; j < clausulas.size(); j++) {
                if (!clausulas.get(i).resueltoCon.contains(clausulas.get(j)) && i != j) { 
                    clausulas.get(i).agregarResueltoCon(clausulas.get(j));
                    clausulas.get(j).agregarResueltoCon(clausulas.get(i));
                    return List.of(clausulas.get(i), clausulas.get(j));
                }
            }
        }
        return null;
    }

    // Intenta resolver dos cláusulas y generar una nueva
    public static List<Clausula> resolver(Clausula clausula1, Clausula clausula2) {
        for (String c1 : clausula1.clausulas) {
            for (String c2 : clausula2.clausulas) {
                Map<String, String> sustitucion = unificar(c1, c2);
                if (sustitucion != null) {
                    System.out.println("Eliminando " + c1 + " y " + c2);
                    List<String> nuevaClausula = new ArrayList<>();
                    for (String l : clausula1.clausulas) {
                        if (!l.equals(c1)) nuevaClausula.add(sustituir(l, sustitucion));
                    }
                    for (String l : clausula2.clausulas) {
                        if (!l.equals(c2)) nuevaClausula.add(sustituir(l, sustitucion));
                    }
                    return List.of(new Clausula(nuevaClausula));
                }
            }
        }
        return new ArrayList<>();
    }

    // Intenta unificar dos literales
    private static Map<String, String> unificar(String literal1, String literal2) {
        String base1 = literal1.replaceAll("\\(.*\\)", "");
        String base2 = literal2.replaceAll("\\(.*\\)", "");
        if (!base1.equals(negacion(base2))) return null;

        if (!literal1.contains("(") && !literal2.contains("(")) {
            return new HashMap<>();
        }

        String argumentos1 = literal1.replaceAll(".*\\((.*)\\)", "$1");
        String argumentos2 = literal2.replaceAll(".*\\((.*)\\)", "$1");
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

    // Aplica sustituciones a un literal
    private static String sustituir(String literal, Map<String, String> sustitucion) {
        for (Map.Entry<String, String> entry : sustitucion.entrySet()) {
            literal = literal.replace(entry.getKey(), entry.getValue());
        }
        return literal;
    }

    // Genera la negación de un literal
    private static String negacion(String literal) {
        if (literal.charAt(0) == '-') {
            return literal.substring(1);
        } else {
            return "-" + literal;
        }
    }

    // Agrega una nueva cláusula a la lista
    public static void agregarClausula(List<Clausula> clausulas, Clausula nuevaClausula) {
        clausulas.add(nuevaClausula);
    }

    // Verifica si hay una contradicción en las cláusulas
    public static boolean esClausulaNula(List<Clausula> clausulas) {
        for (Clausula c1 : clausulas) {
            if (c1.clausulas.size() == 1) {
                for (Clausula c2 : clausulas) {
                    if (c2.clausulas.size() == 1 && c1.clausulas.get(0).equals(negacion(c2.clausulas.get(0)))) {
                        System.out.println("\n Se encontró una contradicción entre:");
                        System.out.println("   " + c1.clausulas);
                        System.out.println("   " + c2.clausulas);
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

        List<Clausula> clausulas = new ArrayList<>();
        for (String axioma : listaAxiomas) {
            clausulas.add(new Clausula(List.of(axioma.split(" ∨ ")))); 
        }

        System.out.println("\n🔹 Clausulas iniciales:");
        for (Clausula c : clausulas) {
            System.out.println("   " + c.clausulas);
        }

        while (hayClausulasPorResolver(clausulas)) {
            List<Clausula> clausulasPorResolver = encontrarClausulasPorResolver(clausulas);
            if (clausulasPorResolver != null) {
                System.out.println("\n🔹 Resolviendo:");
                System.out.println("   " + clausulasPorResolver.get(0).clausulas);
                System.out.println("   " + clausulasPorResolver.get(1).clausulas);

                List<Clausula> resultados = resolver(clausulasPorResolver.get(0), clausulasPorResolver.get(1));
                for (Clausula resultado : resultados) {
                    if (resultado.clausulas.size() > 0 && !clausulas.contains(resultado)) {
                        agregarClausula(clausulas, resultado);
                        System.out.println("Nueva cláusula generada: " + resultado.clausulas);
                    }
                }
            }

            if (esClausulaNula(clausulas)) {
                return true;
            }
        }

        System.out.println("\n No se pudo refutar la sentencia.");
        return false;
    }
}

