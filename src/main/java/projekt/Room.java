package projekt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Jedna „miestnosť“ v rámci levelu.
 * Používa fronty na pridanie/odobranie entít (entitiesToAdd/Remove)
 */

public class Room {

    public final List<DrawableSimulable> entities = new ArrayList<>();

    public final Collection<DrawableSimulable> entitiesToAdd = new LinkedList<>();
    public final Collection<DrawableSimulable> entitiesToRemove = new LinkedList<>();

    public void add(DrawableSimulable drawableSimulable) {
        entitiesToAdd.add(drawableSimulable);
    }

    public void remove(DrawableSimulable drawableSimulable) {
        entitiesToRemove.add(drawableSimulable);
    }

    // flush() sa volá na konci update kroku – až tu reálne pridáme/odoberieme entity.
    // Počas simulácie tak môžeme bezpečne iterovať cez entities bez toho, aby sme menili kolekciu.
    public void flush() {
        entities.removeAll(entitiesToRemove);
        entities.addAll(entitiesToAdd);
        entitiesToAdd.clear();
        entitiesToRemove.clear();
    }
}
