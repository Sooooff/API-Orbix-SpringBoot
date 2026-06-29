package com.example.orbixapi.model;

import com.example.orbixapi.dto.ReviewTagOption;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ReviewTagCatalog {

    private static final Map<ReviewTag, String> LABELS = Map.ofEntries(
            Map.entry(ReviewTag.EXCELENTE_ESTADO, "Excelente estado"),
            Map.entry(ReviewTag.MUY_LIMPIO, "Muy limpio"),
            Map.entry(ReviewTag.RECOMENDADO, "Recomendado"),
            Map.entry(ReviewTag.BUEN_ESTADO, "Buen estado"),
            Map.entry(ReviewTag.LIMPIO, "Limpio"),
            Map.entry(ReviewTag.BUENA_ATENCION, "Buena atención"),
            Map.entry(ReviewTag.ESTADO_ACEPTABLE, "Estado aceptable"),
            Map.entry(ReviewTag.REGULAR_LIMPIEZA, "Limpieza regular"),
            Map.entry(ReviewTag.POCO_LIMPIO, "Poco limpio"),
            Map.entry(ReviewTag.NO_COINCIDE_DESCRIPCION, "No coincide descripción"),
            Map.entry(ReviewTag.MAL_ESTADO, "Mal estado"),
            Map.entry(ReviewTag.MUY_SUCIO, "Muy sucio"),
            Map.entry(ReviewTag.NO_RECOMENDADO, "No recomendado"),
            Map.entry(ReviewTag.MUY_PUNTUAL, "Muy puntual"),
            Map.entry(ReviewTag.MUY_RESPETUOSO, "Muy respetuoso"),
            Map.entry(ReviewTag.PUNTUAL, "Puntual"),
            Map.entry(ReviewTag.RESPETUOSO, "Respetuoso"),
            Map.entry(ReviewTag.CUIDO_EL_VEHICULO, "Cuidó el vehículo"),
            Map.entry(ReviewTag.ENTREGA_ACEPTABLE, "Entrega aceptable"),
            Map.entry(ReviewTag.CUIDADO_REGULAR, "Cuidado regular"),
            Map.entry(ReviewTag.IMPUNTUAL, "Impuntual"),
            Map.entry(ReviewTag.CUIDADO_DEFICIENTE, "Cuidado deficiente"),
            Map.entry(ReviewTag.MUY_IMPUNTUAL, "Muy impuntual"),
            Map.entry(ReviewTag.DANOS_AL_VEHICULO, "Daños al vehículo"),
            Map.entry(ReviewTag.ANFITRION_AMABLE, "Anfitrión amable"),
            Map.entry(ReviewTag.RESPUESTA_RAPIDA, "Respuesta rápida"),
            Map.entry(ReviewTag.INSTRUCCIONES_CLARAS, "Instrucciones claras")
    );

    private static final Map<Integer, List<ReviewTag>> VEHICLE_TAGS = Map.of(
            5, List.of(ReviewTag.EXCELENTE_ESTADO, ReviewTag.MUY_LIMPIO, ReviewTag.RECOMENDADO),
            4, List.of(ReviewTag.BUEN_ESTADO, ReviewTag.LIMPIO, ReviewTag.BUENA_ATENCION),
            3, List.of(ReviewTag.ESTADO_ACEPTABLE, ReviewTag.REGULAR_LIMPIEZA),
            2, List.of(ReviewTag.POCO_LIMPIO, ReviewTag.NO_COINCIDE_DESCRIPCION),
            1, List.of(ReviewTag.MAL_ESTADO, ReviewTag.MUY_SUCIO, ReviewTag.NO_RECOMENDADO)
    );

    private static final Map<Integer, List<ReviewTag>> USER_TAGS = Map.of(
            5, List.of(ReviewTag.MUY_PUNTUAL, ReviewTag.MUY_RESPETUOSO, ReviewTag.RECOMENDADO),
            4, List.of(ReviewTag.PUNTUAL, ReviewTag.RESPETUOSO, ReviewTag.CUIDO_EL_VEHICULO),
            3, List.of(ReviewTag.ENTREGA_ACEPTABLE, ReviewTag.CUIDADO_REGULAR),
            2, List.of(ReviewTag.IMPUNTUAL, ReviewTag.CUIDADO_DEFICIENTE),
            1, List.of(ReviewTag.MUY_IMPUNTUAL, ReviewTag.DANOS_AL_VEHICULO)
    );

    private static final Map<Integer, String> VEHICLE_TITLES = Map.of(
            5, "¿Qué destacó del vehículo?",
            4, "¿Qué te gustó del vehículo?",
            3, "¿Cómo estuvo el vehículo?",
            2, "¿Qué no cumplió tus expectativas?",
            1, "¿Qué salió mal con el vehículo?"
    );

    private static final Map<Integer, String> USER_TITLES = Map.of(
            5, "¿Qué destacó del cliente?",
            4, "¿Cómo fue la experiencia con el cliente?",
            3, "¿Cómo se comportó el cliente?",
            2, "¿Qué aspectos del cliente mejorarían?",
            1, "¿Qué problema hubo con el cliente?"
    );

    private ReviewTagCatalog() {
    }

    public static List<ReviewTagOption> optionsFor(ReviewType type, int rating) {
        return tagsFor(type, rating).stream()
                .map(tag -> new ReviewTagOption(tag.name(), label(tag)))
                .toList();
    }

    public static Map<Integer, List<ReviewTagOption>> allVehicleOptions() {
        return toOptionsMap(VEHICLE_TAGS);
    }

    public static Map<Integer, List<ReviewTagOption>> allUserOptions() {
        return toOptionsMap(USER_TAGS);
    }

    public static Map<Integer, String> allVehicleTitles() {
        return Map.copyOf(VEHICLE_TITLES);
    }

    public static Map<Integer, String> allUserTitles() {
        return Map.copyOf(USER_TITLES);
    }

    public static String titleFor(ReviewType type, int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("La calificación debe estar entre 1 y 5");
        }
        return switch (type) {
            case VEHICLE -> VEHICLE_TITLES.get(rating);
            case USER -> USER_TITLES.get(rating);
        };
    }

    public static void validate(ReviewType type, int rating, List<ReviewTag> selected) {
        if (selected == null || selected.isEmpty()) {
            return;
        }
        Set<ReviewTag> allowed = Set.copyOf(tagsFor(type, rating));
        for (ReviewTag tag : selected) {
            if (!allowed.contains(tag)) {
                throw new IllegalArgumentException(
                        "El tag '" + tag.name() + "' no es válido para una calificación de " + rating + " estrellas"
                );
            }
        }
    }

    public static String label(ReviewTag tag) {
        return LABELS.getOrDefault(tag, tag.name());
    }

    private static List<ReviewTag> tagsFor(ReviewType type, int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("La calificación debe estar entre 1 y 5");
        }
        return switch (type) {
            case VEHICLE -> VEHICLE_TAGS.get(rating);
            case USER -> USER_TAGS.get(rating);
        };
    }

    private static Map<Integer, List<ReviewTagOption>> toOptionsMap(Map<Integer, List<ReviewTag>> source) {
        Map<Integer, List<ReviewTagOption>> result = new LinkedHashMap<>();
        for (int stars = 5; stars >= 1; stars--) {
            result.put(stars, optionsForTags(source.get(stars)));
        }
        return result;
    }

    private static List<ReviewTagOption> optionsForTags(List<ReviewTag> tags) {
        return tags.stream()
                .map(tag -> new ReviewTagOption(tag.name(), label(tag)))
                .toList();
    }
}
