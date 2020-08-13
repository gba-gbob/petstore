package org.gba.petstore;

import com.fasterxml.jackson.databind.type.TypeFactory;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.gba.petstore.domian.Pet;
import org.gba.petstore.domian.PetStatus;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class SearchSteps {
    private static final Map<Long, Response> searchResponses = new HashMap<>();
    private static final Type type = TypeFactory.defaultInstance().constructCollectionLikeType(ArrayList.class, Pet.class);

    /**
     * Nice to have optimisation to demonstrate custom parameter mapping
     */
    @ParameterType(".*")
    public int httpStatus(String statusName) {
        try {
            Field field = HttpStatus.class.getField(statusName);
            return field.getInt(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Unknown http status see HttpStatus.class for available status names", e);
        }
    }

    /**
     * Method allows enum bounded and string status choice to be used in bdd
     * In addition responses are thread bounded to allow parallel feature execution in case steps are used in multiple features.
     * While this is premature optimisation for such a small project it is implemented to facilitate parallel execution discussion.
     */

    @When("Bob searches for pets with status {string}")
    public void bob_searches_for_pets_with_status(String status) {
        try {
            searchResponses.put(Thread.currentThread().getId(), getPetsByStatus(PetStatus.valueOf(status)));
        } catch (IllegalArgumentException e) {
            searchResponses.put(Thread.currentThread().getId(), getPetsByStatus(status));
        }
    }

    @Then("Bob finds {long} pets with name {string} and status {string}")
    public void bob_finds_pets_with_name_and_status(long numPets, String name, String status) {
        synchronized (this) {
            Response searchResponse = searchResponses.get(Thread.currentThread().getId());
            assertEquals("Received unexpected response from api", HttpStatus.SC_OK, searchResponse.statusCode());
            List<Pet> pets = searchResponse.as(type);
            long petsWithName = pets.parallelStream().filter(it -> it.getName() != null && it.getName().equals(name)).count();
            assertEquals(String.format("Number of pets with name %s and status %s is not as expected", name, status),
                    numPets, petsWithName);
            if (pets.size() > 0) {
                assertThat(pets).extracting("status").contains(status);
            }
        }
    }

    @Then("Bob receives {httpStatus} response")
    public void bob_receives_response(int status) {
        synchronized (this) {
            Response searchResponse = searchResponses.get(Thread.currentThread().getId());
            assertEquals("Received unexpected response from api", status, searchResponse.statusCode());
        }
    }

    private Response getPetsByStatus(PetStatus petStatus) {
        return given().param("status", petStatus.toString()).when()
                .get("/pet/findByStatus").then().extract().response();
    }

    private Response getPetsByStatus(String petStatus) {
        return given().param("status", petStatus).when()
                .get("/pet/findByStatus").then().extract().response();
    }
}
