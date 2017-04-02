/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.core.rest;

import com.model.Person;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 
 * @author Gonzalo H. Mendoza
 * email: yogonza524@gmail.com
 * StackOverflow: http://stackoverflow.com/users/5079517/gonza
 */
public class RestApp extends AbstractVerticle{

    //Model
    private static Map<String,Person> persons;
    
    @Override
    public void start(){
        
        //Create a datasource for persons
        persons = new HashMap<>();
        
        //Add some persons to datasource
        persons.put(UUID.randomUUID().toString(), new Person("Gonzalo","Mendoza",29));
        persons.put(UUID.randomUUID().toString(), new Person("Cristiano","Ronaldo",31));
        persons.put(UUID.randomUUID().toString(), new Person("Lionel","Messi",29));
        persons.put(UUID.randomUUID().toString(), new Person("Andres","Iniesta",34));
        
        //Create the router
        Router router = Router.router(Vertx.vertx());
        
        //Allow to get the body of HTTP request
        router.route().handler(BodyHandler.create());
        
        //Create endpoints
        router.get("/person").handler(this::all);
        
        router.get("/person/:id").handler(this::find);
        router.post("/person").handler(this::create);
        router.put("/person/:id").handler(this::update);
        router.delete("/person/:id").handler(this::delete);
        
        //Create the server
        Vertx.vertx().createHttpServer().requestHandler(router::accept).listen(8080);
        
    }
    
    //Endpoints handlers
    private void all(RoutingContext context){
        context.response()
                .setStatusCode(200)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encode(persons));
    }
    
    private void find(RoutingContext context){
        String id = context.request().getParam("id");
        
        Person found = persons.get(id);
        
        int code = 400;
        String leyend = "Person not found!";
        
        if (found != null) {
            code = 200;
        }
        
        context.response()
                .setStatusCode(code)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encode(found != null? found : new JsonObject().put("leyend", leyend)));
    }
    
    private void create(RoutingContext context){
        String val = context.getBodyAsString();
        
        Person found = Json.decodeValue(val, Person.class);
        
        int code = 400;
        String leyend = "Person not created!";
        
        if (found != null) {
            persons.put(UUID.randomUUID().toString(), found);
            code = 201;
        }
        
        context.response()
                .setStatusCode(code)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encode(found != null? new JsonObject().put("leyend", "Person created!!!") : new JsonObject().put("leyend", leyend)));
    }
    
    private void update(RoutingContext context){
        String id = context.request().getParam("id");
        String val = context.getBodyAsString();
        
        Person found = Json.decodeValue(val, Person.class);
        
        int code = 400;
        String leyend = "Person not updated!";
        
        if (found != null) {
            persons.put(id, found);
            code = 200;
            leyend = "Person updated!";
        }
        
        context.response()
                .setStatusCode(code)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encode(found != null? new JsonObject().put("leyend", leyend) : new JsonObject().put("leyend", leyend)));
    }
    
    private void delete(RoutingContext context){
        String id = context.request().getParam("id");
        
        Person removed = persons.remove(id);
        
        int code = 404;
        String leyend = "Person not removed!";
        
        if (removed != null) {
            code = 200;
            leyend = "Person removed!";
        }
        
        context.response()
                .setStatusCode(code)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encode(new JsonObject().put("leyend", leyend)));
    }
    
    public static void main(String[] args) {
        
        //Begin the application
        new RestApp().start();
    }
}
