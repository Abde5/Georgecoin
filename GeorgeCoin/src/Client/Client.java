package Client;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


public class Client {

  private String SERVER = "http://localhost:";
  private RestTemplate rest;
  private HttpHeaders headers;
  private ResponseEntity<String> responseEntity;
  private HttpEntity<String> requestEntity;

  public Client(int port) {
	SERVER += String.valueOf(port);  
	System.out.println(SERVER);
    this.rest = new RestTemplate();
    this.headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    headers.add("Accept", "*/*");
  }

	/**
	 * Function designed to make GET requests at a given URI
	 * @param uri get query
	 * @return response entity
	 */  
	public String get(String uri) {
	    requestEntity = new HttpEntity<String>("", headers);
	    setResponseEntity(uri, HttpMethod.GET);
	    return getEntityBody();
	}
	
	/**
	 * Function designed to make POST requests to a given URI
	 * @param uri post query
	 * @param json json query
	 * @return response entity
	 */
	public String post(String uri, String json) {   
	    requestEntity = new HttpEntity<String>(json, headers);
	    setResponseEntity(uri, HttpMethod.POST);
	    return getEntityBody();
	}
	
	/**
	 * Get entity body
	 * @return response entity body
	 */
	private String getEntityBody() {
		return responseEntity.getBody();
	}
	
	/**
	 * Set response entity
	 * @param method
	 * @param post
	 */
	private void setResponseEntity(String uri, HttpMethod post){
		responseEntity = rest.exchange(SERVER + uri, post, requestEntity, String.class);
	}
}