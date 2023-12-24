package brad.tillmann;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.SECONDS;

public class LCModCatalog {
    private static final String jsonCatalogUrl = "https://thunderstore.io/c/lethal-company/api/v1/package/";
    private static final Map<String, Integer> searchTermWeightMap = Map.of(
            "name", 10,
            "fullName", 5,
            "owner", 3,
            "categories", 2
    );
    private Map<String, LCMod> modDescriptorsByFullName;

    private LCModCatalog() {
        updateModCatalog();
    }

    public static LCModCatalog getInstance() {
        return InitializationOnDemandClassHolder.instance;
    }

    public void updateModCatalog() {
        // Update package list
        modDescriptorsByFullName = Collections.emptyMap();
        try {
            modDescriptorsByFullName = downloadModCatalog().stream()
                    .collect(Collectors.toMap(
                            LCMod::getFullName,
                            descriptor -> descriptor,
                            (prev, next) -> next,
                            HashMap::new));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will search mod descriptors from the catalog for terms given in the search string.
     * The search string will be split into individual search terms using whitespace.
     * It will then attempt to match each search term to one of a number of fields for each mod descriptor.
     * Matches for different fields will add different weights to the result. The result weight will determine its order in the returned list.
     * These fields are: name, fullName, owner, categories
     *
     * @param searchString
     * @return
     */
    public List<LCMod> search(String searchString) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        // Create search terms
        String[] searchTerms = searchString.toLowerCase().split("\\s+");
        List<Pair<LCMod, Integer>> results = new LinkedList<>();

        // Match terms for each mod descriptor
        for (Map.Entry<String, LCMod> modDescriptorEntry : modDescriptorsByFullName.entrySet()) {
            LCMod descriptor = modDescriptorEntry.getValue();
            int weight = 0;

            // Calculate match weight
            for (String searchTerm : searchTerms) {
                for (String field : searchTermWeightMap.keySet()) {
                    int fieldWeight = searchTermWeightMap.get(field);

                    if (StringUtils.equals(field, "categories")) {
                        for (String category : descriptor.getCategories()) {
                            if (StringUtils.contains(category.toLowerCase(), searchTerm.toLowerCase()))
                                weight += fieldWeight;
                        }
                    } else {
                        String fieldValue = BeanUtils.getProperty(descriptor, field);
                        if (StringUtils.contains(fieldValue.toLowerCase(), searchTerm.toLowerCase()))
                            weight += fieldWeight;
                    }
                }
            }

            // Add search result entry
            if (weight > 0)
                results.add(Pair.of(descriptor, weight));
        }

        // Sort by weight
        results.sort(Comparator.comparing(p -> -p.getRight()));
        // Pack into list and return
        return results.stream()
                .map(Pair::getLeft)
                .collect(Collectors.toList());
    }

    public LCMod getModDescriptorByFullName(String fullName) {
        return modDescriptorsByFullName.get(fullName);
    }

    private List<LCMod> readValueJackson(String content) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            return objectMapper.readValue(content, new TypeReference<List<LCMod>>() {
            });
        } catch (IOException ioe) {
            throw new CompletionException(ioe);
        }
    }

    private List<LCMod> downloadModCatalog() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(jsonCatalogUrl))
                .timeout(Duration.of(10, SECONDS))
                .GET()
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::readValueJackson)
                .get();
    }

    private static class InitializationOnDemandClassHolder {
        private static final LCModCatalog instance = new LCModCatalog();

    }
}
