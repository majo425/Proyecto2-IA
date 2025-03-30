package proyecto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResolverConjuntiva {

    //Método que verifica si hay cláusulas pendientes por resolver y que pueden resolverse con otras
   
    public static boolean hayClausulasPorResolver(List<Clausula> clausulas) {
        //Recorre la lista de clausulas
        for (Clausula c : clausulas) {
            //Ej: ¬Q(x) ∨ R(y) - 2 literales o P(x) - 1 literal
            //Si la clausula tiene 1 o menos literales
            //Si resueltoCon es menor que la cantidad de clausulas menos la actual, entonces la clausula no se ha terminado de resolver
            if (c.clausulas.size() <= 1 && c.resueltoCon.size() < clausulas.size() - 1) {
                return true;
            }
        }
        return false;
    }

    //Método que busca dos cláusulas que puedan ser resueltas entre spi
    public static List<Clausula> encontrarClausulasPorResolver(List<Clausula> clausulas) {
        //Se recorren todas las combinaciones posibles de dos cláusulas en la lista
        for (int i = 0; i < clausulas.size(); i++) { //Clausula 1
            for (int j = 0; j < clausulas.size(); j++) { //Clausula 2
                //Se valida en resueltoCon que la clausula no se haya resuelto con la actual, y se asegura que no se compare con si misma
                if (!clausulas.get(i).resueltoCon.contains(clausulas.get(j)) && i != j) { 
                    //Se agregan las cláusulas a resueltoCon para que no se vuelva a comparar en futuras iteraciones
                    clausulas.get(i).agregarResueltoCon(clausulas.get(j));
                    clausulas.get(j).agregarResueltoCon(clausulas.get(i));
                    //Se devuelven las cláusulas que no han sido resueltas
                    return List.of(clausulas.get(i), clausulas.get(j));
                }
            }
        }
        return null;
    }

    //Método para resolver dos cláusulas
    public static List<Clausula> resolver(Clausula clausula1, Clausula clausula2) {
        //Busca dos clausulas (liteales) que puedan cancelarse usando unificación
        for (String c1 : clausula1.clausulas) {
            for (String c2 : clausula2.clausulas) {
                //Llamado al metodo de unificación para ver si se pueden cancelar y hace la sustitución (x->p)
                Map<String, String> sustitucion = unificar(c1, c2);
                if (sustitucion != null) {
                    //Si se pueden cancelar, se sustituyen los literales
                    System.out.println("Eliminando " + c1 + " y " + c2);
                    List<String> nuevaClausula = new ArrayList<>(); //Lista para la nueva cláusula con los literales que quedan
                    //Recorre las clausulas originales y guarda en la lista auqellos literales que no fueron eliminados
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
        return new ArrayList<>(); //Si no hay solución
    }

    //Metodo de unificación
    private static Map<String, String> unificar(String literal1, String literal2) {
        //Extrae el predicado del literal, eliminado lo que este entre parentesis
        String base1 = literal1.replaceAll("\\(.*\\)", "");
        String base2 = literal2.replaceAll("\\(.*\\)", "");
        //Valida que los pedicados sean cancelables ente sí
        if (!base1.equals(negacion(base2))) return null;

        //Extrae los argumentos del liter, lo que este entre parentesis
        String argumentos1 = literal1.replaceAll(".*\\((.*)\\)", "$1");
        String argumentos2 = literal2.replaceAll(".*\\((.*)\\)", "$1");

        //Separa los argumentos en un array
        String[] args1 = argumentos1.split(",");
        String[] args2 = argumentos2.split(",");

        //Valida que tengan la misma cantidad de argumentos
        if (args1.length != args2.length) return null;

        //El map almacena las sustituciones
        Map<String, String> sustitucion = new HashMap<>();

        for (int i = 0; i < args1.length; i++) { //Comparar los argumentos uno por uno
            if (!args1[i].equals(args2[i])) {   //Si no son iguales
                if (Character.isLowerCase(args1[i].charAt(0))) { //Si el argumento 1 es una variable
                    sustitucion.put(args1[i], args2[i]);
                } else if (Character.isLowerCase(args2[i].charAt(0))) { //Si el argumento 2 es una variable
                    sustitucion.put(args2[i], args1[i]);
                } else { //Si no son variables
                    return null;
                }
            }
            //Si son iguales, ya están unificados
        }
        return sustitucion;
    }

    //Metodo para realizar la sustitución que se genera en la unificación
    private static String sustituir(String literal, Map<String, String> sustitucion) {
        //Devuelve la variable a reemplazar y la constante asignada
        for (Map.Entry<String, String> entry : sustitucion.entrySet()) {
            //Reemplaza la variable por la constante
            literal = literal.replace(entry.getKey(), entry.getValue());
        }
        return literal;
    }


    //Método que invierte el signo de un literal
    private static String negacion(String literal) {
        //Si el literal es negativo, lo pasa a positivo y viceversa
        if (literal.charAt(0) == '-') {
            return literal.substring(1);
        } else {
            return "-" + literal;
        }
    }

    //Método para agregar una nueva cláusula a la lista
    public static void agregarClausula(List<Clausula> clausulas, Clausula nuevaClausula) {
        clausulas.add(nuevaClausula);
    }

    //Método para verificar si hay una contradicción en una cláusula en la lista
    public static boolean esClausulaNula(List<Clausula> clausulas) {
        //Verifica que ambas clausulas solo tengan un literal
        for (Clausula c1 : clausulas) {
            if (c1.clausulas.size() == 1) {
                for (Clausula c2 : clausulas) {
                    if (c2.clausulas.size() == 1 && c1.clausulas.get(0).equals(negacion(c2.clausulas.get(0)))) { //Si la clausula 1 es negación de la clausula 2
                        System.out.println("\n Se encontró una contradicción entre:");
                        System.out.println("   " + c1.clausulas);
                        System.out.println("   " + c2.clausulas);
                        return true; //Retorna verdadero porque se encontro una contradiccion
                    }
                }
            }
        }
        return false; //Retorna falso porque no se encontro una contradiccion
    }

    //Método para realizar la refutación
    public static boolean refutacion(List<String> axiomas, String sentencia) {
        System.out.println("\n🔹 Iniciando resolución por refutación...");
        //Crear una lista mutable a partir de axiomas
        List<String> listaAxiomas = new ArrayList<>(axiomas);  
        listaAxiomas.add(negacion(sentencia)); // Agregar la sentencia pegunta negada

        //Cada axioma se convierte en una clausula
        List<Clausula> clausulas = new ArrayList<>();
        for (String axioma : listaAxiomas) {
            clausulas.add(new Clausula(List.of(axioma.split(" ∨ ")))); 
        }

        System.out.println("\n🔹 Clausulas iniciales:");
        for (Clausula c : clausulas) {
            System.out.println("   " + c.clausulas);
        }

        //Mientras haya cláusulas por resolver
        while (hayClausulasPorResolver(clausulas)) {
            //Encuentra dos cláusulas que puedan ser resueltas
            List<Clausula> clausulasPorResolver = encontrarClausulasPorResolver(clausulas);
            if (clausulasPorResolver != null) {
                System.out.println("\n🔹 Resolviendo:");
                System.out.println("   " + clausulasPorResolver.get(0).clausulas);
                System.out.println("   " + clausulasPorResolver.get(1).clausulas);

                //Realiza la resolución
                List<Clausula> resultados = resolver(clausulasPorResolver.get(0), clausulasPorResolver.get(1));

                for (Clausula resultado : resultados) {
                    //Agrega las nuevas cláusulas a la lista, se asegura que no se agregue una cláusula ya existente
                    if (resultado.clausulas.size() > 0 && !clausulas.contains(resultado)) {
                        agregarClausula(clausulas, resultado);
                        System.out.println("Nueva cláusula generada: " + resultado.clausulas);
                    }
                }
            }

            //Verifica si se encontro una contradiccion
            if (esClausulaNula(clausulas)) {
                return true;
            }
        }

        System.out.println("\n No se pudo refutar la sentencia.");
        return false;
    }
}