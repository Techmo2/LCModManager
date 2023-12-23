package brad.tillmann;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;

import static java.time.temporal.ChronoUnit.SECONDS;

public class LethalCompanyModRepository {
    private static String jsonCatalogUrl = "https://thunderstore.io/c/lethal-company/api/v1/package/";
    private List<LethalCompanyMod> modCatalog;
    public LethalCompanyModRepository()
    {
        // Update package list
        modCatalog = Collections.emptyList();
        try {

            modCatalog = asyncGetModCatalog();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        // DEBUG
        // Print map
        LethalCompanyMod mod = modCatalog.get(0);
        System.out.println(mod.getName());
        System.out.println(mod.getOwner());
        System.out.println(mod.getRatingScore());
        System.out.println(mod.getVersions().size());
    }

    private List<LethalCompanyMod> readValueJackson(String content)
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            return objectMapper.readValue(content, new TypeReference<List<LethalCompanyMod>>(){});
        } catch (IOException ioe) {
            throw new CompletionException(ioe);
        }
    }

    private List<LethalCompanyMod> asyncGetModCatalog() throws Exception {
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
}
