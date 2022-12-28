package pl.edu.agh.ii.cinemaProject.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.vavr.control.Either;
import io.vavr.control.Try;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;

public class JsonLoader {
    public static <T> Either<Throwable, T> loadJsonResource(URL resourceName, TypeToken<T> tClass) {
        Gson gson = new Gson();

        return Try.of(() -> Files.readAllLines(new File(resourceName.toURI()).toPath()))
                .map((lines) -> {
                    StringBuilder ret = new StringBuilder();
                    for (var line : lines) {
                        ret.append(line);
                    }
                    return ret.toString();
                })
                .flatMap((jsonInput) -> Try.of(() -> gson.fromJson(jsonInput, tClass)))
                .toEither();
    }

}
