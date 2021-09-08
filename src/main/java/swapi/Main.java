package swapi;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

public class Main {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {

	  

    RestTemplate restClient = new RestTemplate();

    List<String> characterList = new ArrayList<>();
    List<String> typeList = new ArrayList<>();
    
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
      List<String> tipos = new ArrayList<>();
      String valor="";
      for (Object person : people) {
        String name = JsonPath.read(person, "$.name");
        List<String> especies = JsonPath.read(person, "$.species");
        
        
        if(especies.size()>0) {
        	ResponseEntity<String> specieJson =
                    restClient.exchange(especies.get(0), HttpMethod.GET, entity, String.class);
        	
        	String contentEspecie = specieJson.getBody();
        	JSONParser parser = new JSONParser();  
        	JSONObject json = new JSONObject();
			try {
				json = (JSONObject) parser.parse(contentEspecie);
				valor = json.getAsString("name"); 
				tipos.add(valor);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	//String prueba = JsonPath.read(contentEspecie, "$.name");
            try {
                JSONObject jsonObject = new JSONObject();
           }catch (Exception err){
           }
        }else {
        	tipos.add("UNKNOWN");
        }
        
        names.add(name);
      }

      pagedUrl = JsonPath.read(content, "$.next");
      characterList.addAll(names);
      typeList.addAll(tipos);
    }

    /*characterList.stream()
        .sorted()
        .forEach(LOGGER::info);*/
    
    for (int i=0; i<typeList.size();i++) {
    	System.out.println(characterList.get(i).toString()+" - "+typeList.get(i).toString());
    }
  }
}
