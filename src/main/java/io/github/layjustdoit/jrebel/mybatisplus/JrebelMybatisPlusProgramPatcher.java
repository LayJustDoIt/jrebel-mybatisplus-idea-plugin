package io.github.layjustdoit.jrebel.mybatisplus;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.JavaProgramPatcher;
import com.intellij.openapi.diagnostic.Logger;

import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Automatically injects {@code -Drebel.plugins=<bundled-runtime-jar>} into Java
 * run configurations before launch, so users do not need to manually configure
 * VM arguments.
 *
 * <p>Multiple {@code rebel.plugins} entries are merged using comma {@code ,}
 * separator, confirmed via JRebel official documentation.</p>
 *
 * <p>Based on the original jrebel-mybatisplus runtime by suchu
 * (https://github.com/sweetink). See THIRD_PARTY_NOTICES.md for details.</p>
 *
 * @author LayJustDoIt (https://github.com/LayJustDoIt)
 */
public class JrebelMybatisPlusProgramPatcher extends JavaProgramPatcher {

    private static final Logger LOG = Logger.getInstance(JrebelMybatisPlusProgramPatcher.class);
    private static final String PROP_NAME = "rebel.plugins";
    private static final String LOG_PREFIX = "[MyBatis-Plus Reload] ";

    /**
     * Marker class resource that exists inside the bundled runtime jar
     * (online.githuboy.jrebel.mybatisplus.MybatisPlusPlugin). Used only as a
     * resource URL anchor to locate the runtime jar via ClassLoader.getResource.
     *
     * <p>Intentionally NOT loaded via Class.forName / loadClass — we only read
     * the resource URL, never initialize the JRebel plugin class.</p>
     */
    private static final String RUNTIME_MARKER_RESOURCE =
            "online/githuboy/jrebel/mybatisplus/MybatisPlusPlugin.class";

    @Override
    public void patchJavaParameters(Executor executor, RunProfile configuration, JavaParameters javaParameters) {
        // Only JRebel executors (JRebel Run / JRebel Debug).
        if (!executor.getId().contains("JRebel")) {
            return;
        }

        Path runtimeJarPath = findBundledRuntimeJar();
        if (runtimeJarPath == null) {
            LOG.warn(LOG_PREFIX + "bundled runtime jar not found, skipping -Drebel.plugins injection");
            return;
        }

        ParametersList vmParams = javaParameters.getVMParametersList();
        String runtimeJarStr = runtimeJarPath.toString();
        String existing = vmParams.getPropertyValue(PROP_NAME);

        if (existing == null) {
            // Case 1: No existing -Drebel.plugins -> create it
            vmParams.addProperty(PROP_NAME, runtimeJarStr);
            LOG.debug(LOG_PREFIX + "Injected JRebel runtime plugin: " + runtimeJarStr);
        } else if (existing.contains(runtimeJarStr)) {
            // Case 3: Already contains our exact jar path -> skip (idempotent)
        } else {
            // Case 2 & 4: Exists but doesn't contain our jar -> merge
            String merged = mergeRebelPlugins(existing, runtimeJarStr);
            vmParams.getList().removeIf(p -> p.startsWith("-D" + PROP_NAME + "="));
            vmParams.addProperty(PROP_NAME, merged);
            LOG.debug(LOG_PREFIX + "Injected JRebel runtime plugin: " + runtimeJarStr);
        }
    }

    /**
     * Merges the existing rebel.plugins value with our runtime jar.
     *
     * <p>Visible for testing.</p>
     *
     * <ul>
     *   <li>Replaces any old jr-mybatisplus-*.jar with the current version (Case 4)</li>
     *   <li>Appends our jar if no jr-mybatisplus entry exists (Case 2)</li>
     *   <li>Preserves all other plugin entries</li>
     * </ul>
     */
    static String mergeRebelPlugins(String existing, String runtimeJarPath) {
        String[] parts = existing.split(",");
        List<String> result = new ArrayList<>();
        boolean replaced = false;

        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.isEmpty()) continue;

            if (trimmed.contains("jr-mybatisplus-") && trimmed.endsWith(".jar")) {
                result.add(runtimeJarPath);
                replaced = true;
            } else {
                result.add(trimmed);
            }
        }

        if (!replaced) {
            result.add(runtimeJarPath);
        }

        return String.join(",", result);
    }

    /**
     * Finds the bundled runtime jar by locating the marker resource
     * ({@code online/githuboy/jrebel/mybatisplus/MybatisPlusPlugin.class}) via
     * the plugin ClassLoader, then resolving the enclosing jar file.
     *
     * <p>Why not ProtectionDomain.getCodeSource(): under IntelliJ's
     * PluginClassLoader, getProtectionDomain().getCodeSource() returns null,
     * so the runtime jar cannot be located that way.</p>
     *
     * <p>Safety constraints:
     * <ul>
     *   <li>No {@code Class.forName} / {@code loadClass} — only {@code getResource}</li>
     *   <li>Does not initialize {@code MybatisPlusPlugin}</li>
     *   <li>No {@code PluginManagerCore.getPlugin} (would be internal API)</li>
     *   <li>No hardcoded paths</li>
     * </ul>
     * </p>
     */
    private Path findBundledRuntimeJar() {
        ClassLoader classLoader = JrebelMybatisPlusProgramPatcher.class.getClassLoader();
        URL resource = classLoader.getResource(RUNTIME_MARKER_RESOURCE);

        if (resource == null) {
            return null;
        }

        try {
            URLConnection connection = resource.openConnection();

            // Preferred: standard jar URL -> JarURLConnection
            if (connection instanceof JarURLConnection) {
                URL jarFileUrl = ((JarURLConnection) connection).getJarFileURL();
                Path runtimeJar = Paths.get(jarFileUrl.toURI()).toAbsolutePath().normalize();
                return Files.isRegularFile(runtimeJar) ? runtimeJar : null;
            }

            // Fallback: parse "!/" separator from URL external form.
            String external = resource.toExternalForm();
            int separator = external.indexOf("!/");
            if (separator > 0) {
                String jarUrl = external.substring(0, separator);
                if (jarUrl.startsWith("jar:")) {
                    jarUrl = jarUrl.substring(4);
                }
                Path runtimeJar = Paths.get(URI.create(jarUrl)).toAbsolutePath().normalize();
                return Files.isRegularFile(runtimeJar) ? runtimeJar : null;
            }

            LOG.warn(LOG_PREFIX + "unsupported runtime resource URL=" + external);
            return null;
        } catch (Exception e) {
            LOG.warn(LOG_PREFIX + "failed to resolve bundled runtime jar", e);
            return null;
        }
    }
}
