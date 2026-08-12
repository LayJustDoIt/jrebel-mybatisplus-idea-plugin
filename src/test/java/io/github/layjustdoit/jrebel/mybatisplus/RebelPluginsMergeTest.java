package io.github.layjustdoit.jrebel.mybatisplus;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link JrebelMybatisPlusProgramPatcher#mergeRebelPlugins(String, String)}.
 *
 * <p>Verifies the four merge cases required by the specification:</p>
 * <ul>
 *   <li>Case 1: existing=null -> runtime only (handled in caller, not in merge)</li>
 *   <li>Case 2: existing=/a.jar -> /a.jar,runtime</li>
 *   <li>Case 3: existing already contains runtime -> idempotent in caller, merge should keep single entry</li>
 *   <li>Case 4: existing has old jr-mybatisplus-1.0.7.jar -> replaced with current runtime</li>
 * </ul>
 */
class RebelPluginsMergeTest {

    private static final String RUNTIME = "/plugins/jrebel-mybatisplus-idea-plugin/lib/jr-mybatisplus-1.0.8.jar";

    @Test
    void case2_existingSingleJar_appendsRuntime() {
        String existing = "/a.jar";
        String merged = JrebelMybatisPlusProgramPatcher.mergeRebelPlugins(existing, RUNTIME);
        assertEquals("/a.jar," + RUNTIME, merged);
    }

    @Test
    void case3_existingAlreadyContainsRuntime_keepsSingleEntry() {
        String existing = "/a.jar," + RUNTIME;
        String merged = JrebelMybatisPlusProgramPatcher.mergeRebelPlugins(existing, RUNTIME);
        // Merge should not duplicate runtime jar.
        long count = java.util.Arrays.stream(merged.split(","))
                .filter(s -> s.equals(RUNTIME))
                .count();
        assertEquals(1, count, "runtime jar should appear exactly once");
        assertTrue(merged.contains("/a.jar"));
    }

    @Test
    void case4_existingHasOldVersion_replacesWithCurrent() {
        String existing = "/a.jar,/old/jr-mybatisplus-1.0.7.jar";
        String merged = JrebelMybatisPlusProgramPatcher.mergeRebelPlugins(existing, RUNTIME);
        assertEquals("/a.jar," + RUNTIME, merged);
    }

    @Test
    void case4_soloOldVersion_replacedWithCurrent() {
        String existing = "/old/jr-mybatisplus-1.0.7.jar";
        String merged = JrebelMybatisPlusProgramPatcher.mergeRebelPlugins(existing, RUNTIME);
        assertEquals(RUNTIME, merged);
    }

    @Test
    void multipleExistingJars_preservesAllAndAppendsRuntime() {
        String existing = "/a.jar,/b.jar,/c.jar";
        String merged = JrebelMybatisPlusProgramPatcher.mergeRebelPlugins(existing, RUNTIME);
        assertEquals("/a.jar,/b.jar,/c.jar," + RUNTIME, merged);
    }

    @Test
    void multipleExistingJars_replacesOldMybatisplusEntry() {
        String existing = "/a.jar,/old/jr-mybatisplus-1.0.7.jar,/b.jar";
        String merged = JrebelMybatisPlusProgramPatcher.mergeRebelPlugins(existing, RUNTIME);
        assertEquals("/a.jar," + RUNTIME + ",/b.jar", merged);
    }
}
