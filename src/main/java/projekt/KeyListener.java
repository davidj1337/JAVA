package projekt;

/**
 * Jednoduchý callback na informovanie UI o počte zozbieraných kľúčov.
 */

public interface KeyListener {
    void keyCollected(int collectedKeys, int totalKeys);
}
