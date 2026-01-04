package lab;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Jednoduchý cache manažér pre obrázky (Image), aby sa pri každom frame nenačítavali zo zdrojov znova.
 * Kľúčom je cesta k resource súboru (napr. "/tilemap.png").
 */

public final class ResourceManager {
    private static final Map<String, Image> IMAGES = new HashMap<>();

    private ResourceManager() {
        //nič
    }

    public static Image getImage(Class<?> clazz, String name) {
        // Cache mapuje názov resource -> Image. Pri opakovanom volaní sa vráti už načítaný obrázok.
        if (IMAGES.containsKey(name)) {
            return IMAGES.get(name);
        }
        try {
            if (clazz.getResource(name) == null) {
                System.err.println("Image not found: " + name);
                return null;
            }
            Image img = new Image(Objects.requireNonNull(clazz.getResourceAsStream(name)));
            IMAGES.put(name, img);
            return img;
        } catch (Exception e) {
            System.err.println("Error loading image: " + name);
            e.printStackTrace();
            return null;
        }
    }
}
