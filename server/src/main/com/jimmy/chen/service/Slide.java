package com.jimmy.chen.service;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

@JsonIgnoreProperties({"morphia","mongoClient","datastore"})
@Entity("slides")
public class Slide {
	private String title;
	private String imagePath;
	private String text;
	@Id
	private ObjectId _id;
	private boolean readMoreSlide;
	@Reference
	private String readMoreUrl;
	@Embedded
	private ArrayList<Link> links;
	MongoClient mongoClient = new MongoClient("localhost",27017);
	private Morphia morphia = new  Morphia();
	private Datastore datastore = morphia.createDatastore(mongoClient, "website");
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public ArrayList<Link> getLinks() {
		return links;
	}
	public void setLinks(ArrayList<Link> links) {
		this.links = links;
	}
	public ObjectId getId() {
		return _id;
	}
	public void setId(ObjectId id) {
		this._id = id;
	}
	public boolean isReadMoreSlide() {
		return readMoreSlide;
	}
	public void setReadMoreSlide(boolean readMoreSlide) {
		this.readMoreSlide = readMoreSlide;
	}
	public String getReadMoreUrl() {
		return readMoreUrl;
	}
	public void setReadMoreUrl(String readMoreUrl) {
		this.readMoreUrl = readMoreUrl;
	}
	public void processFile(File file) throws IOException{
		BufferedReader br= new BufferedReader(new FileReader(file));
		this.title=br.readLine();
		this.imagePath=br.readLine();
		this.text=br.readLine();
		this.links=new ArrayList<Link>();
		int counter=0;
		String url;
		String text;
		while((url=br.readLine())!=null && !url.equals("")){
			Link link = new Link();
			link.setUrl(url);
			text=br.readLine();
			link.setText(text);
			links.add(link);
		}
		if (links.size()==1 && links.get(0).getText().equals("Read More")){
			readMoreSlide=true;
			readMoreUrl=links.get(0).getUrl();
			links.remove(0);
		}
	}
	public String serialize() throws JsonProcessingException{
		ObjectMapper mapper= new ObjectMapper();
		return mapper.writeValueAsString(this);
	}
	public void serializeIntoFile(File serializeFile) throws IOException{
		ObjectMapper mapper = new ObjectMapper();
		mapper.writerWithDefaultPrettyPrinter().writeValue(serializeFile, this);
		
	}
	@Override
	public String toString() {
		return "Slide [title=" + title + ", imagePath=" + imagePath + ", text="
				+ text + ", links=" + links + "]";
	}
	public void insertIntoDb(File jsonFile) throws IOException{
		datastore.ensureIndexes();
		datastore.save(this);
	}
	
}
