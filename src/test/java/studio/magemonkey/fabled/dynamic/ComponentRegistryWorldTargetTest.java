package studio.magemonkey.fabled.dynamic;

import org.junit.jupiter.api.Test;
import studio.magemonkey.fabled.dynamic.target.WorldTarget;
import studio.magemonkey.fabled.testutil.MockedTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * WorldTarget existed but was never registered as a usable target component - covers
 * that it's now selectable like any other target.
 */
public class ComponentRegistryWorldTargetTest extends MockedTest {
    @Test
    void worldTarget_isRegisteredAsTargetComponent() {
        String key = new WorldTarget().getKey();

        assertTrue(ComponentRegistry.getComponents()
                .get(ComponentType.TARGET)
                .containsKey(key));
    }
}
