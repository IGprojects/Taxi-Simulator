package core;

/**
 * @class Pair
 * @brief Classe genèrica que representa un parell de valors (clau, valor).
 *
 * @author Dídac Gros Labrador
 * @version 2025.05.15
 */
public class Pair<K, V> {
    private final K key; /// < Clau del parell.
    private final V value; /// < Valor del parell.

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * @pre Cert.
     * @post Retorna la clau del parell.
     * 
     * @return Clau del parell.
     */
    public K getKey() {
        return key;
    }

    /**
     * @pre Cert.
     * @post Retorna el valor del parell.
     * 
     * @return Valor del parell.
     */
    public V getValue() {
        return value;
    }

    /**
     * @pre Cert.
     * @post Retorna una cadena de text que representa el parell.
     * 
     * @return Cadena de text que representa el parell.
     */
    @Override
    public String toString() {
        return "(" + key + ", " + value + ")";
    }

    /**
     * @pre Cert.
     * @post Retorna si el parell és igual a un altre objecte.
     * 
     * @return Cert si el parell és igual a l'altre objecte, fals en cas contrari.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Pair))
            return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return key.equals(pair.key) && value.equals(pair.value);
    }

    /**
     * @pre Cert.
     * @post Retorna el codi hash del parell.
     * 
     * @return Codi hash del parell.
     */
    @Override
    public int hashCode() {
        return key.hashCode() ^ value.hashCode();
    }
}
