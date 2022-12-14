package me.petrolingus.unn.psr.opengl;

import me.petrolingus.unn.psr.ParticleSystemResearchApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {

    public static String loadShader(String shaderName) {
        try {
            URI resource = Objects.requireNonNull(ParticleSystemResearchApplication.class.getResource("shaders/" + shaderName)).toURI();
            return String.join("\n", Files.readAllLines(Paths.get(Objects.requireNonNull(resource))));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static String loadShaderV2(String shaderName) {
        try (Stream<String> stringStream = Files.lines(Paths.get(shaderName))) {
            return stringStream.collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> readAllLines(String fileName) throws Exception {
        List<String> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Class.forName(Utils.class.getName()).getResourceAsStream(fileName))))) {
            String line;
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
        }
        return list;
    }

    public static double valueMapper(double value, double min, double max) {
        return ((value - min) / (max - min));
    }

    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

}