package com.sucy.skill.testutil;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DependencyResolver {
    private static Logger       log          = LoggerFactory.getLogger(DependencyResolver.class);
    private static List<String> repositories =
            List.of("https://s01.oss.sonatype.org/content/repositories/snapshots/", "https://repo1.maven.org/maven2");

    public static File resolve(String dependency) throws FileNotFoundException {
        log.info("Attempting to resolve dependency " + dependency);
        String[] pieces   = dependency.split(":");
        String   userHome = FileUtils.getUserDirectoryPath();
        String path = userHome + "/.m2/repository/" + (pieces[0] + "/" + pieces[1]).replace('.', '/')
                + "/" + pieces[2] + "/" + pieces[1] + "-" + pieces[2] + ".jar";
        File file = new File(path);
        if (file.exists()) {
            log.info("Found " + dependency + " locally!");
            return file;
        }
        log.info("Could not find " + dependency + " locally. Searching repos.");
        return downloadArtifact(pieces[0], pieces[1], pieces[2]);
    }

    private static File downloadArtifact(String groupId, String artifact, String version) throws FileNotFoundException {
        String url = findArtifactUrl(groupId, artifact, version);
        log.info("Downloading " + artifact + " from " + url);
        try (BufferedInputStream inputStream = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream fileOS = new FileOutputStream(artifact + ".jar")) {
            byte data[] = new byte[1024];
            int  byteContent;
            while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                fileOS.write(data, 0, byteContent);
            }

            log.info("Download complete");
            return new File(artifact + ".jar");
        } catch (IOException e) {
            // handles IO exceptions
            throw new RuntimeException("Could not fetch dependency " + artifact);
        }
    }

    private static String findArtifactUrl(String groupId, String artifact, String version) throws
            FileNotFoundException {
        for (String rep : repositories) {
            StringBuilder html       = new StringBuilder();
            String        repository = rep + (groupId + "/" + artifact).replace('.', '/') + "/" + version + "/";
            try (BufferedReader read = new BufferedReader(new InputStreamReader(new URL(repository).openStream()))) {
                String line;
                while ((line = read.readLine()) != null) {
                    html.append(line);
                }
            } catch (IOException e) {
                // handles IO exceptions
                continue;
            }

            if (html.length() == 0) return null;

            String text = html.toString();
            Pattern pat = Pattern.compile(
                    "<a href=\"("
                            + (repository + artifact + "-" + version.replace("-SNAPSHOT", ""))
                            .replace("/", "\\/")
                            .replace(".", "\\.")
                            + "-?[^>]*?(?<!sources)(?<!javadocs)"
                            + "\\.jar)\">");
            Matcher mat = pat.matcher(text);
            String  url = "NO_URL";
            while (mat.find()) {
                url = mat.group(1);
            }
            if (!url.equals("NO_URL"))
                return url;
        }

        throw new FileNotFoundException("Couldn't locate " + groupId + ":" + artifact + ":" + version);
    }
}
