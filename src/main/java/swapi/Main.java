package swapi;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;

public class Main {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {



    RestTemplate restClient = new RestTemplate();

    List<String> characterList = new ArrayList<>();
    
    String pagedUrl = "https://swapi.dev/api/people/?page=1";
    
    HttpHeaders headers = new HttpHeaders();
    //headers.add("User-Agent", "Testing");

    HttpEntity<String> entity = new HttpEntity<>(headers);
    
    while (pagedUrl != null) {
      /**
       * There is a bug in the API. It returns urls with http but it is only accessible by https.
       */
      pagedUrl = pagedUrl.replaceAll("http:", "https:");

      ResponseEntity<String> peopleJson =
              restClient.exchange(pagedUrl, HttpMethod.GET, entity, String.class);

      String content = peopleJson.getBody();
      JSONArray people = JsonPath.read(content, "$.results[*]");

      List<String> names = new ArrayList<>();
      for (Object person : people) {
        String name = JsonPath.read(person, "$.name");
        names.add(name);
      }

      pagedUrl = JsonPath.read(content, "$.next");
      characterList.addAll(names);
    }

    characterList.stream()
        .sorted()
        .forEach(LOGGER::info);
  }
}
