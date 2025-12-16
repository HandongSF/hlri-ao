package edu.handong.csee.isel.ao.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class ConfigExtractor {
    protected JsonElement json;

    public ConfigExtractor(Path config) throws IOException {
        json = JsonParser.parseString(Files.readString(config));
    }
}
