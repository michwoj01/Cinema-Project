package pl.edu.agh.ii.cinemaProject.db.dto;

import java.util.List;
import java.util.Optional;

public record MovieFiltersDTO(
        Optional<Integer> minDuration,
        Optional<Integer> maxDuration,
        Optional<String> nameContains,
        Optional<Boolean> isRecommended
) {
    private boolean isFiltering(List<Optional<?>> optionals) {
        return optionals.stream().anyMatch(Optional::isPresent);
    }

    public boolean isFiltering() {
        return isFiltering(List.of(minDuration, maxDuration, nameContains, isRecommended));
    }
}

