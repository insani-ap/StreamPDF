package com.example.bin.pdfbin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@RestController
public class GeneralController {
    @GetMapping("/getPDF")
    public byte[] altRoute() throws IOException {
        Path path = new File(Objects.requireNonNull(getClass().getResource("/public/example.pdf")).getFile()).toPath();
        return Files.readAllBytes(path);
    }
}
