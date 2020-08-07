package com.telekom.timon.leanix.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Spelling {

    private static final String ABC = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static Map<String, Integer> dictionary = new HashMap<>();
    private static final String DICTIONARY_VALUES = "Tesla,Apple,Microsoft,CocaCola";
    private static String DARWIN_DICTIONARY_VALUES = "";

    /*public static void main(String[] args) {

        Stream.of(DICTIONARY_VALUES.toLowerCase().split(",")).forEach((word) -> {
            dictionary.compute(word, (k, v) -> v == null ? 1 : v + 1);
        });

        System.out.println("Correction for T-VPP(P): " + correct("T-VPP_PARTNERSHOP(P)"));
        System.out.println("Correction for Apptle: " + correct("Apple2"));
        System.out.println("Correction for Cocakola: " + correct("Cocakola"));
        System.out.println("Correction for Mikrosoft: " + correct("Mikrosoft"));
    }*/

    public static String getDarwinDictionaryValues() {
        return DARWIN_DICTIONARY_VALUES;
    }

    public static void setDarwinDictionaryValues(final String darwinDictionaryValues) {
        DARWIN_DICTIONARY_VALUES = darwinDictionaryValues;
    }

    public static Map<String, Integer> getDictionary() {
        return dictionary;
    }

    public static void setDictionary(final Map<String, Integer> dictionary) {
        Spelling.dictionary = dictionary;
    }

    private static Stream<String> getStringStream(String word) {
        Stream<String> deletes = IntStream.range(0, word.length())
                .mapToObj((i) -> word.substring(0, i) + word.substring(i + 1));

        Stream<String> replaces = IntStream.range(0, word.length()).boxed().flatMap((i) -> ABC.chars()
                .mapToObj((c) -> word.substring(0, i) + (char) c + word.substring(i + 1)));

        Stream<String> inserts = IntStream.range(0, word.length() + 1).boxed().flatMap((i) -> ABC.chars()
                .mapToObj((c) -> word.substring(0, i) + (char) c + word.substring(i)));

        Stream<String> transposes = IntStream.range(0, word.length() - 1)
                .mapToObj((i) -> word.substring(0, i) + word.substring(i + 1, i + 2) + word.charAt(i) + word.substring(i + 2));

        return Stream.of(deletes, replaces, inserts, transposes).flatMap((x) -> x);
    }


    private static Stream<String> edits1(final String word) {
        return getStringStream(word);
    }


    public static String correct(String word) {
        Optional<String> e1 = known(edits1(word)).max(Comparator.comparingInt(a -> dictionary.get(a)));

        if (e1.isPresent()) {
            return dictionary.containsKey(word) ? word : e1.get();
        }

        Optional<String> e2 = known(edits1(word).map(Spelling::edits1).flatMap((x) -> x))
                .max(Comparator.comparingInt(a -> dictionary.get(a)));

        return (e2.orElse(word));
    }


    private static Stream<String> known(Stream<String> words) {
        return words.filter((word) -> dictionary.containsKey(word));
    }


    public static void checkSpelling(final String appName) {
        //TODO: check if names are familiar and log it
        Spelling.setDarwinDictionaryValues(Spelling.getDarwinDictionaryValues() +
                appName + ",");

        Stream.of(Spelling.getDarwinDictionaryValues().toLowerCase().split(",")).forEach((word) -> {
            Spelling.getDictionary().compute(word, (k, v) -> v == null ? 1 : v + 1);
        });
    }
}
