package com.sucy.skill;

import org.junit.jupiter.api.Test;
import studio.magemonkey.fabled.Fabled;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.spi.ToolProvider;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guards against <a href="https://github.com/magemonkeystudio/fabled/issues/1897">#1897</a>. {@link Fabled}
 * extends {@link SkillAPI}, so a {@code Fabled.x(...)} call in {@code SkillAPI} whose target {@code Fabled}
 * does not declare itself compiles against the inherited {@code SkillAPI} method, which then calls itself
 * forever.
 */
class SkillAPIDelegationTest {

    private static final Pattern FABLED_CALL =
            Pattern.compile("// (?:Interface)?Method studio/magemonkey/fabled/Fabled\\.(\\w+):(\\S+)");

    @Test
    void everyFabledMethodSkillAPICalls_isDeclaredOnFabled() {
        Set<String> calls = fabledCallsInSkillAPI();
        // Make sure the bytecode scan works at all, so the check below cannot pass vacuously
        assertTrue(calls.contains("hasPlayerData:(Lorg/bukkit/OfflinePlayer;)Z"), "scan found: " + calls);

        Set<String> declared = Arrays.stream(Fabled.class.getDeclaredMethods())
                .map(SkillAPIDelegationTest::signature)
                .collect(Collectors.toSet());
        List<String> inherited = new ArrayList<>();
        for (String call : calls) {
            if (!declared.contains(call)) inherited.add(call);
        }
        assertEquals(List.of(), inherited,
                "SkillAPI calls Fabled methods that Fabled only inherits from SkillAPI");
    }

    private static String signature(Method method) {
        return method.getName() + ':'
                + MethodType.methodType(method.getReturnType(), method.getParameterTypes())
                .toMethodDescriptorString();
    }

    private static Set<String> fabledCallsInSkillAPI() {
        ToolProvider javap = ToolProvider.findFirst("javap")
                .orElseThrow(() -> new IllegalStateException("javap is not available"));
        URL classFile = SkillAPI.class.getResource("SkillAPI.class");
        assertNotNull(classFile);

        StringWriter out = new StringWriter();
        StringWriter err = new StringWriter();
        int exit = javap.run(new PrintWriter(out), new PrintWriter(err), "-c", "-p", classFile.toExternalForm());
        assertEquals(0, exit, err.toString());

        Set<String> calls   = new TreeSet<>();
        Matcher     matcher = FABLED_CALL.matcher(out.toString());
        while (matcher.find()) {
            calls.add(matcher.group(1) + ':' + matcher.group(2));
        }
        return calls;
    }
}
