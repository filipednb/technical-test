Development Process for the Hostfully Booking API

1. Data model:
   - I started thinking of all the entities we wanted to extract from the requirements, so I ended up with the following:
   ```sql
   -- Users table
   CREATE TABLE user (
      id Long PRIMARY KEY,
      type VARCHAR(50) NOT NULL,
      name VARCHAR(100) NOT NULL,
      email VARCHAR(100) NOT NULL UNIQUE
   );
   
   -- Properties
   CREATE TABLE property (
      id Long PRIMARY KEY,
      name VARCHAR(100) NOT NULL,
      owner_id Long NOT NULL,
      location VARCHAR(100) NOT NULL,
      FOREIGN KEY (owner_id) REFERENCES user(id)
   );
   
   -- Bookings
   CREATE TABLE booking (
      id Long PRIMARY KEY,
      guest_id Long NOT NULL,
      owner_id Long NOT NULL,
      property_id Long NOT NULL,
      check_in_date TIMESTAMP NOT NULL,
      check_out_date TIMESTAMP NOT NULL,
      status VARCHAR(50) NOT NULL,
      created_at TIMESTAMP NOT NULL,
      updated_at TIMESTAMP NOT NULL,
      FOREIGN KEY (guest_id) REFERENCES user(id),
      FOREIGN KEY (owner_id) REFERENCES user(id),
      FOREIGN KEY (property_id) REFERENCES property(id)
   );
   
   -- Block
   CREATE TABLE block (
      id Long PRIMARY KEY,
      property_id Long NOT NULL,
      start_date TIMESTAMP NOT NULL,
      end_date TIMESTAMP NOT NULL,
      FOREIGN KEY (property_id) REFERENCES property(id)
   );
   ```

2. Design and Architecture:

   With the db schema ready to go, I started to design the application architecture, ensuring it followed the principles of RESTful API*, defining the endpoints for each functionality and the relationships between entities such as User, Property, Booking, and Block. 
   
   **By the RESTful book, I wouldn't have action endpoints like rebook and cancel, but to facilitate the test I wanted to create them.**


4. Implementation:

   Initially I wrote most of the implementation in single giant files, so I started with Controllers + Services. Then I wanted to split, so, following a bit of DDD I created the domain folder, and then all the Resource, services, repositories, requests, responses etc... each related files in their own package.


5. Validation:

   I added validation to ensure data integrity, at least in the most external layer (Controller or Resource). This included using standard annotations for field validation and creating custom validators for requirements such as unique email addresses.


6. Exception Handling:

   I implemented global exception handling to provide meaningful error messages, and the right status code, for validation errors and other exceptions.


7. Testing:

   I wrote unit tests and integration tests to ensure the application functionality worked as expected. This included testing service methods, repository queries, and controller endpoints.


8. Documentation (TODO: Document requests and responses alongside status codes ):

   I documented the API using OpenAPI 3 (Swagger) to provide a clear and interactive way for users to understand and test the endpoints.
