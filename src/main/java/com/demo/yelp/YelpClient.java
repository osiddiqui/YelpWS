/*
 Example code based on code from Nicholas Smith at http://imnes.blogspot.com/2011/01/how-to-use-yelp-v2-from-java-including.html
 For a more complete example (how to integrate with GSON, etc) see the blog post above.
 */

package com.demo.yelp;


import java.io.IOException;
import java.util.Properties;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class YelpClient {

	OAuthService service;
	Token accessToken;

	/**
	 * Setup the Yelp API OAuth credentials.
	 *
	 * OAuth credentials are available from the developer site, under Manage API access (version 2 API).
	 *
	 * @param consumerKey Consumer key
	 * @param consumerSecret Consumer secret
	 * @param token Token
	 * @param tokenSecret Token secret
	 */
	public YelpClient(String consumerKey, String consumerSecret, String token, String tokenSecret) {
		this.service = new ServiceBuilder().provider(YelpApi2.class).apiKey(consumerKey).apiSecret(consumerSecret).build();
		this.accessToken = new Token(token, tokenSecret);
	}

	/**
	 * Search with term and location.
	 *
	 * @param term Search term
	 * @param latitude Latitude
	 * @param longitude Longitude
	 * @param limit how many results to return
	 * @return JSON string response
	 */
	public String search(String term, String zip, int limit) {
		OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
		request.addQuerystringParameter("term", term);
		request.addQuerystringParameter("location", zip);
		request.addQuerystringParameter("limit", String.valueOf(limit));
		this.service.signRequest(this.accessToken, request);
		Response response = request.send();
		return response.getBody();
	}

	public static void main(String[] args) {
		
		Properties credentials = new Properties();
		try {
		credentials.load(YelpClient.class.getClassLoader().getResourceAsStream("yelp-credentials.properties"));
	    } catch (IOException io) {
	    	System.err.println(io.getMessage());
	    }
		
		String consumerKey = credentials.getProperty("consumerKey");
		String consumerSecret = credentials.getProperty("consumerSecret");
		String token = credentials.getProperty("token");
		String tokenSecret = credentials.getProperty("tokenSecret");

		YelpClient yelp = new YelpClient(consumerKey, consumerSecret, token, tokenSecret);
		String response = yelp.search("Sushi", "20001", 1);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(response);
		
		System.out.print(json.toString());
		
		
	}



}
